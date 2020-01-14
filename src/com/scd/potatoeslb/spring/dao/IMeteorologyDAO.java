package com.scd.potatoeslb.spring.dao;

import java.time.LocalDateTime;
import java.util.List;

import com.scd.potatoeslb.data.Meteorology;

public interface IMeteorologyDAO {
	boolean isHumid( int minHumidity, LocalDateTime from, LocalDateTime to );
	Meteorology getMeteorologyById(Long id);
	List<Meteorology> getAllMeteorologies();
	List<Meteorology> getMeteorologiesByTimeInterval( LocalDateTime from, LocalDateTime to );
	boolean deleteMeteorology(Long id);
	boolean updateMeteorology(Meteorology meteorology);
	boolean createMeteorology(Meteorology meteorology);
	
}
