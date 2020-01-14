package com.scd.potatoeslb.risks;

import java.time.LocalDateTime;

import org.json.JSONArray;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.scd.potatoeslb.ApplicationContextProvider;
import com.scd.potatoeslb.data.Meteorology;
import com.scd.potatoeslb.spring.dao.IMeteorologyDAO;

public class RiskMapManager {

	private static RiskMapManager instance;
	private AnnotationConfigApplicationContext context = ApplicationContextProvider.getApplicationContext();
	private IMeteorologyDAO meteorologyDAO = context.getBean(IMeteorologyDAO.class);
	private RiskMap riskMap;
	
	// constructor
	private RiskMapManager() {
		riskMap = new RiskMap();
	}
	
	// singleton
	public static RiskMapManager getRiskMapManager() {
		if ( instance == null ) {
			instance = new RiskMapManager();
		}
		return instance;
	}
	
	public void refreshRiskMap() {
		
		LocalDateTime from = LocalDateTime.now().minusHours(Meteorology.HUMIDITY_SAMPLE_HOURS + Meteorology.WIND_SAMPLE_HOURS);
		LocalDateTime to = LocalDateTime.now().minusHours(Meteorology.WIND_SAMPLE_HOURS);
		
		if ( meteorologyDAO.isHumid(Meteorology.MIN_HUMIDITY_PERCENT, from, to) ) { 
			// calculate a new risk map
			System.out.println("calculating risk map..");
			riskMap.updateRisks();
			System.out.println("..finished calculating risk map");
		} else { // TODO: remove "else" block after debug
			System.out.println("calculating risk map..");
			riskMap.updateRisks();
			System.out.println("..finished calculating risk map");
		}
	}
	
	public JSONArray getRiskMap() {
		return riskMap.getJsonRiskMap();
	}
	
	
}
