package weka.gui.stpm;

import java.io.IOException;
import java.sql.SQLException;

public class SMOT extends Method{
	
	private Double buffer;
	private Config config;
	private boolean jCheckBoxBuffer;
	private boolean jRadioButtonFType;
	
	public void setInformations(Double buffer, Config config, boolean jCheckBoxBuffer, boolean jRadioButtonFType){
		this.buffer = buffer;
		this.config = config;
		this.jCheckBoxBuffer = jCheckBoxBuffer;
		this.jRadioButtonFType = jRadioButtonFType;
	}

	@Override
	public void run(Trajectory t, InterceptsG in, String targetFeature, InterceptsG streets) throws SQLException, IOException {
		java.util.Date ini = new java.util.Date();
        System.out.println("Processing trajectory "+t.tid);
        TrajectoryMethods.smot2(this.jCheckBoxBuffer, buffer, config, t, this.jRadioButtonFType, in);
        
        java.util.Date fim = new java.util.Date();
        java.util.Date tempo = new java.util.Date(fim.getTime()-ini.getTime());
        System.out.println("\tProcessing time: "+tempo.getTime()+" ms");
		
	}

	@Override
	public void run2(Trajectory t, InterceptsG in, String targetFeature) throws SQLException {
		//TrajectoryMethods.smot(jCheckBoxBuffer.isSelected(), buffer, config, t, targetFeature, relevantFeatures, featureType);
	}
	
    public String toString() {
            return "SMoT";
    }    
}
