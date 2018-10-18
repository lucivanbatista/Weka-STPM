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
    /**
     * trying to make the bird to fly...
     */
    private final WekaTaskMonitor tm = new WekaTaskMonitor();
    /**
     * Selects the appropriate method to be applied
     */
    JComboBox<Method> jComboBoxMethod;
    /**
     * Select the parameters of each method
     */
    JComboBox<Parameter> jComboBoxParam;
    /**
     * Enters the numbers atributed to each parameter here.
     */
    javax.swing.JTextField jTextFieldParam;
    /**
     * The name of the table with streets information.
     */
    javax.swing.JComboBox jComboBoxStreet;
    /**
     * The column of that table with information about speed limit (in meters/second).
     */
    javax.swing.JComboBox jComboBoxStreetLimit;
    /**
     * List of relevant features, the tables to use to try to discover the places in trajectory.
     */
    javax.swing.JList jListRF;
    /**
     * Indicates the distance in meters of the buffer in the relevant features.
     */
    //private javax.swing.JSpinner jSpinnerBuffer;
    javax.swing.JTextField jTextFieldBuffer;
    /**
     * Use or not buffer in the relevant features
     */
    javax.swing.JCheckBox jCheckBoxBuffer;

    //Feature type/instance
    /**
     * Text field for entering the min time. Must enter the seconds and press 'TAB', for recording.
     */
    javax.swing.JTextField RFMinTime;
    /**
     * If selected, says that we are working with Feature Instance.
     */
    javax.swing.JRadioButton jRadioButtonFInstance;
    /**
     * If selected, says that we are working with Feature Type.
     */
    javax.swing.JRadioButton jRadioButtonFType;
    /**
     * Opens the Generate Arff File Frame
     */
    javax.swing.JButton jButtonGenArffFile;
    /**
     * Select the schema of DB, usually 'public' for localhost tests.
     */
    JComboBox<String> jComboBoxSchema;
    /**
     * DEPRECATED
     */
    javax.swing.JComboBox jComboBoxTF;
    javax.swing.ButtonGroup buttonGroup1;//used in the genarfffile frame
    javax.swing.ButtonGroup buttonGroupItem;// the same
    javax.swing.ButtonGroup buttonGroupTime;//the same
    /**
     * The thing (trajectories tables) which we want to apply the method selected
     */
    private javax.swing.JList jListTrajectoryTables;
    private Double buffer = 50.0;    // variable buffer, initialized
    private AssociatedParameter poi_associated = null;

    GraphicComponents(Connection conn, Config config) {
        this.conn = conn;
        this.config = config;
    }

    void initComboBoxSchemas(List<String> schemas) {
        schemas.forEach(schema -> jComboBoxSchema.addItem(schema));
    }

    void initGraphicComponents(Method[] algorithms) {

        Container container = getContentPane();

        container.setLayout(new BorderLayout());

        JPanel panelSchema = new JPanel();
        panelSchema.setBorder(BorderFactory.createEtchedBorder());
        JLabel schemaLabel = new JLabel("Schema:");
        panelSchema.add(schemaLabel, BorderLayout.CENTER);
        jComboBoxSchema = new JComboBox<>();
        jComboBoxSchema.setPreferredSize(new Dimension(150, 22));
        panelSchema.add(jComboBoxSchema, BorderLayout.CENTER);

        JButton Load = new JButton("Load");
        Load.addActionListener(event -> LoadActionPerformed());
        panelSchema.add(Load);

        JButton configure = new JButton("Configure Trajectory Table");
        configure.addActionListener(this::configureActionPerformed);
        panelSchema.add(configure);


        //bruno
        JButton show = new JButton("Visualization");
        show.addActionListener(evt -> showGeographicData());
        panelSchema.add(show);
        //bruno


        //@Hercules
        JButton filter = new JButton("Trajectory cleaning");
        filter.addActionListener(event -> filterActionPerformed());
        panelSchema.add(filter);
        //HMA
        container.add(panelSchema, BorderLayout.NORTH);

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

        //Granularity Level-Panel Granularity
        JPanel panelGranularity = new JPanel();
        panelGranularity.setBorder(BorderFactory.createTitledBorder("Granularity Level"));

        jRadioButtonFType = new JRadioButton("Feature Type", false);
        jRadioButtonFInstance = new JRadioButton("Feature Instance", true);
        ButtonGroup group = new ButtonGroup();
        group.add(jRadioButtonFType);
        group.add(jRadioButtonFInstance);

        panelGranularity.add(jRadioButtonFType);
        panelGranularity.add(jRadioButtonFInstance);

        c.gridx = 5;
        c.gridy = 0;
        c.gridwidth = 5;
        c.gridheight = 3;
        panelContent.add(panelGranularity, c);

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
        JPanel bufPanel = new JPanel();
        bufPanel.setBorder(BorderFactory.createEtchedBorder());
        GridBagLayout gbag = new GridBagLayout();
        bufPanel.setLayout(gbag);
        GridBagConstraints c2 = new GridBagConstraints();
        c2.insets = new Insets(5, 5, 5, 5);

        c2.gridx = 0;
        c2.gridy = 0;
        jCheckBoxBuffer = new JCheckBox();
        jCheckBoxBuffer.setSelected(true);
        jCheckBoxBuffer.setText("User Buffer (m):");
        bufPanel.add(jCheckBoxBuffer, c2);

        c2.gridy = 2;
        c2.gridheight = 2;
        jTextFieldBuffer = new JTextField();
        jTextFieldBuffer.setPreferredSize(new Dimension(60, 20));
        jTextFieldBuffer.setText("50.0");
        bufPanel.add(jTextFieldBuffer, c2);

        c.gridx = 3;
        c.gridy = 2;
        c.gridheight = 2;
        c.gridwidth = 2;
        panelContent.add(bufPanel, c);

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
        JPanel panelMethod = new JPanel();
        panelMethod.setBorder(BorderFactory.createTitledBorder("Method"));
        gbag = new GridBagLayout();
        panelMethod.setLayout(gbag);
        c2 = new GridBagConstraints();
        c2.insets = new Insets(3, 3, 3, 3);

        c2.gridx = 0;
        c2.gridy = 0;
        c2.gridwidth = 6;
        jComboBoxMethod = new JComboBox<>(algorithms);
        jComboBoxMethod.setPreferredSize(new Dimension(210, 20));
        jComboBoxMethod.addItemListener(event -> jComboBoxMethodItemStateChanged());
        panelMethod.add(jComboBoxMethod, c2);

        c2.gridx = 0;
        c2.gridy = 2;
        c2.gridwidth = 3;
        JLabel jLabel4 = new JLabel("Parameter: ");
        panelMethod.add(jLabel4, c2);

        c2.gridx = 3;
        JLabel jLabel5 = new JLabel("Value: ");
        panelMethod.add(jLabel5, c2);

        c2.gridx = 3;
        c2.gridy = 3;
        jTextFieldParam = new JTextField();
        jTextFieldParam.setPreferredSize(new Dimension(40, 20));
        jTextFieldParam.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                jTextFieldParamFocusLost();
            }
        });
        panelMethod.add(jTextFieldParam, c2);

        c2.gridx = 0;
        c2.gridy = 3;
        jComboBoxParam = new JComboBox<>();
        jComboBoxParam.setPreferredSize(new Dimension(160, 20));
        jComboBoxParam.addItemListener(this::jComboBoxParamItemStateChanged);
        panelMethod.add(jComboBoxParam, c2);

        c.gridx = 5;
        c.gridy = 3;
        panelContent.add(panelMethod, c);
        container.add(panelContent, BorderLayout.CENTER);

        JPanel panelDown = new JPanel();
        panelDown.setBorder(BorderFactory.createEtchedBorder());

        gridbag = new GridBagLayout();
        panelDown.setLayout(gridbag);
        c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 10, 10);

        jButtonGenArffFile = new JButton("Generate Arff File...");
        jButtonGenArffFile.addActionListener(event -> jButtonGenArffFileActionPerformed());
        c.gridx = 0;
        c.gridy = 0;
        panelDown.add(jButtonGenArffFile, c);

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

    void jComboBoxMethodItemStateChanged() {
        Method alg = (Method) jComboBoxMethod.getSelectedItem();
        jComboBoxParam.removeAllItems();
        if (alg != null) {
            for (int i=0;i<alg.param.size();i++) {
                jComboBoxParam.addItem(alg.param.elementAt(i));
            }
        }

        //prevents the SMoT methods to call upon parameters
        if(alg.toString().compareTo("SMoT")==0){
        	jComboBoxParam.setEnabled(false);
        	jTextFieldParam.setEnabled(false);
        }
        else{
        	jComboBoxParam.setEnabled(true);
        	jTextFieldParam.setEnabled(true);
        }
    }

    private boolean checkBufferState() {
        try {
            buffer = Double.valueOf(jTextFieldBuffer.getText());
            return true;
        } catch (NumberFormatException e) {
            jTextFieldBuffer.setText("50.0");
            return false;
        }
    }

    private void jButtonGenArffFileActionPerformed() {
        GenArffFile gaf = new GenArffFile(conn, jRadioButtonFType.isSelected());
        gaf.setVisible(true);
    }

    private void jButtonCancelActionPerformed() {
        this.dispose();
    }

    private void jTextFieldParamFocusLost() {
        Parameter p = (Parameter) jComboBoxParam.getSelectedItem();
        try {
            if (p != null) {
                if (DOUBLE == p.type) {
                    p.value = Double.valueOf(jTextFieldParam.getText());
                } else {
                    p.value = Integer.valueOf(jTextFieldParam.getText());
                }
            }
        } catch (NumberFormatException e) {
            showMessageDialog(this, "Parameter value invalid!");
        }
    }

    // Campos CB ao ser ativado
    private void jComboBoxParamItemStateChanged(java.awt.event.ItemEvent evt) {
        Parameter p = (Parameter) evt.getItem();
        if (p.type == DOUBLE) {
            jTextFieldParam.setText(p.value.toString());
        } else {
            jTextFieldParam.setText(((Integer) p.value).toString());
        }
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
            jTextFieldBuffer.grabFocus();
        } catch (java.lang.NumberFormatException e) {
            showMessageDialog(this, e.toString());
            RFMinTime.grabFocus();
        }
    }

    private void LoadActionPerformed() {
        try { //load the tables in a list of auxiliary strings
            Statement s = conn.createStatement();
            final String sql = "SELECT f_table_name as tableName,type FROM geometry_columns " +
                    "WHERE f_table_schema=trim('" + jComboBoxSchema.getSelectedItem() + "') ORDER BY tableName";

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
        if (jCheckBoxBuffer.isSelected()) {
        	if(checkBufferState()){    		
    			System.out.println("Buffer of "+buffer+" saved.");
    		}
    		else{
    			showMessageDialog(this,"Buffer expects a number.");
    			return;
    		}
        } else {
            this.buffer = 50.0; // default value
        }


        //cause CB-SMoT has a version without RFs
        if (jListRF.getSelectedIndex() == -1 && isNotEquals(jComboBoxMethod.getSelectedItem(), "CB-SMoT")) {
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
//        Object[] selectedValues = jListRF.getSelectedValuesList().toArray(); // COMENTAR DEPOIS
        Object[] selectedValues = new Object[]{poi_associated}; // DESCOMENTAR DEPOIS
        Method method = (Method) jComboBoxMethod.getSelectedItem();
        Boolean enableBuffer = jCheckBoxBuffer.isSelected();
        int maxSelectedIndex = jListRF.getMaxSelectionIndex();
        Boolean enableFType = jRadioButtonFType.isSelected();
        System.out.println(method);
        final int tam = str.length;
        for (int count = 0; count < tam; ) {
            config.table = str[count];
            try {
                long time = createTrajectoryTablesSelected(
                        config.table,
                        nameTableStop(config.table),
                        config.tid,
                        config.time,
                        selectedValues,
                        buffer,
                        method,
                        enableBuffer,
                        config,
                        enableFType,
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

    private void showGeographicData() {
        try {
            Statement s = conn.createStatement();

            if (jListTrajectoryTables.getSelectedIndex() == -1) {
                ShowGeoData rep = new ShowGeoData(conn);
                JFrame f = new JFrame("Geographic Data Visualizer");
                f.setFocusable(true);
                f.requestFocus();
                f.getContentPane().setLayout(new BorderLayout());
                f.getContentPane().add(rep, BorderLayout.CENTER);
                f.pack();
                f.setVisible(true);
                f.setSize(new Dimension(800, 600));
                f.setResizable(false);

                rep.paintAll(f.getGraphics());
                return;
            }

            final String sql = String.format("SELECT f_table_name as tableName,type FROM geometry_columns WHERE f_table_schema=trim('%s') ORDER BY tableName",
                    jComboBoxSchema.getSelectedItem());

            ResultSet vTableName = s.executeQuery(sql);
            int indexActualVT = 0;
            while (vTableName.next()) {
                if (indexActualVT++ == jListTrajectoryTables.getSelectedIndex()) {
                    ShowGeoData rep = new ShowGeoData(conn);
                    JFrame f = new JFrame("Geographic Data Visualizer");
                    f.setFocusable(true);
                    f.requestFocus();
                    f.getContentPane().setLayout(new BorderLayout());
                    f.getContentPane().add(rep, BorderLayout.CENTER);
                    f.pack();
                    f.setVisible(true);
                    f.setSize(new Dimension(800, 600));
                    f.setResizable(false);

                    rep.loadPoints(vTableName.getString("tableName"), Color.BLUE, 2);

                    rep.paintAll(f.getGraphics());
                }
            }

        } catch (SQLException e) {
        	System.out.println(e.getMessage());
        }
    }

    // Format name of parameters

    private void filterActionPerformed() {
        int[] i = jListTrajectoryTables.getSelectedIndices();

        if (i.length < 1) {
            showMessageDialog(this, "Select one or more Trajectory Table.");
            return;
        }

        String schema = (String) jComboBoxSchema.getSelectedItem();
        Object[] temp = jListTrajectoryTables.getSelectedValues();

        TrajectoryClean tc = new TrajectoryClean(conn, Arrays.stream(temp)
                .map(ob -> (String) ob)
                .collect(Collectors.toList()));

        tc.setConfig(this.config);
        tc.setEsquema(schema);
        tc.setVisibleFrame(true);

    }

    private List<Parameter> parametersCluster() {
        List<Parameter> list = new ArrayList<>();
        int tamParams = 0;
        while (tamParams < jComboBoxParam.getModel().getSize())
            list.add(jComboBoxParam.getModel().getElementAt(tamParams++));
        return list;
    }

    private String nameTableStop(String sp) {
        List<Parameter> parameters = parametersCluster();
        return createNameOfStopTable(sp, parameters);
    }
}
