package weka.gui.stpm;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.SQLException;

public class IBSMoT extends Method {
    private Double buffer;
    private Config config;
    private boolean enableBuffer;
    private boolean enableFType;
    BufferedWriter fr;

    void setInformation(Double buffer, Config config, boolean enableBuffer, boolean enableFType,  BufferedWriter fr) {
        this.buffer = buffer;
        this.config = config;
        this.enableBuffer = enableBuffer;
        this.enableFType = enableFType;
        this.fr = fr;
    }

    @Override
    public void run(Trajectory t, InterceptsG in, String targetFeature) {
    	try {
//	        long initialTime = System.currentTimeMillis();
	       fr.write("Processing trajectory " + t.tid);
	       fr.newLine();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        
        try {
            TrajectoryMethods.smot2(enableBuffer, buffer, config, t, enableFType, in);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
//        long tempo = System.currentTimeMillis() - initialTime;
//        System.out.println("Processing time: " + tempo + " ms");
    }

    public String toString() {
        return "SMoT";
    }
}
