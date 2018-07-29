package weka.gui.stpm;

import java.util.Vector;

public class CBSMoT extends Method {

	private Double buffer;
	private Config config;
	private boolean jCheckBoxBuffer;
	private boolean jRadioButtonFType;
	private int table_srid;
    private int maxSelectionIndex;

    void setInformation(Double buffer, Config config, boolean jCheckBoxBuffer, boolean jRadioButtonFType, Integer table_srid, int maxSelectionIndex) {
		this.buffer = buffer;
		this.config = config;
		this.jCheckBoxBuffer = jCheckBoxBuffer;
		this.jRadioButtonFType = jRadioButtonFType;
		this.table_srid = table_srid;
        this.maxSelectionIndex = maxSelectionIndex;
	}

	@Override
    public void run(Trajectory t, InterceptsG in, String targetFeature) {
		//load the Parameter Vector of the method class
        Parameter avg = param.elementAt(0);
        Parameter minTime = param.elementAt(1);
        Parameter speedLimit = param.elementAt(2);

		double SL = ((Double) speedLimit.value).doubleValue();
		int minTimeMili = ((Integer) minTime.value).intValue() * 1000;

		java.util.Date ini = new java.util.Date();

		System.out.println("\t\tStarting Trajectory "+t.tid+"\n\t\tavg= "+((Double) avg.value).doubleValue()+" ;\n\t\tminTime= "+minTimeMili+" ;\n\t\tSL= "+SL+" ; ");

		// the clustering method, which will use the points in 't'	
		Vector<ClusterPoints> clusters = TrajectoryMethods.speedClustering(t,((Double) avg.value).doubleValue(),minTimeMili,SL);                    

		java.util.Date fim = new java.util.Date();
		java.util.Date tempo = new java.util.Date(fim.getTime()-ini.getTime());
		System.out.println("Clusterization: " +tempo.getTime()+" ms");

		//starts to apply semantics...
		ini = new java.util.Date();
        if (maxSelectionIndex == -1) {//not RF selected
			// save in the stops-table the clusters founded (as unknowns);
			NewTrajectoryMethods.saveStopsClusters(this.jCheckBoxBuffer,clusters,config,minTimeMili,buffer,t.getSRID(),false);
		}
		else{
			//or attribute semantic given a list of RFs
			TrajectoryMethods.stopsDiscoveryFaster(this.jCheckBoxBuffer,buffer,clusters,config,minTimeMili,this.jRadioButtonFType,in,this.table_srid,false);
		}
		fim = new java.util.Date();
		tempo = new java.util.Date(fim.getTime()-ini.getTime());
		System.out.println("Semantics Application: " +tempo.getTime()+" ms");
	}


	public String toString() {
		return "CB-SMoT";
	}

}
