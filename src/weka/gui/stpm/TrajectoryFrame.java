package weka.gui.stpm;

import weka.gui.stpm.clean.TrajectoryClean;
import weka.gui.stpm.clean.Util;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import static weka.gui.stpm.Constants.*;
import static weka.gui.stpm.Parameter.Type.DOUBLE;
import static weka.gui.stpm.StringUtil.isEmpty;

public class TrajectoryFrame extends JDialog {
    /**
     * Spatial reference for ALL trajectory_table
     */
    static Integer table_srid; //spatial reference ID,google it
    private static Connection conn;
    private static String currentNameTableStop;
    private static IBSMoT IB_SMoT_RUN = new IBSMoT();
    private static CBSMoT CB_SMoT_RUN = new CBSMoT();
    private Method[] algorithms;
    private Config userConfigurations;
    private GraphicComponents graphicComponents;


    public TrajectoryFrame(String user, String pass, String url) {
        this.userConfigurations = new Config();
        this.setTitle("Trajectory");
        initAlgorithms();

        try {
            loadPropertiesFromFile(user, pass, url);

            this.graphicComponents = new GraphicComponents(conn, userConfigurations, algorithms);
            this.graphicComponents.initGraphicComponents();
            this.graphicComponents.LoadActionPerformed();
        } catch (Exception e) {
            System.out.println(e.toString());
            JOptionPane.showMessageDialog(this, "Error in connection with DB.");
            dispose();
        }
    }

    // TID dos taxis and ur points
    private static void loadTrajectories(String trajectoryTable, String tid, String time, Object[] objects, Double buffer, Method method, boolean enableBuffer,
                                         Config userConfigurations, boolean enableFType, int maxSelectedIndex) throws SQLException {

        InterceptsG i;

        Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        // selects the trajectory-tid to be processed
        String sql = "SELECT " + tid + " as tid, count(*) FROM " + trajectoryTable + " GROUP BY " + tid + " ORDER BY tid DESC";
        ResultSet rs = s.executeQuery(sql);

        System.out.println("Creating interceptions...");
        i = createIntercepts(objects, buffer, trajectoryTable, enableBuffer);
        System.out.println("Interceptions created ");

        File speedFile = null;
        if (TrajectoryClean.isPrintSpeedToFileXls()) {
            speedFile = Util.getFileSpeed(TrajectoryFrame.getCurrentNameTableStop());
        }

        // Add hash index on tid if it doesn't exists to boost perf
        Statement s0a = conn.createStatement();
        String sql0a = "with x as (select oid from pg_class where relname = '" + trajectoryTable + "') " +
                "select 1 from (select attnum from pg_attribute where attrelid IN (select * from x) and attname = '" + tid + "') a" +
                "inner join (select indkey from pg_index where indrelid IN (select * from x)) b on attnum = ALL(indkey);";
        ResultSet rs0 = s0a.executeQuery(sql0a);
        boolean indexTimeExists;
        indexTimeExists = rs0.next();
        s0a.close();

        // Change of methods is here
        if (!indexTimeExists){
        	System.out.println("Creating Index on " + tid);
            Statement s0b = conn.createStatement();
            String sql0b = "CREATE INDEX ON " + trajectoryTable + " USING hash (" + tid + ") WITH (FILLFACTOR=100);";
            s0b.execute(sql0b);
            s0b.close();
        } else {
        	System.out.println("Tid index on " + trajectoryTable + " exists. Performance will be good.");
        }

        //for each trajectory... (select que pega os tid dos taxistas e ordena de forma decrescente)
        while (rs.next()) {
            Trajectory trajectory = new Trajectory(table_srid);
            trajectory.tid = rs.getInt("tid");
            String meth = method.toString();
            if (!meth.startsWith("SMoT")) {
                //select the points of the trajectory in sequential time
                Statement s1 = conn.createStatement();
                String sql2 = "SELECT " + time + " as time,the_geom,gid FROM " + trajectoryTable +
                        " WHERE " + tid + "=" + trajectory.tid + " ORDER BY " + time;
 
                //ordered by time
                ResultSet rs2 = s1.executeQuery(sql2);

                int timeIndex = 0;

                // info of the each point will be use in gps format
                while (rs2.next()) {
                    Timestamp t = rs2.getTimestamp("time");
                    org.postgis.PGgeometry geom = (org.postgis.PGgeometry) rs2.getObject("the_geom");
                    org.postgis.Point p = (org.postgis.Point) geom.getGeometry();
                    //get the time, the_geom and tid columns to fill the Vector
                    GPSPoint gps = new GPSPoint(trajectory.tid, t, p, timeIndex++);

                    gps.gid = rs2.getInt("gid");
                    trajectory.points.addElement(gps);
                }

                // calculates the speed of each point, and then runs the method
                if (trajectory.points.size() > 5) {

                    trajectory.calculatePointsSpeed();

                    if (TrajectoryClean.isPrintSpeedToFileXls()) {
                        Util.imprimeVelocidades(trajectory.points, speedFile, rs.isFirst());
                    }
                    // Run do CB
                    CB_SMoT_RUN.setInformation(buffer, userConfigurations, enableBuffer, enableFType, table_srid, maxSelectedIndex);
                    CB_SMoT_RUN.run(trajectory, i, trajectoryTable);
                } else {
                	System.out.println("Trajectory " + trajectory.tid + " has less than 5 points.");
                }

            }
            // Run do IB
            else{
                IB_SMoT_RUN.setInformation(buffer, userConfigurations, enableBuffer, enableFType);
                IB_SMoT_RUN.run(trajectory, i, trajectoryTable);
            }
        }
        TrajectoryMethods.resetunknown();
        enriquecimentoSemanticoStopComplete(userConfigurations);
    }
    
    // TrajectoryTable e a con
    public static void enriquecimentoSemanticoStopComplete(Config userConfigurations){
    	Statement s2;
    	String sql_enriquecimento_stop="";
		try {
			s2 = conn.createStatement();
			sql_enriquecimento_stop = "INSERT INTO complete_" + TrajectoryFrame.getCurrentNameTableStop() + " select m.*, c.gid_stop " + 
								"from con_" + TrajectoryFrame.getCurrentNameTableStop() + " as c, " + userConfigurations.table + " as m where m.gid = c.gid_point";
	           	s2.execute(sql_enriquecimento_stop);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public static List<Trajectory> getTrajectoriesWithSpeeds(String tableTraj, Config config, Integer table_srid) throws SQLException {
        List<Trajectory> trajectorys = new ArrayList<Trajectory>();
        Statement s = config.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        // selects the trajectory-tid to be processed
        String sql = "SELECT "+config.tid+" as tid, count(*) FROM "+tableTraj+" GROUP BY "+config.tid+" ORDER BY tid DESC";
        ResultSet rs = s.executeQuery(sql);
        
        //for each trajectory...
        while (rs.next()) {
            Trajectory trajectory = null;
            if(table_srid != null){
                trajectory = new Trajectory(table_srid);
            }else{
                trajectory = new Trajectory();
            }
            trajectory.tid = rs.getInt("tid");

            //select the points of the trajectory in sequential time
            Statement s1 = config.conn.createStatement();
            String sql2 = "SELECT "+config.time+" as time,the_geom,gid FROM "+tableTraj+" WHERE "+config.tid+"="+trajectory.tid+" ORDER BY "+config.time;

            ResultSet rs2 = s1.executeQuery(sql2);
            //for each of these points of the trajectory...
            int timeIndex = 0;
            while (rs2.next()) {
                Timestamp t = rs2.getTimestamp("time");
                org.postgis.PGgeometry geom = (org.postgis.PGgeometry) rs2.getObject("the_geom");
                org.postgis.Point p = (org.postgis.Point) geom.getGeometry();
                //get the time, the_geom and tid columns to fill the Vector
                GPSPoint gps = new GPSPoint(trajectory.tid,t,p,timeIndex);
                gps.gid = rs2.getInt("gid");
                trajectory.points.addElement(gps);
                timeIndex++;

            }
//            System.out.println("calculando velocidade da trajetoria "+trajectory.tid);
            //calculates the speed of each point, and then runs the method
            if (trajectory.points.size()>5){
                trajectory.calculatePointsSpeed();
            }
            trajectorys.add(trajectory);
            System.out.println("add trajetoria " + trajectory.tid);
            System.gc();
        }
        return trajectorys;
    }

    public static long createTrajectoryTablesSelected(String tableName, String tid, String dateTime, Object[] objects, Double buffer,
                                                      Method method, boolean enableBuffer, Config userConfigurations, boolean enableFType,
                                                      int maxSelectedIndex) throws SQLException {

        long initialTime = System.currentTimeMillis();

        //trajectory srid checking, has to be the same of all the other trajectory-tables and RFs
        Statement sn = conn.createStatement();
        ResultSet rsn = sn.executeQuery("select srid from geometry_columns where f_table_name='" + tableName + "'");
        rsn.next();

        if (table_srid != rsn.getInt("srid")) {
            throw new SQLException("SRID incompatibilities. Trajectory table " + tableName + " should be changed.");
        }

        //end of srid checking
        System.out.println("Creating tables...");

        createTables(method);

        System.out.println("Processing the trajectories...");

        loadTrajectories(tableName, tid, dateTime, objects, buffer, method, enableBuffer, userConfigurations, enableFType, maxSelectedIndex);

        long finalTime = System.currentTimeMillis();

        return finalTime - initialTime;
    }

    private static void createTables(Method method) throws SQLException {
        Statement s = conn.createStatement();
        TrajectoryFrame.setCurrentNameTableStop(method);
        
        //Table completa com os novos enriquecimentos semânticos de stops (APENAS)
        try {
            s.execute("DROP TABLE IF EXISTS " + "complete_" + TrajectoryFrame.getCurrentNameTableStop());
            s.execute("DELETE FROM geometry_columns WHERE f_table_name = '"+"complete_"+TrajectoryFrame.getCurrentNameTableStop()+"'");

        }catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            s.execute(
                    "CREATE TABLE IF NOT EXISTS " + "complete_" + TrajectoryFrame.getCurrentNameTableStop() + " (" +
                            "    tid integer NOT NULL," +
                            "    latitude double precision," +
                            "    longitude double precision," +
                            "    \"time\" timestamp without time zone," +
                            "    edge_id bigint," +
                            "    \"offset\" double precision," +
                            "    gid integer NOT NULL," +
                            "    the_geom geometry," +
                            "	 gid_stop integer"	+
                            ") WITHOUT OIDS;"  
            );
        }
        
        //Conexão Intermediária entre stops e os pontos normais
        try {
            s.execute("DROP TABLE IF EXISTS " + "con_" + TrajectoryFrame.getCurrentNameTableStop());

        }catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            s.execute(
                    "CREATE TABLE IF NOT EXISTS " + "con_" + TrajectoryFrame.getCurrentNameTableStop() + " (" +
                            "    gid_point integer," +
                            "    gid_stop integer" +
                            ");"
            );
        }        
        
        // STOPS table
        System.out.println("\t\tstops table...");
        try {
            s.execute("DROP TABLE IF EXISTS "+TrajectoryFrame.getCurrentNameTableStop());
            s.execute("DELETE FROM geometry_columns WHERE f_table_name = '"+TrajectoryFrame.getCurrentNameTableStop()+"'");

        }catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            s.execute(
                    "CREATE TABLE IF NOT EXISTS " + TrajectoryFrame.getCurrentNameTableStop() + " (" +
                            "    gid serial NOT NULL," +
                            "    tid integer NOT NULL," +
                            "    stopid integer NOT NULL," +
                            "    stop_gid character varying," +
                            "    stop_name character varying," +
                            "    start_time timestamp without time zone," +
                            "    end_time timestamp without time zone," +
                            "    rf character varying," +
                            "    avg real" +
//                            "    CONSTRAINT " + TrajectoryFrame.getCurrentNameTableStop() + "_gidkey PRIMARY KEY (gid)" +
                            ") WITHOUT OIDS;"
            );

            s.execute("SELECT AddGeometryColumn('" + TrajectoryFrame.getCurrentNameTableStop() + "', 'the_geom'," + table_srid + ", 'MULTIPOLYGON', 2)");
        }

        //MOVES table (NOT USED YET)
        System.out.println("\t\tmoves table...");
        try {
            s.execute("DROP TABLE IF EXISTS moves");
            s.execute("DELETE FROM geometry_columns WHERE f_table_name = 'moves'");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }finally {
            s.execute(
                    "CREATE TABLE IF NOT EXISTS moves (" +
                            "      tid integer NOT NULL," +
                            "      moveid integer NOT NULL," +
                            "      start_time timestamp without time zone," +
                            "      end_time timestamp without time zone," +
                            "      start_stop character varying," +
                            "      end_stop character varying," +
                            "      start_stop_pk integer," +
                            "      end_stop_pk integer," +
                            "      rf character varying," +
                            "      CONSTRAINT moves_pkey PRIMARY KEY (tid,rf,moveid)" +
                            ")WITHOUT OIDS"
            );
            s.execute("SELECT AddGeometryColumn('moves', 'the_geom',"+table_srid+", 'LINESTRING', 2)");
        }

    }

    // Creating Interest Points
    private static InterceptsG createIntercepts(Object[] objects, Double buffer, String tableName, Boolean enableBuffer) throws SQLException {
        //get the RFs from panel...

        AssociatedParameter[] relevantFeatures = new AssociatedParameter[objects.length];
        for (int i = 0; i < objects.length; i++) {
            relevantFeatures[i] = (AssociatedParameter) objects[i];
        }
        //for each rf, execute the query and save, in main memory, the results
        Statement s = conn.createStatement();
        InterceptsG intercs = new InterceptsG();
        for(AssociatedParameter a : relevantFeatures){
            // Create a table of registers with:
            // pt -> gid of the trajectory point
            // gid -> gid from RF wich intercept it
            // rf -> rf_name

            java.util.Date tempo2, fim2, ini2 = new java.util.Date();
            System.out.println("\t\t...with " + a.name);
            
            String sql;
            if (a.type.contains("POINT") || a.type.contains("LINE")) {// if any kind of POINT or LINE
                try {
                    s.execute("DROP TABLE "+a.name+"_buf;");
                } catch (SQLException ex) {
                    // do nothing
                } finally {
                    s.execute("create table " + a.name +  "_buf as select gid, amenity, ST_Buffer(the_geom::geography," +
                            buffer + ")::geometry as the_geom from " + a.name + ";"
                    );
                    s.execute("alter table " + a.name + "_buf add constraint " + a.name + "_buf_pk primary key (gid);");
                }
                sql = ("select A.gid as pt, B.gid as gid, '" + a.name + "' as rf, B.amenity as amenity " +
                        "from " + tableName + " A," + a.name + "_buf B " +  "where st_intersects(A.the_geom,B.the_geom);");
            } else {
                if (enableBuffer) { // if user sets buffer
                    try {
                        s.execute("DROP TABLE " + a.name + "_buf");
                    } catch (SQLException ex) {
                        // do nothing
                    } finally {
                        s.execute("CREATE TABLE " +  a.name + "_buf AS SELECT gid, amenity, ST_Buffer(the_geom::geography," +
                                buffer +")::geometry AS the_geom FROM " + a.name + ";"
                        );
                        s.execute("ALTER TABLE " + a.name + "_buf ADD CONSTRAINT " + a.name + "_buf_pk PRIMARY KEY (gid);");
                    }
                    sql = "select A.gid as pt, B.gid as gid, '" + a.name + "' as rf, B.amenity as amenity " +
                            "from " + tableName + " A, " + a.name + "_buf B " +
                            "where st_intersects(A.the_geom,B.the_geom);";
                } else {
                    sql = "select A.gid as pt, B.gid as gid, '" + a.name + "' as rf, B.amenity as amenity " +
                            "from " + tableName + " A, " + a.name + " B " +
                            "where st_intersects(A.the_geom,B.the_geom);";
                }

            }
            ResultSet Intercep = s.executeQuery(sql);
            fim2 = new java.util.Date();
            tempo2 = new java.util.Date(fim2.getTime()-ini2.getTime());
            System.out.println("\t\t"+a.name+" time: " +tempo2.getTime()+" ms");
            // then, save the registers from the query in an adequate struct

            while (Intercep.next()) {
                Interc i = new Interc(Intercep.getInt("pt"), Intercep.getInt("gid"), Intercep.getString("rf"), a.value.intValue(), Intercep.getString("amenity"));
                intercs.addpt(i);
            }
        }

        return intercs;
    }

    public static String getCurrentNameTableStop() {
        return isEmpty(currentNameTableStop) ? "stops" : currentNameTableStop;
    }

    private void loadPropertiesFromFile(String userFromInput, String passFromInput, String urlFromInput) throws SQLException {
        Properties properties = new Properties();

        try {
            properties.load(ClassLoader.getSystemResourceAsStream(CONFIG_PROPERTIES));


            String user = (userFromInput != null) ? userFromInput : properties.getProperty(DB_USER);
            String pass = (userFromInput != null) ? passFromInput : properties.getProperty(DB_PASS);
            String url = (userFromInput != null) ? urlFromInput :
                    properties.getProperty(DB_URL) + properties.getProperty(DB_NAME);

            if (user.equals(VOID)) conn = DriverManager.getConnection(url);
            else conn = DriverManager.getConnection(url, user, pass);

            ((org.postgresql.PGConnection) conn).addDataType(DB_TYPE_GEOMETRY, org.postgis.PGgeometry.class);

            userConfigurations.conn = conn;
            userConfigurations.table = properties.getProperty(TRAJECTORY_TABLE);
            userConfigurations.tid = properties.getProperty(TRAJECTORY_ID);
            userConfigurations.time = properties.getProperty(DETECTION_TIME);
            userConfigurations.poi = properties.getProperty(POINTS_INTEREST);
            userConfigurations.schema = properties.getProperty(SCHEMA);
            userConfigurations.userBuff = Double.parseDouble(properties.getProperty(USERBUFF));
            userConfigurations.rfMinTime = Double.parseDouble(properties.getProperty(RFMINTIME));
            userConfigurations.method = properties.getProperty(METHOD);
            userConfigurations.maxAvgSpeed = Double.parseDouble(properties.getProperty(MAXAVGSPEED));
            userConfigurations.minTime = Integer.parseInt(properties.getProperty(MINTIME));
            userConfigurations.maxSpeed = Double.parseDouble(properties.getProperty(MAXSPEED));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void initAlgorithms() {
        algorithms = new Method[2];
        int i = 0;

        algorithms[i] = IB_SMoT_RUN;
        algorithms[++i] = CB_SMoT_RUN;

        algorithms[i].param.add(new Parameter("MaxAvgSpeed", DOUBLE, userConfigurations.maxAvgSpeed));
        algorithms[i].param.add(new Parameter("MinTime (seconds)", Parameter.Type.INT, userConfigurations.minTime));
        algorithms[i].param.add(new Parameter("MaxSpeed", DOUBLE, userConfigurations.maxSpeed));
    }

    private static void setCurrentNameTableStop(Method method) {
    	String meth = method.toString();
    	Scanner s = new Scanner(System.in);
    	System.out.println("--> Escolha um nome para a tabela de stops: ");
    	String choosedName = "stops_" + s.nextLine();
        if(meth.startsWith("SMoT")) {
        	TrajectoryFrame.currentNameTableStop = "ib_" + choosedName;
        }else {
        	TrajectoryFrame.currentNameTableStop = "cb_" + choosedName;
        }
    }
}