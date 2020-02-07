package com.scd.potatoeslb.risks;

import java.time.LocalDateTime;

import org.json.JSONArray;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.scd.potatoeslb.ApplicationContextProvider;
import com.scd.potatoeslb.data.Meteorology;
import com.scd.potatoeslb.spring.dao.IJsonRiskMapDAO;
import com.scd.potatoeslb.spring.dao.IMeteorologyDAO;

public class RiskMapManager {

	private static final int FIRST_DAY_OF_SEASON1 = 60;// 60; // day in the year TODO: properties file
	private static final int LAST_DAY_OF_SEASON1 = 243; // day in the year
	private static final int FIRST_DAY_OF_SEASON2 = 244;// day in the year
	private static final int LAST_DAY_OF_SEASON2 = 59; // day in the year
	private static final int REPORT_MONTHS_AGO = 6; // default value in case now is no season's time

	
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
		LocalDateTime windDirectionSamplePeriodStart = checkHumidityConditions();
		if( windDirectionSamplePeriodStart != null) {
			// calculate a new risk map
			System.out.println("calculating risk map..");
			riskMap.updateRisks(windDirectionSamplePeriodStart);		
			System.out.println("..finished calculating risk map");
		} 
	}
	
	/*
	 * Check if in the last 23 hours existed two periods of 10 hours of high humidity, with 3 hours difference between them.
	 * If so, return the beginning of the wind sample period. Else, return null; 
	 */
	private LocalDateTime checkHumidityConditions() {
		LocalDateTime now = LocalDateTime.now();
		// check first humidity sample period (23 hours ago)
		LocalDateTime from = now.minusHours(Meteorology.FIRST_HUMIDITY_SAMPLE_PERIOD_HOURS + Meteorology.WIND_SAMPLE_HOURS + Meteorology.SECOND_HUMIDITY_SAMPLE_PERIOD_HOURS);
		LocalDateTime to = now.minusHours(Meteorology.WIND_SAMPLE_HOURS + Meteorology.SECOND_HUMIDITY_SAMPLE_PERIOD_HOURS);
		if ( !meteorologyDAO.isHumid(Meteorology.MIN_HUMIDITY_PERCENT, from, to) ) {
			System.out.println( "Humidity level was calculated from " + from + " to " + to + " and was found not humid.");
			return null;
		}
		// check second humidity sample period (10 hours ago)
		from = now.minusHours(Meteorology.SECOND_HUMIDITY_SAMPLE_PERIOD_HOURS);
		to = now;
		if ( meteorologyDAO.isHumid(Meteorology.MIN_HUMIDITY_PERCENT, from, to) ) {
			System.out.println( "Humidity level was calculated from " + from + " to " + to + " and was found humid.");
			return now.minusHours(Meteorology.WIND_SAMPLE_HOURS + Meteorology.SECOND_HUMIDITY_SAMPLE_PERIOD_HOURS); // return the time to start the wind sample 
		}		
		System.out.println( "Humidity level was calculated from " + from + " to " + to + " and was found not humid.");
		return null;
		
		//System.out.println( "Humidity level was calculated from " + from + " to " + to + " and was found " + (isHumid? "":"not ") + "humid.");

	}
	
	
	public JSONArray getRiskMap() {
		return riskMap.getJsonRiskMap();
	}
	
	public static LocalDateTime getReportsStartDate() {
		LocalDateTime now = LocalDateTime.now();
		int currentDayOfYear = now.getDayOfYear();
		if (currentDayOfYear >= FIRST_DAY_OF_SEASON1 && currentDayOfYear <= LAST_DAY_OF_SEASON1) {
			return now.withDayOfYear(FIRST_DAY_OF_SEASON1);
		}
		if (currentDayOfYear >= FIRST_DAY_OF_SEASON2 && currentDayOfYear <= LAST_DAY_OF_SEASON2) {
			return now.withDayOfYear(FIRST_DAY_OF_SEASON2);
		}
		return now.minusMonths(REPORT_MONTHS_AGO);
	}

	
}
