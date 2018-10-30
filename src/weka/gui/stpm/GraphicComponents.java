package weka.gui.stpm;

import weka.gui.WekaTaskMonitor;
import weka.gui.geodata.visualizer.ShowGeoData;
import weka.gui.stpm.clean.TrajectoryClean;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static javax.swing.JOptionPane.showMessageDialog;
import static weka.gui.stpm.Parameter.Type.DOUBLE;
import static weka.gui.stpm.StringUtil.*;
import static weka.gui.stpm.TrajectoryFrame.createTrajectoryTablesSelected;

class GraphicComponents extends JDialog {
    private final Connection conn;
    private final Config config;
    private Method[] algorithms;

    private final WekaTaskMonitor tm = new WekaTaskMonitor();
    javax.swing.JComboBox jComboBoxStreet;
    javax.swing.JComboBox jComboBoxStreetLimit;
    javax.swing.JList jListRF;
    javax.swing.JTextField RFMinTime;
    JComboBox<String> jComboBoxSchema;
    javax.swing.ButtonGroup buttonGroupItem;// the same
    javax.swing.ButtonGroup buttonGroupTime;//the same
    private javax.swing.JList jListTrajectoryTables;
    private AssociatedParameter poi_associated = null;

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

        //Config of trajectory Table
        c.gridx = 0;
        c.gridy = 0;
        JLabel jLabel1 = new JLabel("Trajectory Table: ");
        panelContent.add(jLabel1, c);

        c.gridx = 2;
        c.gridwidth = 2;
        c.weightx = 1.0;
        JScrollPane sc1 = new JScrollPane();
        DefaultListModel modelsc = new DefaultListModel();
        jListTrajectoryTables = new JList(modelsc);
        jListTrajectoryTables.setVisibleRowCount(2);
        jListTrajectoryTables.setFixedCellWidth(2);
        sc1.setViewportView(jListTrajectoryTables);
        panelContent.add(sc1, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 5;
        c.gridheight = 1;

        //Relevant Features
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 2;
        JLabel jLabel2 = new JLabel();
        jLabel2.setText("Relevant Features");
        panelContent.add(jLabel2, c);

        c.gridy = 3;
        c.gridwidth = 3;
        c.weightx = 1.0;
        JScrollPane jScrollPane1 = new JScrollPane();
        DefaultListModel modelRF = new DefaultListModel();
        jListRF = new JList(modelRF);
        jListRF.setVisibleRowCount(4);
        jListRF.setFixedCellWidth(4);
        jListRF.addListSelectionListener(event -> jListRFValueChanged());
        jScrollPane1.setViewportView(jListRF);
        panelContent.add(jScrollPane1, c);
        validate();

        //Buffer
        GridBagLayout gbag = new GridBagLayout();
        GridBagConstraints c2 = new GridBagConstraints();
        c2.insets = new Insets(5, 5, 5, 5);

        c.gridx = 3;
        c.gridy = 2;
        c.gridheight = 2;
        c.gridwidth = 2;

        //MinTimeBox
        JPanel mtPanel = new JPanel();
        mtPanel.setBorder(BorderFactory.createEtchedBorder());
        gbag = new GridBagLayout();
        mtPanel.setLayout(gbag);
        c2 = new GridBagConstraints();

        c2.gridx = 0;
        c2.gridy = 0;
        JLabel jLabel3 = new JLabel("RF Min Time (sec): ");
        mtPanel.add(jLabel3, c2);

        c2.gridx = 0;
        c2.gridy = 1;
        RFMinTime = new JTextField();
        RFMinTime.setPreferredSize(new Dimension(40, 20));
        RFMinTime.setColumns(6);
        RFMinTime.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                RFMinTimeFocusLost();
            }
        });

        RFMinTime.addActionListener(event -> RFMinTimeActionPerformed());
        mtPanel.add(RFMinTime, c2);

        c.gridx = 3;
        c.gridy = 4;
        panelContent.add(mtPanel, c);

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

        c.gridx = 9;
        c.gridy = 0;
        panelDown.add(tm, c);

        container.add(panelDown, BorderLayout.SOUTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();
        jComboBoxSchema.requestFocusInWindow();
        this.setMaximumSize(new Dimension(600, 360));
        this.setSize(680, 360);
        this.setVisible(true);
    }


    private void configureActionPerformed(ActionEvent e) {
        int[] i = jListTrajectoryTables.getSelectedIndices();
        if (i.length == 1) {
//            Object[] temp = jListTrajectoryTables.getSelectedValuesList().toArray(); // COMENTAR DEPOIS
//            config.table = (String) temp[0]; // COMENTAR DEPOIS
            config.conn = conn;
            TrajectoryConfig tc = new TrajectoryConfig();
            tc.setConfig(config);
            tc.setVisible(true);
        } else {
            showMessageDialog(this, "Select only one Trajectory Table.");
        }
    }

    private void jButtonCancelActionPerformed() {
        this.dispose();
    }

    // Add valor para o RFMinTime ao select Relevant Features
    private void jListRFValueChanged() {
//        AssociatedParameter par = (AssociatedParameter) jListRF.getSelectedValue(); // COMENTAR DEPOIS
    	AssociatedParameter par = poi_associated; // DESCOMENTAR DEPOIS
        if (par != null)
            RFMinTime.setText(par.value.toString());
    }

    // Box do RFMinTime
    private void RFMinTimeFocusLost() {
        Object[] objs = jListRF.getSelectedValuesList().toArray();

        associateValuesFromRFMinTime(objs);
    }

    private void RFMinTimeActionPerformed() {
        Object[] objs = jListRF.getSelectedValuesList().toArray();

        associateValuesFromRFMinTime(objs);
    }

    private void associateValuesFromRFMinTime(Object[] objs) {
        try {
            for (Object obj : objs) {
                AssociatedParameter p = (AssociatedParameter) obj;
                p.value = Integer.parseInt(RFMinTime.getText());
            }
        } catch (java.lang.NumberFormatException e) {
            showMessageDialog(this, e.toString());
            RFMinTime.grabFocus();
        }
    }

    public void LoadActionPerformed() {
        try { //load the tables in a list of auxiliary strings
            Statement s = conn.createStatement();
            final String sql = "SELECT f_table_name as tableName,type FROM geometry_columns " +
                    "WHERE f_table_schema=trim('"+ config.schema + "') ORDER BY tableName";

            ResultSet vTableName = s.executeQuery(sql);
            DefaultListModel model = (DefaultListModel) jListTrajectoryTables.getModel();//Trajct Tables list
            model.removeAllElements();
            DefaultListModel model2 = (DefaultListModel) jListRF.getModel();//RF list
            model2.removeAllElements();
            while (vTableName.next()) {/* creates a new table for each table that has objects with topological relation to vRegion */
                model2.addElement(new AssociatedParameter(
                        vTableName.getString("tableName"),
                        vTableName.getString("type"))
                );// RFs
                
                if(vTableName.getString("tableName").equals(config.poi)){
                	poi_associated = new AssociatedParameter(vTableName.getString("tableName"), vTableName.getString("type"));
                }
                
                model.addElement(vTableName.getString(1));//Trajectory tables
            }
        } catch (Exception vErro) {
            vErro.printStackTrace();
        }

    }

    private void OKActionPerformed() {
    	System.out.println("Buffer of "+config.userBuff+" saved.");	
    	
        //cause CB-SMoT has a version without RFs
        if (jListRF.getSelectedIndex() == -1 && isNotEquals(config.method, "CB-SMoT")) {
            showMessageDialog(this, "Select one or more relevant features.");
            return;
        }

        if (jListTrajectoryTables.getSelectedIndex() == -1) {
            showMessageDialog(this, "Select one or more trajectory table.");
            return;
        }

        //get the thing in 'things', those trajectories tables to be executed
        final Object[] objects = jListTrajectoryTables.getSelectedValuesList().toArray();
        String[] str = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            str[i] = (String) objects[i];
        }

        //controls if SRID of RFs are different from trajectories...
        // ALL the trajectories should have the SAME srid
        // it is checked ahead in the foreach.
//        config.table = str[0]; // COMENTAR DEPOIS
        String error = checkSRIDs(); //att the variable 'table_srid'

        if(error.compareTo("")!=0){
        	JOptionPane.showMessageDialog(this,error);
            return;
        }

        //for each of the trajectory table selected...
        Object[] selectedValues = jListRF.getSelectedValuesList().toArray(); // COMENTAR DEPOIS (URGENTE ESSE E O DEBAIXO!)
//        Object[] selectedValues = new Object[]{poi_associated}; // DESCOMENTAR DEPOIS
        Method method = null;
        if(config.method.equals("SMoT")){
        	method = (Method) algorithms[0];
        }else{
        	method = (Method) algorithms[1];
        }
        Boolean enableBuffer = true; // Always True, isso interfere com base na área, sempre é bom ter uma área ao redor do POI
        int maxSelectedIndex = jListRF.getMaxSelectionIndex();
        final int tam = str.length;
        for (int count = 0; count < tam; ) {
            config.table = str[count];
            try {
                long time = createTrajectoryTablesSelected(
                        config.table,
                        config.tid,
                        config.time,
                        selectedValues,
                        config.userBuff,
                        method,
                        enableBuffer,
                        config,
                        config.ftype,
                        maxSelectedIndex
                );
                count++;
                if (count == tam) {
                	System.out.println("Processing time: " + time + " ms");
                    showMessageDialog(this, "Operation finished successfully.");
                }
            } catch (Exception e) {
            	System.out.println("Error: \n" + e.getMessage());
                showMessageDialog(this, "Error during operation");
            }
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
