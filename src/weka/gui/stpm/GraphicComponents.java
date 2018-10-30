package weka.gui.stpm;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showMessageDialog;
import static weka.gui.stpm.StringUtil.*;
import static weka.gui.stpm.TrajectoryFrame.createTrajectoryTablesSelected;

class GraphicComponents extends JDialog {
    private final Connection conn;
    private final Config config;
    private Method[] algorithms;
    private AssociatedParameter poi_associated = null;
    private ArrayList<AssociatedParameter> rf_poi = new ArrayList<>();

    JComboBox<String> jComboBoxSchema;
    

    GraphicComponents(Connection conn, Config config, Method[] algorithms) {
        this.conn = conn;
        this.config = config;
        this.algorithms = algorithms;
    }

    void initGraphicComponents() {

        Container container = getContentPane();

        container.setLayout(new BorderLayout());
        
        jComboBoxSchema = new JComboBox<>();
        jComboBoxSchema.setPreferredSize(new Dimension(150, 22));
        
        //PANEL CONTENT
        JPanel panelContent = new JPanel();
        panelContent.setBorder(BorderFactory.createEtchedBorder());

        GridBagLayout gridbag = new GridBagLayout();
        panelContent.setLayout(gridbag);
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 5, 10);

        
        //Relevant Features
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 2;
        JLabel jLabel2 = new JLabel();
        jLabel2.setText("Relevant Features");
        panelContent.add(jLabel2, c);

        //Buffer
        GridBagConstraints c2 = new GridBagConstraints();
        c2.insets = new Insets(5, 5, 5, 5);

        c.gridx = 3;
        c.gridy = 2;
        c.gridheight = 2;
        c.gridwidth = 2;

        //Method Panel
        container.add(panelContent, BorderLayout.CENTER);

        JPanel panelDown = new JPanel();
        panelDown.setBorder(BorderFactory.createEtchedBorder());

        gridbag = new GridBagLayout();
        panelDown.setLayout(gridbag);
        c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 10, 10);

        JButton jButtonOK = new JButton("OK");
        jButtonOK.addActionListener(event -> OKActionPerformed());

        c.gridx = 3;
        c.gridy = 0;
        c.gridwidth = 2;
        panelDown.add(jButtonOK, c);

        JButton jButtonCancel = new JButton("Close");
        jButtonCancel.addActionListener(event -> jButtonCancelActionPerformed());
        c.gridx = 5;
        c.gridy = 0;
        c.gridwidth = 2;
        panelDown.add(jButtonCancel, c);

        container.add(panelDown, BorderLayout.SOUTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();
        jComboBoxSchema.requestFocusInWindow();
        this.setMaximumSize(new Dimension(600, 360));
        this.setSize(680, 360);
        this.setVisible(true);
    }

    private void jButtonCancelActionPerformed() {
        this.dispose();
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

        //controls if SRID of RFs are different from trajectories...
        // ALL the trajectories should have the SAME srid
        // it is checked ahead in the foreach.
        String error = checkSRIDs(); //att the variable 'table_srid'

        if(error.compareTo("")!=0){
        	JOptionPane.showMessageDialog(this,error);
            return;
        }

        //for each of the trajectory table selected...
//        Object[] selectedValues = jListRF.getSelectedValuesList().toArray(); // COMENTAR DEPOIS (URGENTE ESSE E O DEBAIXO!)
        Object[] selectedValues = rf_poi.toArray();
//        Object[] selectedValues = new Object[]{poi_associated}; // DESCOMENTAR DEPOIS
        Method method = null;
        if(config.method.equals("SMoT")){
        	method = (Method) algorithms[0];
        }else{
        	method = (Method) algorithms[1];
        }
        Boolean enableBuffer = true; // Always True, isso interfere com base na área, sempre é bom ter uma área ao redor do POI
        int maxSelectedIndex = jListRF.getMaxSelectionIndex();
        
        try {
	        long time = createTrajectoryTablesSelected(config.table, config.tid, config.time, selectedValues, config.userBuff, method, enableBuffer,
	                        config, config.ftype, maxSelectedIndex);
	        System.out.println("Processing time: " + time + " ms");
	        showMessageDialog(this, "Operation finished successfully.");
        } catch (Exception e) {
        	System.out.println("Error: \n" + e.getMessage());
            showMessageDialog(this, "Error during operation");
        }
        Runtime.getRuntime().gc();
    }

    @SuppressWarnings("unchecked")
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
//            java.util.List<AssociatedParameter> objects = jListRF.getSelectedValuesList(); // COMENTAR DEPOIS
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
