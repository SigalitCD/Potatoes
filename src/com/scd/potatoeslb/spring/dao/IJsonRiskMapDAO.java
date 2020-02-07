package com.scd.potatoeslb.spring.dao;

import java.util.List;

import org.json.JSONArray;

import com.scd.potatoeslb.data.Farmer;

public interface IJsonRiskMapDAO {
	String getLatestRiskMap();
	boolean deleteRiskMap(Long id);
	boolean updateRiskMap(JSONArray jsonRiskMap);
	boolean createRiskMap(String jsonRiskMapStr);
}
