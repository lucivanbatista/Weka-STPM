package weka.gui.stpm;

public class Interc implements Comparable<Interc>{
	public int pt;//gid from point 'pt'
	public int gid; // gid from rf 
	public String rf;// rf_name
	public int value;//rf min_time value
	public String amenity;
	
	public Interc(){}
	
	public Interc(int ponto,int gidn,String rfn,int val){
		pt=ponto;
		gid=gidn;
		rf=rfn;
		value=val;
	}
	
	public Interc(int ponto,int gidn,String rfn,int val, String amenity){
		pt=ponto;
		gid=gidn;
		rf=rfn;
		value=val;
		this.amenity = amenity;
	}
	
	public int compareTo(Interc o) {
		if(this.pt > o.pt) 
			return 1;
		else if(this.pt < o.pt)
			return -1;
		else return 0;
	}

	public boolean compareToByGids(Interc intercept) {
		return ((intercept.pt == this.pt) && (intercept.gid == this.gid));
	}
}
