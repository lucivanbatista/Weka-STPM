package weka.gui.stpm;

import java.sql.SQLException;
import java.util.logging.Logger;

public class IBSMoT extends Method {
    private final Logger LOG = Logger.getLogger("SMoT");
    private Double buffer;
    private Config config;
    private boolean enableBuffer;
    private boolean enableFType;

    void setInformation(Double buffer, Config config, boolean enableBuffer, boolean enableFType) {
        this.buffer = buffer;
        this.config = config;
        this.enableBuffer = enableBuffer;
        this.enableFType = enableFType;
    }

    @Override
    public void run(Trajectory t, InterceptsG in, String targetFeature) {
        long initialTime = System.currentTimeMillis();
        LOG.info("Processing trajectory " + t.tid);

        try {
            TrajectoryMethods.smot2(enableBuffer, buffer, config, t, enableFType, in);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        long tempo = System.currentTimeMillis() - initialTime;
        LOG.info("Processing time: " + tempo + " ms");
    }

    public String toString() {
        return "SMoT";
    }
}
