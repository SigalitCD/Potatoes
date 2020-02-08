package com.scd.potatoeslb.data;

import java.time.LocalDateTime;

public class Meteorology {
	
	public static final int MIN_HUMIDITY_PERCENT = 60; // TODO: should be 90 // TODO: properties file
	public static final int FIRST_HUMIDITY_SAMPLE_PERIOD_HOURS = 10; // TODO: should be 10
	public static final int SECOND_HUMIDITY_SAMPLE_PERIOD_HOURS = 10; // TODO: should be 10
	public static final int WIND_SAMPLE_HOURS = 3; // TODO: should be 3

	private int id;
	private int stationId;
	private LocalDateTime sampleTime;
	private boolean isValid;
	private int relativeHumidity;
	private int windDirection;
	
	public Meteorology() {
	}
	
	public Meteorology(int id, int stationId, LocalDateTime sampleTime, boolean isValid, int relativeHumidity, int windDirection) {
		this.id = id;
		this.stationId = stationId;
		this.sampleTime = sampleTime;
		this.isValid = isValid;
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
	
	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	@Override
	public String toString() {
		return "Meteorology [id=" + id + ", stationId=" + stationId + ", sampleTime=" + sampleTime + ", isValid=" + isValid
				+ ", relativeHumidity=" + relativeHumidity + ", windDirection=" + windDirection + "]";
	}

	
}
