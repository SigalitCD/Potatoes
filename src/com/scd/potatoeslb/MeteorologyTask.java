package com.scd.potatoeslb;

import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.scd.potatoeslb.data.MeteorologicalStation;
import com.scd.potatoeslb.spring.dao.IMeteorologicalStationDAO;

public class MeteorologyTask {
	
	private boolean bInitialized = false;
	private List<MeteorologicalStation> stationsList;
	private AnnotationConfigApplicationContext context = ApplicationContextProvider.getApplicationContext();

	
	public void executeTask() {
		init();
		// for each station, retrieve data and insert row to database
	}
	
	private void init() {
		// retrieve configuration: stations list from database
		// for each station, retrieve meta-data from the Meteorological Service and update database
		
	}

	private void getStationsList() {
		IMeteorologicalStationDAO stationDAO = context.getBean(IMeteorologicalStationDAO.class);
		stationsList = stationDAO.getActiveStations();
	};

	private void updateChannels() {
		
	};
	
	
}
