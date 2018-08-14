package com.scd.potatoeslb.data;

import java.time.LocalDateTime;

public class Meteorology {
	private int id;
	private int stationId;
	private LocalDateTime sampleTime;
	private int relativeHumidity;
	private int windDirection;
	
	public Meteorology() {
	}
	
	public Meteorology(int id, int stationId, LocalDateTime sampleTime, int relativeHumidity, int windDirection) {
		this.id = id;
		this.stationId = stationId;
		this.sampleTime = sampleTime;
		this.relativeHumidity = relativeHumidity;
		this.windDirection = windDirection;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStationId() {
		return stationId;
	}

	public void setStationId(int stationId) {
		this.stationId = stationId;
	}

	public LocalDateTime getSampleTime() {
		return sampleTime;
	}

	public void setSampleTime(LocalDateTime sampleTime) {
		this.sampleTime = sampleTime;
	}

	public int getRelativeHumidity() {
		return relativeHumidity;
	}

	public void setRelativeHumidity(int relativeHumidity) {
		this.relativeHumidity = relativeHumidity;
	}

	public int getWindDirection() {
		return windDirection;
	}

	public void setWindDirection(int windDirection) {
		this.windDirection = windDirection;
	}

	@Override
	public String toString() {
		return "Meteorology [id=" + id + ", stationId=" + stationId + ", sampleTime=" + sampleTime
				+ ", relativeHumidity=" + relativeHumidity + ", windDirection=" + windDirection + "]";
	}

	
}
