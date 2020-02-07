package com.scd.potatoeslb.data;

import java.time.LocalDateTime;

public class MeteorologicalStation {
	private int id;
	private String name;
	private int imsId;
	private int rhChannel;
	private int wdChannel;
	private boolean isActive;
	private LocalDateTime lastUpdateTime;
	
	public MeteorologicalStation() {
	}

	public MeteorologicalStation(int id, String name, int imsId, int rhChannel, int wdChannel, LocalDateTime lastUpdateTime) {
		this.id = id;
		this.name = name;
		this.imsId = imsId;
		this.rhChannel = rhChannel;
		this.wdChannel = wdChannel;
		this.lastUpdateTime = lastUpdateTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getImsId() {
		return imsId;
	}

	public void setImsId(int imsId) {
		this.imsId = imsId;
	}

	public int getRhChannel() {
		return rhChannel;
	}

	public void setRhChannel(int rhChannel) {
		this.rhChannel = rhChannel;
	}

	public int getWdChannel() {
		return wdChannel;
	}

	public void setWdChannel(int wdChannel) {
		this.wdChannel = wdChannel;
	}
	
	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public LocalDateTime getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	@Override
	public String toString() {
		return "MeteorologicalStation [id=" + id + ", name=" + name + ", imsId=" + imsId + ", rhChannel=" + rhChannel
				+ ", wdChannel=" + wdChannel + ", lastUpdateTime=" + lastUpdateTime + "]";
	}
	
}
