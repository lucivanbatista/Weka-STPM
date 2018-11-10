/*
 * Config.java
 *
 * Created on 8 de Agosto de 2007, 16:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package weka.gui.stpm;

import java.sql.*;
/**
 *
 * @author Administrador
 */
public class Config {
    public Connection conn;
    public String table;
    public String tid;
    public String time;
	public String limit;
	public String latitude;
	public String longitude;
	public String poi; // Point Of Interest
	public String schema; // Schema
	public Boolean ftype = false; // Type (true) or Instance (false), padr�o � false
	public Double userBuff;
	public Integer rfMinTime;
	public String method;
	// CB parameters: 
	public Double maxAvgSpeed;
	public Integer minTime;
	public Double maxSpeed;
	
    
    /** Creates a new instance of Config */
    public Config() {
    }
    
    public Config(String schema, String trajectoryTable, String trajectoryId, String detectionTime, String pointsInterest, Double userBuff, Integer rfMinTime,
    		String methodName, Double maxAvgSpeed, Integer minTime, Double maxSpeed) {
    	this.table = trajectoryTable;
    	this.tid = trajectoryId;
    	this.time = detectionTime;
    	this.poi = pointsInterest; 
    	this.schema = schema;
    	this.userBuff = userBuff;
    	this.rfMinTime = rfMinTime;
    	this.method = methodName;
    	this.maxAvgSpeed = maxAvgSpeed;
    	this.minTime = minTime;
    	this.maxSpeed = maxSpeed;
    }

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPoi() {
		return poi;
	}

	public void setPoi(String poi) {
		this.poi = poi;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public Double getUserBuff() {
		return userBuff;
	}

	public void setUserBuff(Double userBuff) {
		this.userBuff = userBuff;
	}

	public Integer getRfMinTime() {
		return rfMinTime;
	}

	public void setRfMinTime(Integer rfMinTime) {
		this.rfMinTime = rfMinTime;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Double getMaxAvgSpeed() {
		return maxAvgSpeed;
	}

	public void setMaxAvgSpeed(Double maxAvgSpeed) {
		this.maxAvgSpeed = maxAvgSpeed;
	}

	public Integer getMinTime() {
		return minTime;
	}

	public void setMinTime(Integer minTime) {
		this.minTime = minTime;
	}

	public Double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(Double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
}
