package com.scd.potatoeslb.spring.dao;

import java.util.List;

import com.scd.potatoeslb.data.Meteorology;

public interface IMeteorologyDAO {
	Meteorology getMeteorologyById(Long id);
	List<Meteorology> getAllMeteorologies();
	boolean deleteMeteorology(Long id);
	boolean updateMeteorology(Meteorology meteorology);
	boolean createMeteorology(Meteorology meteorology);
}
