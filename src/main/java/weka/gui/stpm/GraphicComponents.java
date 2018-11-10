package weka.gui.stpm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static weka.gui.stpm.TrajectoryFrame.createTrajectoryTablesSelected;

class GraphicComponents {
    private final Connection conn;
    private final Config config;
    private Method[] algorithms;
    private AssociatedParameter poi_associated = null;
    private ArrayList<AssociatedParameter> rf_poi = new ArrayList<>();

    GraphicComponents(Connection conn, Config config, Method[] algorithms) {
        this.conn = conn;
        this.config = config;
        this.algorithms = algorithms;
    }

    void initGraphicComponents() {
    	OKActionPerformed();
    }

    public void LoadActionPerformed() {
        try { //load the tables in a list of auxiliary strings
            Statement s = conn.createStatement();
            final String sql = "SELECT f_table_name as tableName, type FROM geometry_columns " +
                    "WHERE f_table_schema=trim('"+ config.schema + "') AND f_table_name = '"+ config.poi +"' ORDER BY tableName";

            ResultSet vTableName = s.executeQuery(sql);
            while (vTableName.next()) {/* creates a new table for each table that has objects with topological relation to vRegion */
                rf_poi.add(new AssociatedParameter(vTableName.getString("tableName"), vTableName.getString("type"), config.rfMinTime));// RFs          
             	poi_associated = new AssociatedParameter(vTableName.getString("tableName"), vTableName.getString("type"), config.rfMinTime);
            }
        } catch (Exception vErro) {
            vErro.printStackTrace();
        }
    }

    private void OKActionPerformed() {
    	System.out.println("Buffer of "+config.userBuff+" saved.");	

        String error = checkSRIDs(); //att the variable 'table_srid'

        if(error.compareTo("")!=0){
        	System.out.println(error);
            return;
        }

        //for each of the trajectory table selected...
        Object[] selectedValues = rf_poi.toArray();
        Method method = null;
        if(config.method.equals("SMoT")){
        	method = (Method) algorithms[0];
        }else{
        	method = (Method) algorithms[1];
        }
        Boolean enableBuffer = true; // Always True, isso interfere com base na área, sempre é bom ter uma área ao redor do POI
        int maxSelectedIndex = 1;
        
        try {
	        long time = createTrajectoryTablesSelected(config.table, config.tid, config.time, selectedValues, config.userBuff, method, enableBuffer,
	                        config, config.ftype, maxSelectedIndex);
	        System.out.println("Processing time: " + time + " ms");
	        System.out.println("Operation finished successfully.");
	        System.exit(0);
        } catch (Exception e) {
        	System.out.println("Error during operation: \n" + e.getMessage());
        }
        Runtime.getRuntime().gc();
    }

    private String checkSRIDs() {
        Statement sn;
        try {
            sn = conn.createStatement();

            final String param = "srid";
            final String sql = String.format("select %s from geometry_columns where f_table_name='%s'", param, config.table);

            ResultSet rsn = sn.executeQuery(sql);

            rsn.next();
            TrajectoryFrame.table_srid = rsn.getInt(param);
 
            java.util.List<AssociatedParameter> objects = new ArrayList<AssociatedParameter>(); // DESCOMENTAR DEPOIS
            objects.add(poi_associated); // DESCOMENTAR DEPOIS
            java.util.List<AssociatedParameter> relevantFeatures = new ArrayList<>(objects);

            //comparing their SRIDs with the trajectory
            for (AssociatedParameter a : relevantFeatures) {
                sn = conn.createStatement();
                rsn = sn.executeQuery(sql);
                rsn.next();

                if (TrajectoryFrame.table_srid != rsn.getInt(param)) {
                    return "Error in the SRID of table: " + a.name;
                }
            }
        } catch (SQLException e) {
        	System.out.println(e.getMessage());
        }
        return "";
    }
}
