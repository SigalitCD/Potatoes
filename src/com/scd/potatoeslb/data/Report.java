package com.scd.potatoeslb.data;

import java.time.LocalDateTime;

public class Report {
	private int id;
	private int farmerId;
	private LocalDateTime reportTime;
	private String latitude;
	private String longitude;
	
	public Report() {
	}
	
	public Report(int id, int farmerId, LocalDateTime reportTimestamp, String latitude, String longitude) {
		this.id = id;
		this.farmerId = farmerId;
		this.reportTime = reportTimestamp;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFarmerId() {
		return farmerId;
	}
	public void setFarmerId(int farmerId) {
		this.farmerId = farmerId;
	}
	public LocalDateTime getReportTime() {
		return reportTime;
	}
	public void setReportTime(LocalDateTime reportTime) {
		this.reportTime = reportTime;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return "Report{" + "id=" + id + ", farmerId='" + farmerId + '\'' + ", reportTimestamp='" + reportTime
				+ ", latitude=" + latitude + ", longitude=" + longitude + '\'' + '}';
	}	
}
