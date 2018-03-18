package weka.gui.stpm;

import weka.gui.WekaTaskMonitor;
import weka.gui.geodata.visualizer.ShowGeoData;
import weka.gui.stpm.clean.TrajectoryClean;
import weka.gui.stpm.clean.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static weka.gui.stpm.Constants.*;


//public class TrajectoryFrame extends JFrame{
public class TrajectoryFrame extends JDialog{

	/**set of methods used */
	private Method[] algs;
	
	/**Connection with the DB*/
	private Connection conn; 
	
	/**User configurations*/
	private Config config = new Config();

	// Method panel
	/**Selects the appropriate method to be applied */
    private javax.swing.JComboBox jComboBoxMethod;
    
    /**Select the parameters of each method */
    private javax.swing.JComboBox jComboBoxParam;
    
    /**Enters the numbers atributed to each parameter here. */
    private javax.swing.JTextField jTextFieldParam;
    
    /**The name of the table with streets information. */
    private javax.swing.JComboBox jComboBoxStreet;
	
    /**The column of that table with information about speed limit (in meters/second). */
    private javax.swing.JComboBox jComboBoxStreetLimit;
	    
    //Relevant Features, Buffer and MinTime
    /**List of relevant features, the tables to use to try to discover the places in trajectory.*/
    private javax.swing.JList jListRF;
    
    /**Indicates the distance in meters of the buffer in the relevant features. */
    //private javax.swing.JSpinner jSpinnerBuffer;
    private javax.swing.JTextField jTextFieldBuffer;
    
    /**Use or not buffer in the relevant features */
    private javax.swing.JCheckBox jCheckBoxBuffer;
    
    /**Text field for entering the min time. Must enter the seconds and press 'TAB', for recording. */
	private javax.swing.JTextField RFMinTime;
    
    //Feature type/instance
    /**If selected, says that we are working with Feature Instance. */
    private javax.swing.JRadioButton jRadioButtonFInstance;
    
    /**If selected, says that we are working with Feature Type. */
    private javax.swing.JRadioButton jRadioButtonFType;
    
    //others...
    /**Opens the Generate Arff File Frame */
    private javax.swing.JButton jButtonGenArffFile;    
    
    /** trying to make the bird to fly... */
    private final WekaTaskMonitor tm = new WekaTaskMonitor();
    
    /**The thing (trajectories tables) which we want to aply the method selected*/
    private javax.swing.JList jListTrajectoryTables; // replaces the jComboBoxTF
        
    /**Select the schema of DB, usually 'public' for localhost tests. */
    private javax.swing.JComboBox jComboBoxSchema;
    
    /**DEPRECATED*/
    private javax.swing.JComboBox jComboBoxTF; //old targetfeature combbox
    
    private javax.swing.ButtonGroup buttonGroup1;//used in the genarfffile frame
    private javax.swing.ButtonGroup buttonGroupItem;// the same
    private javax.swing.ButtonGroup buttonGroupTime;//the same
    
    /**Spatial reference for ALL trajectory_table*/
    private int table_srid; //spatial reference ID,google it
	
    /**Buffer variable*/
    private Double buffer=50.0;	// variable buffer, initialized
    
    private SMOT smot_run = new SMOT();
    private CBSMOT cb_run = new CBSMOT();
    
    /*    public TrajectoryFrame(){    	
    	this.setTitle("Trajectory");    	
        init();
    	initComponents();    	
    }    */
    
    // Construtor da interface TrajectoryFrame
    public TrajectoryFrame(String user, String pass, String url) {
    	System.out.println("AQUI 1 : Construtor da interface TrajectoryFrame");
    	this.setTitle("Trajectory");    	
    	init(); // Algoritmos
        initComponents(); // Interface gr�fica
    	try {
            
    		loadPropertiesFromFile(user, pass, url); // Conex�o Banco
            loadSchemas(); // Schemas
            jComboBoxMethodItemStateChanged(null);
            /*            config.conn = conn;
            config.tid = "tid";
            config.time = "time";  */          
    	}catch(Exception e) {
    		System.out.println(e.toString());
            JOptionPane.showMessageDialog(this,"Error in conection with DB.");	    	
            dispose();
        }	
    }


    // Inicio da Conex�o com o Banco de Dados
    private void loadPropertiesFromFile(String userFromInput, String passFromInput, String urlFromInput) throws SQLException {
    	System.out.println("AQUI 2 : Conex�o com o Banco de Dados");
    	Properties properties = new Properties();


    	try {
            properties.load(ClassLoader.getSystemResourceAsStream(CONFIG_PROPERTIES));


    		String user = (userFromInput != null) ? userFromInput :
                    properties.getProperty(DB_USER);
    		String pass = (userFromInput != null) ? passFromInput :
                    properties.getProperty(DB_PASS);
    		String url = (userFromInput  != null) ? urlFromInput  :
                    properties.getProperty(DB_URL) + properties.getProperty(DB_NAME);

    		if (user.equals(VOID))
    			conn = DriverManager.getConnection(url);
    		else
    			conn = DriverManager.getConnection(url,user,pass);

    		((org.postgresql.PGConnection) conn).addDataType(DB_TYPE_GEOMETRY, org.postgis.PGgeometry.class);

    		config.conn = conn;
    		config.table = properties.getProperty(TRAJECTORY_TABLE);
    		config.tid = properties.getProperty(TRAJECTORY_ID);
    		config.time = properties.getProperty(DETECTION_TIME);

    	} catch (IOException ex) {
    		ex.printStackTrace();
    	}
    }
    
    /**Load the tables in the DB with geometry columns*/
    // Carregar os Schemas do Banco que possuem columas geometricas
    private void loadSchemas() {
    	System.out.println("AQUI 3 : Schemas do Banco");
        try {
            Statement smnt = conn.createStatement();
            ResultSet rs = smnt.executeQuery("SELECT DISTINCT f_table_schema FROM geometry_columns");
            while (rs.next()) {
                jComboBoxSchema.addItem(rs.getString(1));
            }
        }catch(SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Failed loading schemas");
        }		
    }
    
    // Define Algoritmos do IB E CB
    private void init() {  
    	System.out.println("AQUI 4 : Define Algoritmos IB e CB");
    	algs = new Method[2];
        int i = 0;

        /** SMOT:
 		* Original method used to find stops, given a list of RFs.
 		*/
        algs[i] = smot_run;                   
//        algs[i] = new SMOT(buffer, config);
        
        /** CB-SMOT:
         * Used to clusterize and find slow-speed periods in a trajectory.        
         */
        algs[++i] = cb_run;
        
        algs[i].param.add(
                        new Parameter("MaxAvgSpeed",Parameter.Type.DOUBLE,new Double(0.9))
                        );
        algs[i].param.add(
                        new Parameter("MinTime (seconds)",Parameter.Type.INT,new Integer(60))
                        );                
        algs[i].param.add(
                        new Parameter("MaxSpeed",Parameter.Type.DOUBLE,new Double(1.1))
                        );                
    }
    
    // TID dos taxistas e seus pontos
    private void loadTrajectories(String tableTraj) throws SQLException, IOException { 
    	System.out.println("AQUI 5 : Get TID dos taxistas e seus pontos ");
    	Method method = (Method) jComboBoxMethod.getSelectedItem();
        InterceptsG i = null;
        InterceptsG streets = null;
        
        Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        // selects the trajectory-tid to be processed
        String sql = "SELECT "+config.tid+" as tid, count(*) FROM "+config.table+" GROUP BY "+config.tid+" ORDER BY tid DESC";
        ResultSet rs = s.executeQuery(sql);
        
    	System.out.print("Creating interceptions...");
    	i = createIntercepts();
    	System.out.println("Interceptions created ");


        File speedFile = null;
        if(TrajectoryClean.isPrintSpeedToFileXls()){
            speedFile = Util.getFileSpeed(TrajectoryFrame.getCurrentNameTableStop());
        }
        
        // Add hash index on tid if it doesn't exists to boost perf
        Statement s0a = conn.createStatement(); 
        String sql0a = "with x as (select oid from pg_class where relname = '"+config.table+"') " +
        		"select 1 from (select attnum from pg_attribute where attrelid IN (select * from x) and attname = '"+config.tid+"') a" +
        		"inner join (select indkey from pg_index where indrelid IN (select * from x)) b on attnum = ALL(indkey);";
        ResultSet rs0 = s0a.executeQuery(sql0a);
        boolean indexTimeExists = false;
        indexTimeExists = rs0.next();
        s0a.close();
        //System.out.println(sql0a);
        //Diferencia��o dos m�todos ser� aqui        
        if (!indexTimeExists){
        	System.out.println("Creating Index on " + config.tid );
        	Statement s0b = conn.createStatement(); 
        	String sql0b = "CREATE INDEX ON "+config.table+" USING hash ("+config.tid+") WITH (FILLFACTOR=100);";
        	s0b.execute(sql0b);
        	s0b.close();
        } else {
        	System.out.println("Tid index on " + config.table + " exists. Performance will be good." );
        }
        
    	//for each trajectory... (select que pega os tid dos taxistas e ordena de forma decrescente)        
        while (rs.next()) {
        	Trajectory trajectory = new Trajectory(table_srid);
            trajectory.tid = rs.getInt("tid");     
            String meth = method.toString();
            
            if (!meth.startsWith("SMoT")) {
            	//select the points of the trajectory in sequential time
            	Statement s1 = conn.createStatement();                
            	String sql2 = "SELECT "+config.time+" as time,the_geom,gid FROM "+tableTraj+" WHERE "+config.tid+"="+trajectory.tid+" ORDER BY "+config.time;
            	//System.out.println(sql2);

            	//Ordena��o por tempo
                ResultSet rs2 = s1.executeQuery(sql2);
                //for each of these points of the trajectory...
                int timeIndex = 0;
                
                // Informa��es de cada ponto sendo colocada em gps
                while (rs2.next()) {
                	
                    Timestamp t = rs2.getTimestamp("time");
                    org.postgis.PGgeometry geom = (org.postgis.PGgeometry) rs2.getObject("the_geom");
                    org.postgis.Point p = (org.postgis.Point) geom.getGeometry();
                    //get the time, the_geom and tid columns to fill the Vector
                    GPSPoint gps = new GPSPoint(trajectory.tid,t,p,timeIndex);
                    
                    gps.gid = rs2.getInt("gid");
                    trajectory.points.addElement(gps);
                    timeIndex++;
                    /*System.out.println("gid: "+gps.gid+
                    		           " tid: "+gps.tid+
                    		           " time: "+gps.time+
                		           	   " timeIndex: "+gps.getTimeIndex());*/                    
                }                
                
                //calculates the speed of each point, and then runs the method
               	if (trajectory.points.size()>5){                	
               		//trajectory.calculatePointsSpeed(2);
               		trajectory.calculatePointsSpeed();
                        double mediaVelocidade  = trajectory.meanSpeed();
                        double mediaDistancia   = trajectory.meanDist();
                        long   duracao          = trajectory.duration();
//                        if(trajectory.tid ==95145){
//                            int uuuui = trajectory.tid;
//                        }else{
//                            continue;
//                        }

                        if(TrajectoryClean.isPrintSpeedToFileXls()){
                            //FIXME: @Hercules. impressao sa velocidade.
                            Util.imprimeVelocidades(trajectory.points, speedFile, rs.isFirst());
                        }
                        // Run do CB
                        
                        cb_run.setInformations(buffer, config, jCheckBoxBuffer.isSelected(), jRadioButtonFType.isSelected(), table_srid, jListRF);
                        cb_run.run(trajectory, i, tableTraj, streets);
//                      method.run(trajectory,i,tableTraj,streets);
               	}else{
               		System.out.println("Trajectory "+trajectory.tid+" has less than 5 points. It will be disconsidered.");
               	}
               	//BEGIN TEST by Lucivan Batista
                System.out.println("--- INICIANDO TESTE ---");
                System.out.println("mediaVelocidade: " + trajectory.meanSpeed() + "; mediaDistancia: " + trajectory.meanDist() + "; duracao: " + trajectory.duration());
                //END TEST by Lucivan Batista
            }
            //just runs the method chosen, aplyed to one trajectory
            //also see init();
            // Run do IB
            else{
            	smot_run.setInformations(buffer, config, jCheckBoxBuffer.isSelected(), jRadioButtonFType.isSelected());
            	smot_run.run(trajectory, i, tableTraj, streets);
//            	method.run(trajectory,i,tableTraj,streets);
            }
            
            
        }        
        TrajectoryMethods.resetunknown();        
    }

    public static List<Trajectory> getTrajectoriesWithSpeeds(String tableTraj, Config config, Integer table_srid) throws SQLException, IOException {
    	System.out.println("AQUI 6");
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
            //System.out.println(sql2);

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
            System.out.println("calculando velocidade da trajetoria "+trajectory.tid);
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

    // Criar tables
    private void createTables() throws SQLException {
    	System.out.println("AQUI 7 : Cria��o das Tabelas");
        Statement s = conn.createStatement();
        Statement s1= conn.createStatement();        
        Double bufferValue = Double.valueOf(jTextFieldBuffer.getText());

        //@Hercules
        String nmTableStop = nameTableStop(config.table);
        TrajectoryFrame.setCurrentNameTableStop(nmTableStop);
        //@Hercules

        // STOPS table
        System.out.println("\t\tstops table...");//testes...
        try {
            s.execute("DROP TABLE "+TrajectoryFrame.getCurrentNameTableStop());
            s.execute("DELETE FROM geometry_columns WHERE f_table_name = '"+TrajectoryFrame.getCurrentNameTableStop()+"'");
            //System.out.println("\t\tstops drop...");
        }catch (SQLException ex) {
        }finally {        	
            s.execute(
                "CREATE TABLE "+TrajectoryFrame.getCurrentNameTableStop()+" ("+
                "    gid serial NOT NULL,"+
                "    tid integer NOT NULL,"+
                "    stopid integer NOT NULL,"+
                "    stop_gid character varying,"+
                "    stop_name character varying,"+
                "    start_time timestamp without time zone,"+
                "    end_time timestamp without time zone,"+
                "    rf character varying,"+    
                "    avg real," +
                "    CONSTRAINT "+TrajectoryFrame.getCurrentNameTableStop()+"_gidkey PRIMARY KEY (gid)"+
                ") WITHOUT OIDS;"    
            );            
              s.execute("SELECT AddGeometryColumn('"+TrajectoryFrame.getCurrentNameTableStop()+"', 'the_geom',"+table_srid+", 'MULTIPOLYGON', 2)");
//            try {
//                //s.execute("ALTER TABLE "+TrajectoryFrame.getCurrentNameTableStop()+" DROP CONSTRAINT enforce_geotype_the_geom");
//            } catch (Exception ex) {
//                try {
//                    //s.execute("ALTER TABLE "+TrajectoryFrame.getCurrentNameTableStop()+" DROP CONSTRAINT \"$2\"");
//                }catch (Exception e) {
//                    ex.printStackTrace();
//                }
//            }
        }

        //MOVES table (NOT USED YET)
        System.out.println("\t\tmoves table...");
        try {
            s.execute("DROP TABLE moves");
            s.execute("DELETE FROM geometry_columns WHERE f_table_name = 'moves'");
        } catch (SQLException ex) {
        }finally {
            s.execute(
                "CREATE TABLE moves ("+
                "      tid integer NOT NULL,"+
                "      moveid integer NOT NULL,"+
                "      start_time timestamp without time zone,"+
                "      end_time timestamp without time zone,"+
                "      start_stop character varying,"+
                "      end_stop character varying,"+
                "      start_stop_pk integer,"+
                "      end_stop_pk integer,"+                    
                "      rf character varying,"+    
                "      CONSTRAINT moves_pkey PRIMARY KEY (tid,rf,moveid)"+
                ")WITHOUT OIDS"
            );
            s.execute("SELECT AddGeometryColumn('moves', 'the_geom',"+table_srid+", 'LINESTRING', 2)");
//            try {
//                //s.execute("ALTER TABLE moves DROP CONSTRAINT enforce_geotype_the_geom");
//            } catch (Exception ex) {
//                try {
//                    //s.execute("ALTER TABLE moves DROP CONSTRAINT \"$2\"");
//                }catch (Exception e) {
//                    ex.printStackTrace();
//                }
//            }
        }
        
        /*Apply buffer geometry to RFs.  */

        /*
        System.out.println("\t\tenvelopes...");
        Object[] objs = jListRF.getSelectedValues();
        String bufenv,buffer;        
        for (Object obj : objs) {
            String rf = ((AssociatedParameter) obj).name;            
            String type = ((AssociatedParameter) obj).name;
            if(!type.contains("POINT")){
	            if (jCheckBoxBuffer.isSelected()) {
	                    buffer = "buffer(the_geom," + bufferValue + ") as buf,";	                    
	                    bufenv = "buffer(envelope(the_geom),"+ bufferValue +") as bufenv";
	            }
	            // if there's no buffer
	            else {
	                buffer = "the_geom as buf,";
	                bufenv = "envelope(the_geom) as bufenv";	                
	            }
	            //System.out.println("Here:\n"+buffer+"\n"+bufenv);
	            try {
	            	String a = "SELECT * FROM "+rf+"_envelope LIMIT 1";
	                s.execute(a);
	            }catch (SQLException ex) {
	                //System.out.println(ex.getMessage());
	            	try {     
	                	//do not create a copy in the DB
	                	//s.execute("CREATE TEMPORARY TABLE "+rf+"_envelope AS SELECT gid,"+buffer+bufenv+" FROM "+rf);                	
	                	//create a copy in DB
	                	String a = "CREATE TABLE "+rf+"_envelope AS SELECT gid,"+buffer+bufenv+" FROM "+rf;
	                	s.execute(a);                	
	                } catch (SQLException ex2) {
	                	//System.out.println(ex2.getMessage());
	                    s.execute("UPDATE "+rf+"_envelope SET env = "+buffer);
	                }
	            }
            }// if not POINT            
        }
        */

    }

    // Cria��o dos Intercepts com os Pontos de Interesse
    private InterceptsG createIntercepts() throws SQLException{
	System.out.println("AQUI 8 : Create Intercepts");
	 	//get the RFs from panel...
    	Object[] objs = jListRF.getSelectedValuesList().toArray();
    	AssociatedParameter[] relevantFeatures = new AssociatedParameter[objs.length];                    
        for (int i=0;i<objs.length;i++) {
            relevantFeatures[i] = (AssociatedParameter) objs[i];
        }
        //for each rf, execute the query and save, in main memory, the results
        Statement s = config.conn.createStatement();
        InterceptsG intercs = new InterceptsG(); 
        for(AssociatedParameter a:relevantFeatures){
        	// Create a table of registers with:
        	// pt -> gid of the trajectory point
        	// gid -> gid from RF wich intercept it
        	// rf -> rf_name 
        	java.util.Date tempo2,fim2,ini2 = new java.util.Date();            	
        	System.out.println("\t\t...with "+a.name);
        	
        	String sql; 
        	if(a.type.contains("POINT") || a.type.contains("LINE")){// if any kind of POINT or LINE
        		try {
                    s.execute("DROP TABLE "+a.name+"_buf;");
                } catch (SQLException ex) {
                	// do nothing
                } finally {
                	s.execute("create table "+a.name+"_buf as select gid, ST_Buffer(the_geom::geography,"+buffer+")::geometry as the_geom from "+a.name+";");
                	s.execute("alter table "+a.name+"_buf add constraint "+a.name+"_buf_pk primary key (gid);");
                }
        		sql=("select A.gid as pt, B.gid as gid, '"+a.name+"' as rf "+
        			 "from "+config.table+" A,"+a.name+"_buf B " +
	            	 "where st_intersects(A.the_geom,B.the_geom);");
        	}
        	else{
        		/* sql=("select A.gid as pt,B.gid as gid,'"+a.name+"' as rf from "+config.table+" A,"+a.name+"_envelope B" +
	            	 " where st_intersects(A.the_geom,B.bufenv) " + 
	            	 "AND st_intersects(A.the_geom,B.buf) ;"); */
        		if (jCheckBoxBuffer.isSelected()) { // if user sets buffer
        			try {
            			s.execute("DROP TABLE "+a.name+"_buf");
            		} catch (SQLException ex) {
            			// do nothing
            		} finally {
            			s.execute("CREATE TABLE "+a.name+"_buf AS SELECT gid, ST_Buffer(the_geom::geography,"+buffer+")::geometry AS the_geom FROM "+a.name+";");
            			s.execute("ALTER TABLE "+a.name+"_buf ADD CONSTRAINT "+a.name+"_buf_pk PRIMARY KEY (gid);");
            		}
            		sql = "select A.gid as pt, B.gid as gid, '"+a.name+"' as rf "+
            		      "from "+config.table+" A, "+a.name+"_buf B "+
            		      "where st_intersects(A.the_geom,B.the_geom);";
        		} else {
        			sql = "select A.gid as pt, B.gid as gid, '"+a.name+"' as rf "+
      		      		  "from "+config.table+" A, "+a.name+" B "+
      		      		  "where st_intersects(A.the_geom,B.the_geom);";
        		}
        		
        	}
            ResultSet Intercep = s.executeQuery(sql);
            fim2 = new java.util.Date();
            tempo2 = new java.util.Date(fim2.getTime()-ini2.getTime());
            System.out.println("\t\t"+a.name+" time: " +tempo2.getTime()+" ms");
            // then, save the registers from the query in an adequate struct 
                             
            while(Intercep.next()){            	
            	Interc i = new Interc (Intercep.getInt("pt"), Intercep.getInt("gid"), Intercep.getString("rf"),a.value.intValue());
            	intercs.addpt(i);
            }                
        }
        
        return intercs;
}
   
//----------------------------------------------------------------------------------------------------------------
// INTERFACE BEGINS
//----------------------------------------------------------------------------------------------------------------    
    
	// Cria��o da Interface Gr�fica
	private void initComponents(){
		System.out.println("AQUI 9 : Cria��o da Interface Gr�fica");
		//Mounts the layout
		Container container = getContentPane();
		//JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
	
		//PANEL LOAD SCHEMA
		JPanel panelSchema = new JPanel();
		panelSchema.setBorder(BorderFactory.createEtchedBorder());
		JLabel schemaLabel = new JLabel("Schema:");			
		panelSchema.add(schemaLabel,BorderLayout.CENTER);
		jComboBoxSchema = new JComboBox();
		jComboBoxSchema.setPreferredSize(new Dimension(150,22));
		panelSchema.add(jComboBoxSchema,BorderLayout.CENTER);		
	
		JButton Load = new javax.swing.JButton("Load");
		Load.addActionListener(evt -> LoadActionPerformed(evt));
		panelSchema.add(Load);
	
		JButton configure = new javax.swing.JButton("Configure Trajectory Table");
		configure.addActionListener(evt -> configureActionPerformed(evt));
		panelSchema.add(configure);
	
	
		//bruno
		JButton show = new javax.swing.JButton("Visualization");
		show.addActionListener(evt -> showDadosGeograficos());
		panelSchema.add(show);
		//bruno
	
	
		//@Hercules
		JButton filter = new javax.swing.JButton("Trajectory cleaning");
		filter.addActionListener(evt -> filterActionPerformed(evt));
		panelSchema.add(filter);
		//HMA
		container.add(panelSchema,BorderLayout.NORTH);
	
		//PANEL CONTENT
		JPanel panelContent = new JPanel();
		panelContent.setBorder(BorderFactory.createEtchedBorder());		
	
		GridBagLayout gridbag = new GridBagLayout();
		panelContent.setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();
	
		c.fill = GridBagConstraints.BOTH; 			
		c.insets = new Insets(5,10,5,10);
	
		//Config of trajectory Table
		c.gridx=0;
		c.gridy=0;			
		JLabel jLabel1 = new javax.swing.JLabel("Trajectory Table: ");
		panelContent.add(jLabel1,c);
	
		c.gridx=2;			
		c.gridwidth=2;
		c.weightx = 1.0;						
		JScrollPane sc1 = new javax.swing.JScrollPane();
		DefaultListModel modelsc = new DefaultListModel();
		jListTrajectoryTables= new JList(modelsc);
		jListTrajectoryTables.setVisibleRowCount(2);
		jListTrajectoryTables.setFixedCellWidth(2);			
		sc1.setViewportView(jListTrajectoryTables);
		panelContent.add(sc1,c);
	
		c.gridx=0;
		c.gridy=1;
		c.gridwidth=5;
		c.gridheight=1;
		javax.swing.JButton jButtonConfig = new javax.swing.JButton("Trajectory Table Config...");
	
	
		//Granularity Level-Panel Granularity
		JPanel panelGranularity = new JPanel();
		panelGranularity.setBorder(BorderFactory.createTitledBorder("Granularity Level"));
	
		jRadioButtonFType = new JRadioButton("Feature Type",false);
		jRadioButtonFInstance = new JRadioButton("Feature Instance",true);
		ButtonGroup group = new ButtonGroup();
		group.add(jRadioButtonFType);
		group.add(jRadioButtonFInstance);
	
		panelGranularity.add(jRadioButtonFType);
		panelGranularity.add(jRadioButtonFInstance);
	
		c.gridx=5;
		c.gridy=0;
		c.gridwidth=5;
		c.gridheight=3;
		panelContent.add(panelGranularity,c);
	
		//Relevant Features
		c.gridx=0;
		c.gridy=1;
		c.gridheight=2;
		JLabel jLabel2 = new javax.swing.JLabel();
		jLabel2.setText("Relevant Features");
		panelContent.add(jLabel2,c);
	
		c.gridy=3;
		c.gridwidth=3;
		c.weightx = 1.0;			
		JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
		DefaultListModel modelRF = new DefaultListModel();
		jListRF= new JList(modelRF);
		jListRF.setVisibleRowCount(4);
		jListRF.setFixedCellWidth(4);
		jListRF.addListSelectionListener(evt -> jListRFValueChanged(evt));
		jScrollPane1.setViewportView(jListRF);
		panelContent.add(jScrollPane1,c);
		validate();
	
		//Buffer
		JPanel bufPanel = new JPanel();
		bufPanel.setBorder(BorderFactory.createEtchedBorder());
		GridBagLayout gbag = new GridBagLayout();
		bufPanel.setLayout(gbag);
		GridBagConstraints c2 = new GridBagConstraints();
		c2.insets = new Insets(5,5,5,5);
	
		c2.gridx=0;
		c2.gridy=0;
		jCheckBoxBuffer= new JCheckBox();
		jCheckBoxBuffer.setSelected(true);
		jCheckBoxBuffer.setText("User Buffer (m):");		        
		bufPanel.add(jCheckBoxBuffer,c2); 
	
		c2.gridy=2;
		c2.gridheight=2;				       
		jTextFieldBuffer=new JTextField();
		jTextFieldBuffer.setPreferredSize(new Dimension(60,20));
		jTextFieldBuffer.setText("50.0");		        	        
		bufPanel.add(jTextFieldBuffer,c2);
	
		c.gridx=3;
		c.gridy=2;
		c.gridheight=2;
		c.gridwidth=2;
		panelContent.add(bufPanel,c);   
	
		//MinTimeBox	     
		JPanel mtPanel = new JPanel();
		mtPanel.setBorder(BorderFactory.createEtchedBorder());
		gbag = new GridBagLayout();
		mtPanel.setLayout(gbag);
		c2 = new GridBagConstraints();
	
		c2.gridx=0;
		c2.gridy=0;
		JLabel jLabel3 = new javax.swing.JLabel("RF Min Time (sec): ");
		mtPanel.add(jLabel3,c2);
	
		c2.gridx=0;
		c2.gridy=1;
		RFMinTime= new JTextField();
		RFMinTime.setPreferredSize(new Dimension(40,20));
		RFMinTime.setColumns(6);
		RFMinTime.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt) {//if 'tab' after entering RF mintime
				RFMinTimeFocusLost(evt);
			}
		});
		RFMinTime.addActionListener(// if 'enter' after entering RF mintime
                e -> RFMinTimeActionPerformed(e)
        );
		mtPanel.add(RFMinTime,c2);
	
		c.gridx=3;
		c.gridy=4;
		panelContent.add(mtPanel,c);
	
		//Method Panel
		JPanel panelMethod = new JPanel();
		panelMethod.setBorder(BorderFactory.createTitledBorder("Method"));
		gbag = new GridBagLayout();
		panelMethod.setLayout(gbag);
		c2 = new GridBagConstraints();
		c2.insets=new Insets(3,3,3,3);
	
		c2.gridx=0;
		c2.gridy=0;				
		c2.gridwidth=6;
		jComboBoxMethod=new JComboBox(algs);
		jComboBoxMethod.setPreferredSize(new Dimension(210,20));	
		jComboBoxMethod.addItemListener(evt -> jComboBoxMethodItemStateChanged(evt));
		panelMethod.add(jComboBoxMethod,c2);
	
		c2.gridx=0;
		c2.gridy=2;
		c2.gridwidth=3;
		JLabel jLabel4 = new javax.swing.JLabel("Parameter: ");
		panelMethod.add(jLabel4,c2);
	
		c2.gridx=3;
		JLabel jLabel5 = new javax.swing.JLabel("Value: ");
		panelMethod.add(jLabel5,c2);
	
		c2.gridx=3;
		c2.gridy=3;
		jTextFieldParam= new JTextField();
		jTextFieldParam.setPreferredSize(new Dimension(40,20));
		jTextFieldParam.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt) {
				jTextFieldParamFocusLost(evt);
			}
		});
		panelMethod.add(jTextFieldParam,c2);
	
		c2.gridx=0;
		c2.gridy=3;
		jComboBoxParam=new JComboBox();
		jComboBoxParam.setPreferredSize(new Dimension(160,20));
		jComboBoxParam.addItemListener(evt -> jComboBoxParamItemStateChanged(evt));
		panelMethod.add(jComboBoxParam,c2);
	
		c.gridx=5;
		c.gridy=3;
		panelContent.add(panelMethod,c);
		container.add(panelContent,BorderLayout.CENTER);	
	
		JPanel panelDown = new JPanel();
		panelDown.setBorder(BorderFactory.createEtchedBorder());		
	
		gridbag = new GridBagLayout();
		panelDown.setLayout(gridbag);
		c = new GridBagConstraints();
	
		c.fill = GridBagConstraints.BOTH; 			
		c.insets = new Insets(10,10,10,10);
	
		jButtonGenArffFile = new JButton("Generate Arff File..."); 
		jButtonGenArffFile.addActionListener(evt -> jButtonGenArffFileActionPerformed(evt));
		c.gridx=0;
		c.gridy=0;
		panelDown.add(jButtonGenArffFile,c);
	
		JButton jButtonOK = new javax.swing.JButton("OK");
		jButtonOK.addActionListener(evt -> OKActionPerformed(evt));
	
		c.gridx=3;
		c.gridy=0;
		c.gridwidth=2;
		panelDown.add(jButtonOK,c);			
	
		JButton jButtonCancel = new javax.swing.JButton("Close");
		jButtonCancel.addActionListener(evt -> jButtonCancelActionPerformed(evt));
		c.gridx=5;
		c.gridy=0;
		c.gridwidth=2;
		panelDown.add(jButtonCancel,c);		
	
		c.gridx=9;
		c.gridy=0;			
		panelDown.add(tm,c);
	
		container.add(panelDown,BorderLayout.SOUTH);
	
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.pack();
		jComboBoxSchema.requestFocusInWindow();				
		this.setMaximumSize(new Dimension(600,360));		
		this.setSize(680,360);
		this.setVisible(true);
	}
	
	// Interface Gr�fica Configure Trajectory Table
    private void configureActionPerformed(ActionEvent e) {
    	System.out.println("AQUI 10 : Interface Gr�fica Configure Trajectory Table");
    	int[] i = jListTrajectoryTables.getSelectedIndices();
    	if( i.length == 1){
    		Object[] temp = jListTrajectoryTables.getSelectedValuesList().toArray();
    		config.table = (String)temp[0];
            config.conn = conn;
            TrajectoryConfig tc = new TrajectoryConfig();
            tc.setConfig(config);
            tc.setVisible(true);
    	}
    	else{
    		JOptionPane.showMessageDialog(this,"Select only one Trajectory Table.");
    	}
    }
    
    // Muda a interface com base na sele��o do IB ou CB
    private void jComboBoxMethodItemStateChanged(java.awt.event.ItemEvent evt) {
    	System.out.println("AQUI 11 : Interface = Sele��o IB ou CB");
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
    
    // Checa o Buffer
    private boolean checkBufferState() {
    	System.out.println("AQUI 12 : Checar Buffer");
        try{
        	buffer = Double.valueOf(jTextFieldBuffer.getText());
        	return true;
        	
        } catch (NumberFormatException e){
        	jTextFieldBuffer.setText("50.0");
        	return false;        	        	
        }   
    }

    // Interface Gr�fica Generate Arff File
    private void jButtonGenArffFileActionPerformed(java.awt.event.ActionEvent evt) {
    	System.out.println("AQUI 13 : Interface Generate Arff File");
        GenArffFile gaf = new GenArffFile(conn,jRadioButtonFType.isSelected());
        gaf.setVisible(true);
    }
    
    // Sair da Interface
    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {
    	System.out.println("AQUI 14 : Sair");
        this.dispose();
    }
    
    // Valores dos Campos CB ao serem alterados
    @SuppressWarnings("static-access")
	private void jTextFieldParamFocusLost(java.awt.event.FocusEvent evt) {
    	System.out.println("AQUI 15 : Valores Campos CB alterados");
        Parameter p = (Parameter) jComboBoxParam.getSelectedItem();
        try {
            if (p != null) {
                if (p.type.DOUBLE == p.type) {
                    p.value = Double.valueOf(jTextFieldParam.getText());
                }else {
                    p.value = Integer.valueOf(jTextFieldParam.getText());
                }
            }
        }catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(this,"Parameter value invalid!");
        }
    }

    // Campos CB ao ser ativado
    @SuppressWarnings("static-access")
	private void jComboBoxParamItemStateChanged(java.awt.event.ItemEvent evt) {
    	System.out.println("AQUI 16 : Campos CB ativando");
        Parameter p = (Parameter) evt.getItem();
        if (p.type == p.type.DOUBLE) {
            jTextFieldParam.setText(p.value.toString());
        }else {
            jTextFieldParam.setText(((Integer)p.value).toString());
        }
    }

    private void jComboBoxMethodItemStateChanged1(java.awt.event.ItemEvent evt) {
    	System.out.println("AQUI 17");
        Method alg = (Method) jComboBoxMethod.getSelectedItem();
        jComboBoxParam.removeAllItems();
        for (int i = 0; i< (alg != null ? alg.param.size() : 0); i++) {
            jComboBoxParam.addItem(alg.param.elementAt(i));
        }
    }

    // Atribui valor para o RFMinTime ao selecionar Relevant Features
    private void jListRFValueChanged(javax.swing.event.ListSelectionEvent evt) {
    	System.out.println("AQUI 18 : Set Value RFMinTime");
        AssociatedParameter par = (AssociatedParameter) jListRF.getSelectedValue();
        if (par != null)
            RFMinTime.setText(""+par.value.intValue());
    }

    // Box do RFMinTime
    private void RFMinTimeFocusLost(java.awt.event.FocusEvent evt) {
    	System.out.println("AQUI 19 : Box RFMinTime");
        Object[] objs = jListRF.getSelectedValuesList().toArray();
        try {
            for (Object obj : objs) {
                AssociatedParameter p = (AssociatedParameter) obj;
                p.value = Integer.parseInt(RFMinTime.getText());
            }
            jTextFieldBuffer.grabFocus();
        }catch(java.lang.NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this, e.toString());
            RFMinTime.grabFocus();
        }
    }
    
    private void RFMinTimeActionPerformed(java.awt.event.ActionEvent evt) {
    	System.out.println("AQUI 20");
        Object[] objs = jListRF.getSelectedValuesList().toArray();
        try {
            for (Object obj : objs) {
                AssociatedParameter p = (AssociatedParameter) obj;
                p.value = Integer.parseInt(RFMinTime.getText());
            }
            jTextFieldBuffer.grabFocus();
        }catch(java.lang.NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this, e.toString());
            RFMinTime.grabFocus();
        }
    }
    
    // Carregar Tables do Schema selecionado
    private void LoadActionPerformed(ActionEvent evt) {
    	System.out.println("AQUI 21 : Carregar Tables");
    	try{ //load the tables in a list of auxiliary strings
            Statement s = conn.createStatement();
            ResultSet vTableName = s.executeQuery("SELECT f_table_name as tableName,type "+
                                                  "FROM geometry_columns " +
                                                  "WHERE f_table_schema=trim('"+(String) jComboBoxSchema.getSelectedItem()+"') "+
                                                  "ORDER BY tableName");
            DefaultListModel model = (DefaultListModel) jListTrajectoryTables.getModel();//Trajct Tables list
            model.removeAllElements();
            DefaultListModel model2 = (DefaultListModel) jListRF.getModel();//RF list
            model2.removeAllElements();                        
            while ( vTableName.next() )  {/* creates a new table for each table that has objects with topological relation to vRegion */
                model2.addElement(new AssociatedParameter(vTableName.getString("tableName"),vTableName.getString("type")));// RFs
            	model.addElement(vTableName.getString(1));//Traject tables
            }	
        }catch (Exception vErro){
            vErro.printStackTrace();
        }
    
    }
    
    // Valor do Buffer
    private void OKActionPerformed(ActionEvent evt)  {
    	System.out.println("AQUI 22 : Buffer Valor");
    	if(jCheckBoxBuffer.isSelected()){
    		if(checkBufferState()){    		
    			System.out.println("Buffer of "+buffer+" saved.");
    		}
    		else{
    			JOptionPane.showMessageDialog(this,"Buffer expects a number.");
    			return;
    		}
    	} else {
    		this.buffer = 50.0; // default value 
    	}
    	
    	if (jListRF.getSelectedIndex() == -1 &&
        		jComboBoxMethod.getSelectedItem().toString().compareTo("CB-SMoT")!=0){//cause CB-SMoT has a version without RFs
        	//cause DB-SMoT has a version without RFs too !
            JOptionPane.showMessageDialog(this,"Select one or more relevant features.");
            return;
        }
        
        if(jListTrajectoryTables.getSelectedIndex() == -1){
        	JOptionPane.showMessageDialog(this,"Select one or more trajectory table.");
            return;
        }        
        
        //get the thing in 'things', those trajectories tables to be executed 
        Object[] objs = jListTrajectoryTables.getSelectedValuesList().toArray();
        String[] str = new String [objs.length];
        for(int i=0;i<objs.length;i++){
                str[i]=(String)objs[i];
        }
		
        //controls if SRID of RFs are different from trajectories...
		// ALL the trajectories should have the SAME srid
		// it is checked ahead in the foreach.
        config.table = str[0];
        String error = checkSRIDs();//att the variable 'table_srid'        
        if(error.compareTo("")!=0){
        	JOptionPane.showMessageDialog(this,error);
            return;
        }

        //for each of the trajectory table selected...
        for(int count =0; count<str.length;){
            java.util.Date tempo,fim,ini = new java.util.Date();
            config.table = str[count];
            try {
                //trajectory srid checking, has to be the same of all the other trajectory-tables and RFs
                Statement sn = conn.createStatement();
                ResultSet rsn = sn.executeQuery("select srid from geometry_columns where f_table_name='"+config.table+"'");
                rsn.next();
                if(table_srid!=rsn.getInt("srid")){
                        throw new Exception("SRID incompatiblities. Trajectory table "+config.table+" should be changed.");
                }
                //end of srid checking
                System.out.println("Creating tables...");
                createTables();
                System.out.println("Processing the trajectories...");

                loadTrajectories(config.table);
                fim = new java.util.Date();
                tempo = new java.util.Date(fim.getTime()-ini.getTime());
                //insertCleanTrajProcess(tempo.getTime());
                count++;
                if (count == str.length){
                    System.out.println("Processing time: " +tempo.getTime()+" ms");
                    JOptionPane.showMessageDialog(this,"Operation finished succesfully.");
                }
            }catch(Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,"Error during operation");
                System.out.println("Error: \n"+e.getMessage());
            }
            finally{
            }
            //@Hercules, comented 30-08-2010.
//	        if(objs.length>1){
//	        	//if we have others tables of trajectories,
//	        	//we should save the stops-table with another name to re-start the method
//				Statement st= conn.createStatement();
//				//save stops table
//				try{
//					st.execute("create table stops_"+s+" as (select * from stops);");
//					System.out.println("stops_"+s+" created sucessfully.");
//				}
//				catch(SQLException e){
//					st.execute("create table stops_"+s+"_"+tempo.getTime()+" as (select * from stops);");
//					System.out.println("stops_"+s+"_"+tempo.getTime()+" created sucessfully.");
//				}
//	        }
        }//endof foreach
        Runtime.getRuntime().gc();
    }
    
    // SRID
    private String checkSRIDs() {
    	System.out.println("AQUI 23 : SRID");
        Statement sn;
		try {
			//getting trajectory SRID
			sn = conn.createStatement();
			
			ResultSet rsn = sn.executeQuery("select srid from geometry_columns where f_table_name='"+config.table+"'");
	        rsn.next();
	        table_srid=rsn.getInt("srid");
	        
	        //getting all the RFs
	        Object[] objs = jListRF.getSelectedValues();                    
	        AssociatedParameter[] relevantFeatures = new AssociatedParameter[objs.length];                    
	        for (int i=0;i<objs.length;i++) {
	            relevantFeatures[i] = (AssociatedParameter) objs[i];
	        }
	        //comparing their SRIDs with the trajectory
	        for(AssociatedParameter a:relevantFeatures){
	        	sn = conn.createStatement();
				rsn = sn.executeQuery("select srid from geometry_columns where f_table_name='"+config.table+"'");
		        rsn.next();
		        if(table_srid!=rsn.getInt("srid")){
		        	return "Error in the SRID of table: "+a.name;
		        }
	        }
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
    	return "";
	}

    // Interface Gr�fica Visualization
    //@autor: Bruno
	protected String showDadosGeograficos() {
		System.out.println("AQUI 24 : Interface Visualization");
		try {
			//getting trajectory SRID
			
			Statement s = conn.createStatement();

            System.out.println("+++++++++++++++++++++");
			if (jListTrajectoryTables.getSelectedIndex() != -1){
				 ResultSet vTableName = s.executeQuery("SELECT f_table_name as tableName,type "+
						"FROM geometry_columns " +
						"WHERE f_table_schema=trim('"+ jComboBoxSchema.getSelectedItem()+"') "+
				 		"ORDER BY tableName");
				 int indexAtualVT=0;
				 while ( vTableName.next() ) {
					 if(indexAtualVT==jListTrajectoryTables.getSelectedIndex()){
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
						 
						 rep.loadPoints(vTableName.getString("tableName"),Color.BLUE,2);
						 
						 rep.paintAll(f.getGraphics());
						 
//						// JUST FOR TESTING
//						f.addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e) {
//							System.exit(0);
//							}});
//						// JUST FOR TESTING
					 }
					 indexAtualVT++;

				 }	

			}else{
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
			}

                        System.out.println("---------------------");
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();

		}
		return "";
	}
	
	// Formatar Nome dos Par�metros
//----------------------------------------------------------------------------------------------------------------
// INTERFACE ENDS
//----------------------------------------------------------------------------------------------------------------    



private void filterActionPerformed(ActionEvent e) {
	System.out.println("AQUI 25");
    String esquema = (String)jComboBoxSchema.getSelectedItem();
    int[] i = jListTrajectoryTables.getSelectedIndices();
    if (i.length >= 1) {
        List<String> listaTab = new ArrayList<>();
        Object[] temp = jListTrajectoryTables.getSelectedValues();
        for(Object ob: temp){
            listaTab.add((String) ob);
        }
        TrajectoryClean tc = new TrajectoryClean(conn, listaTab);
        tc.setConfig(this.config);
        tc.setEsquema(esquema);
        tc.setVisibleFrame(true);
    } else {
        JOptionPane.showMessageDialog(this, "Select one or more Trajectory Table.");
    }
}
	public String formatNameParameter(String nm){
	System.out.println("AQUI 26 : Formatar Nome dos Par�metros");
        System.out.println("Parm = "+nm);
        if(nm.trim().equalsIgnoreCase("MaxAvgSpeed")){
            return "as";
        }else if(nm.trim().equalsIgnoreCase("MinAvgSpeed")){
            return "as";
        }else if(nm.trim().equalsIgnoreCase("MinTime (seconds)")){
            return "mt";
        }else if(nm.trim().equalsIgnoreCase("MaxSpeed")){
            return "ms";
        }else if(nm.trim().equalsIgnoreCase("MinDirChange (degrees)")){
            return "md";
        }else if(nm.trim().equalsIgnoreCase("MaxTolerance (points)")){
            return "mt";
        }else if(nm.trim().equalsIgnoreCase("MinTimeVar (seconds)")){
            return "mtv";
        }else if(nm.trim().equalsIgnoreCase("MinTimeSpeed (seconds)")){
            return "mts";
        }
        return nm;
    }
    
	// ??? Cluster par�metros Str
	public String parametersClusterStr(){
    	System.out.println("AQUI 27 : ??? Par�metros Cluster Str ???");
        StringBuilder str = new StringBuilder();
        for(Parameter param :parametersCluster()){
            if(param.name!=null && !param.name.equals("") && param.value!=null){
                str.append("_").append(formatNameParameter(param.name)).append("_").append(param.value.toString());
            }
        }
        return str.toString().toLowerCase().replace(".", "_")
                .replace(",", "_").replace(" ", "")
                .replace("(degrees)", "")
                .replace("(seconds)", "")
                .replace("(points)", "");
    }


    // ??? Cluster par�metros
    public List<Parameter> parametersCluster(){
    	System.out.println("AQUI 28 : ??? Par�metros Cluster ???");
        List<Parameter> list = new ArrayList<>();
        int tamParans = 0;
        while(tamParans < this.jComboBoxParam.getModel().getSize()){
            Parameter param = (Parameter)this.jComboBoxParam.getModel().getElementAt(tamParans);
            list.add(param);
            tamParans++;
        }
        return list;
    }
    
    // Nome da tabela de stop

    public String nameTableStop(String sp){
    	System.out.println("AQUI 29 : Nome da tabela de stop");
        return "stops_".concat(sp.concat(parametersClusterStr()));
    }

    private static String currentNameTableStop;

    // Get Nome atual da table stop
    public static String getCurrentNameTableStop() {
    	System.out.println("AQUI 30 : Get nome da atual table stop");
        if(currentNameTableStop==null || currentNameTableStop.equals("")){
            return "stops";
        }
        return currentNameTableStop;
    }

    // Set Nome atual da table stop
    private static void setCurrentNameTableStop(String currentNameTableStop) {
    	System.out.println("AQUI 31 : Set nome da atual table stop");
        TrajectoryFrame.currentNameTableStop = currentNameTableStop;
    }

    public void insertCleanTrajProcess(long timeProcess){
    	System.out.println("AQUI 32");
        Object[] objs = jListRF.getSelectedValuesList().toArray();
        AssociatedParameter[] relevantFeatures = new AssociatedParameter[objs.length];
        Integer rfMinTime = null;
        if(relevantFeatures.length > 0){
            rfMinTime = relevantFeatures[0].value;
        }
        Double avgSpeed = null;
        Integer minTime = null;
        Double maxSpeed = null;
        int qtidadeStop = 0;

        for(Parameter param: parametersCluster()){
            try{
                if(param.name.equalsIgnoreCase("MaxAvgSpeed")){
                    avgSpeed = (Double)param.value;
                }else if(param.name.equalsIgnoreCase("MinAvgSpeed")){
                    avgSpeed = (Double)param.value;
                }else if(param.name.equalsIgnoreCase("MinTime (seconds)")){
                    minTime = (Integer)param.value;
                }else if(param.name.equalsIgnoreCase("MaxSpeed")){
                    maxSpeed = (Double)param.value;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        String sqlCount = "select count(*) from "+TrajectoryFrame.getCurrentNameTableStop();
        System.out.println(sqlCount);
        try{
            PreparedStatement psCount = conn.prepareStatement(sqlCount);
            ResultSet resultSet = psCount.executeQuery();
            if(resultSet.next()){
                qtidadeStop = resultSet.getInt(1);
            }
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }


        StringBuilder sql = new StringBuilder();
            sql.append(" INSERT INTO cleantrajprocess( ");
            sql.append("     oidstop, nomestop, tempostop, paramrfmintime, paramminavgspeed,  ");
            sql.append(" parammintime, parammaxspeed, qtidadestop) ");
            sql.append(" VALUES (null, ?, ?, ");
            sql.append(rfMinTime==null?" null,":" ?, ");
            sql.append(avgSpeed ==null?" null,":" ?, ");
            sql.append(minTime  ==null?" null,":" ?, ");
            sql.append(maxSpeed ==null?" null,":" ?, ");
            sql.append(" ? ) ");


        try {

            System.out.println(sql.toString());
            int i=1;
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            ps.setString(i++, TrajectoryFrame.getCurrentNameTableStop());
            ps.setLong  (i++, timeProcess);
            if(rfMinTime!=null){
                ps.setInt   (i++, rfMinTime);
            }
            if(avgSpeed!=null){
                ps.setDouble(i++, avgSpeed);
            }
            if(minTime!=null){
                ps.setInt   (i++, minTime);
            }
            if(maxSpeed!=null){
                ps.setDouble(i++  , maxSpeed);
            }
            ps.setInt(i, qtidadeStop);
            ps.execute();
        } catch (SQLException ex) {
            Logger.getLogger(TrajectoryFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}