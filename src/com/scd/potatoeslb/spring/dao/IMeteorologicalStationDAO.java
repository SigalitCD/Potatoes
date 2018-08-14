package com.scd.potatoeslb.spring.dao;

import java.util.List;

import com.scd.potatoeslb.data.MeteorologicalStation;

public interface IMeteorologicalStationDAO {
	MeteorologicalStation getStationById(int id);
	List<MeteorologicalStation> getAllStations();
	List<MeteorologicalStation> getActiveStations();
	boolean deleteStation(int id);
	boolean updateStation(MeteorologicalStation report);
	boolean createStation(MeteorologicalStation report);
}
