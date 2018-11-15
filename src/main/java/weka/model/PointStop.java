package weka.model;

import java.sql.Timestamp;

public class PointStop {
	public int tid; // Identificador do Objeto
	public double latitude;
	public double longitude;
	public Timestamp time; // Tempo do Objeto
	public int edge_id; 
	public int gid; // Serial Ponto do Objeto
	public int gid_stop; // Serial do Stop
	public int stop_gid; // Identificador único do POI
    public Timestamp startTime;
    public Timestamp endTime; 
    public String rf; // Amenity / Tipo de ponto de interesse
    
    public PointStop() {
    	
    }
    
    public PointStop(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
    }   
    
	public PointStop(int tid, double latitude, double longitude, Timestamp time, int edge_id, int gid, int gid_stop,
			int stop_gid, Timestamp startTime, Timestamp endTime, String rf) {
		this.tid = tid;
		this.latitude = latitude;
		this.longitude = longitude;
		this.time = time;
		this.edge_id = edge_id;
		this.gid = gid;
		this.gid_stop = gid_stop;
		this.stop_gid = stop_gid;
		this.startTime = startTime;
		this.endTime = endTime;
		this.rf = rf;
	}
	
	public int getTid() {
		return tid;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}
	public int getEdge_id() {
		return edge_id;
	}
	public void setEdge_id(int edge_id) {
		this.edge_id = edge_id;
	}
	public int getGid() {
		return gid;
	}
	public void setGid(int gid) {
		this.gid = gid;
	}
	public int getGid_stop() {
		return gid_stop;
	}
	public void setGid_stop(int gid_stop) {
		this.gid_stop = gid_stop;
	}
	public int getStop_gid() {
		return stop_gid;
	}
	public void setStop_gid(int stop_gid) {
		this.stop_gid = stop_gid;
	}
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	public Timestamp getEndTime() {
		return endTime;
	}
	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	public String getRf() {
		return rf;
	}
	public void setRf(String rf) {
		this.rf = rf;
	}
}
