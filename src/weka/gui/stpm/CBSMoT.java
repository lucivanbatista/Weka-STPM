package weka.gui.stpm;

import java.util.Vector;
import java.util.logging.Logger;

public class CBSMoT extends Method {

	private Double buffer;
	private Config config;
	private boolean enableBuffer;
	private boolean enablePreProcessByTypeOrInstanceGranularity;
	private int table_srid;
    private int maxSelectionIndex;

    void setInformation(Double buffer, Config config, boolean enableBuffer, boolean enablePreProcessByTypeOrInstanceGranularity, Integer sRID, int maxSelectionIndex) {
		this.buffer = buffer;
		this.config = config;
		this.enableBuffer = enableBuffer;
		this.enablePreProcessByTypeOrInstanceGranularity = enablePreProcessByTypeOrInstanceGranularity;
		this.table_srid = sRID;
        this.maxSelectionIndex = maxSelectionIndex;
	}

	@Override
    public void run(Trajectory t, InterceptsG in, String targetFeature) {

		LOG.info("Starting Trajectory " + t.tid);

		// the clustering method, which will use the points in 't'	
		Vector<ClusterPoints> clusters = TrajectoryMethods.speedClustering(t, parameters.avgSpeed, parameters.minTime * 1000, parameters.speedLimit);

		// Starts to apply semantics...
        if (maxSelectionIndex == -1) { // not RF selected
			// save in the stops-table the clusters founded (as unknowns);
			NewTrajectoryMethods.saveStopsClusters(this.enableBuffer,clusters,config,parameters.minTime * 1000,buffer,t.getSRID(),false);
		} else{
			TrajectoryMethods.stopsDiscoveryFaster(this.enableBuffer,buffer,clusters,config,parameters.minTime * 1000,this.enablePreProcessByTypeOrInstanceGranularity,in,this.table_srid,false);
		}

	}

	private static Logger LOG = Logger.getLogger(CBSMoT.class.getName());

}
