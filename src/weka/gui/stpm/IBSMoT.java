package weka.gui.stpm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static weka.gui.stpm.TrajectoryMethods.saveStopsAndMoves2;

public class IBSMoT extends Method {
    private Double buffer;
    private Config config;
    private boolean enablePreProcessByTypeOrInstanceGranularity;


    void setInformation(Double buffer, Config config, boolean enablePreProcessByTypeOrInstanceGranularity) {
        this.buffer = buffer;
        this.config = config;
        this.enablePreProcessByTypeOrInstanceGranularity = enablePreProcessByTypeOrInstanceGranularity;
    }

    @Override
    public void run(Trajectory t, InterceptsG in, String targetFeature) {
        LOG.info("Processing trajectory " + t.tid);

        try {
            List<Stop> stops = iBSMoT(config, t, in);

            // Remove from here
            saveStopsAndMoves2(stops,config.conn,enablePreProcessByTypeOrInstanceGranularity,buffer,0);
        } catch (SQLException e) {
            LOG.severe("Error to execute IBSMoT. " + e.getMessage());
        }
    }

    /** Finds the stops in a trajectory.
     *
     * @param config			User configurations.
     * @param t					The trajectory being analized.
     * @param intercepts		The Table of Geometry Relations (Intercepts Table) to be used.
     * @throws SQLException		If any table didn't exist in the BD, or a field.
     */
    private static List<Stop> iBSMoT(Config config, Trajectory t, InterceptsG intercepts) throws SQLException {
        int gidaux,serial_gid=0;
        org.postgis.PGgeometry geom;
        Statement s=config.conn.createStatement();
        String sql = "SELECT " + config.tid + ",gid," + config.time + ",the_geom FROM " + config.table + " WHERE " + config.tid +" = " + t.tid + " ORDER BY TIME;";
        ResultSet rs = s.executeQuery(sql);

        List<Stop> allStops = new ArrayList<>();

        boolean first = true;
        Stop st = new Stop();
        int gidRelevantFeature = -1;

        while(rs.next()){

            //creates the point in the BD
            GPSPoint pt = new GPSPoint();
            pt.tid=rs.getInt("tid");
            pt.gid=rs.getInt("gid");
            pt.time=rs.getTimestamp(config.time);

            geom = (org.postgis.PGgeometry) rs.getObject("the_geom");
            pt.point = (org.postgis.Point) geom.getGeometry();

            //get the actual gid/time variables to be tested
            gidaux=pt.gid;
            if(!first){//the others not-the-first need to control if the gid is or not sequential
                //before it, there's an Interc ?
                Interc rf=intercepts.is_in(pt.gid);
                if((rf!=null) && (rf.gid == gidRelevantFeature) /*&& (rf.rf == nameRelevantFeature)*/) {//yes, it intercepts something, so...
                    //... we have to test that sequential control, then...
                    if(gidaux - serial_gid <= 1){ // the diference between gid's can't exceed 1
                        st.amenity = rf.amenity;
                        st.addPoint(pt);
                    }
                }
                else {//no, it didn't have an Interc, so...
                    if (st.check()) { //tests the actual stop
                        allStops.add(st);
                    }
                    st = new Stop();//creates a new stop
                    if (rf != null) { //it is the first of another relevant feature in the same trajectory
                        st.amenity = rf.amenity;
                        st.addPoint(pt,rf.rf,rf.value,rf.gid); // saves the enterTime
                        first=false;
                        gidRelevantFeature = rf.gid;
                    }
                    else {
                        first = true;
                    }
                }
            }
            else{//being the first, there's no need to tests, so...
                Interc rf=intercepts.is_in(pt.gid);
                if(rf!=null){//...tests only if there's an intercs associated and add to the stop
                    st.amenity = rf.amenity;
                    st.addPoint(pt,rf.rf,rf.value,rf.gid); // saves the enterTime
                    first=false;
                    gidRelevantFeature = rf.gid;
                }
                // in case of no Interc at all, we can continue normally.
            }
            //refresh the values of the serial_variables
            serial_gid=gidaux;
        }
        if ((!first) && (st.check())) {
            allStops.add(st);
        }

        rs.close();

        return allStops;
    }

    private static Logger LOG = Logger.getLogger(IBSMoT.class.getName());
}
