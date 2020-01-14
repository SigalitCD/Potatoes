package com.scd.potatoeslb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.scd.potatoeslb.data.Meteorology;
import com.scd.potatoeslb.risks.RiskMapManager;
import com.scd.potatoeslb.spring.dao.IMeteorologyDAO;

public class MeteorologyScheduler {
	
	// TODO: get STATION_ID from properties file, get IMS_STATION_ID from database, get the other data such as channel id, from the database
	// TODO: get api url from properties file
	// TODO: define intervals in properties file
	// TODO: check meta-data? maybe in case of problems? 
	// TODO: check if we need to change the values of RH and WD to float instead of int

	private static final int CHANNEL_STATUS_VALID = 1;
	private static final int CHANNEL_STATUS_INVALID = 2;
	private static final int STATION_ID = 1;
	private static final long INTERVAL = 10; // a fixed period between invocations, in minutes. // TODO: change to 10 minutes on production! insert into json or properties file
	private static final TimeUnit INTERVAL_UNITS = TimeUnit.MINUTES;  // TODO: change TimeUnit to minutes on production!
	private static final int IMS_STATION_ID = 58; // station of HAVAT-BSOR
	private static final String URL_STR_META_DATA = "https://api.ims.gov.il/v1/envista/stations/" + IMS_STATION_ID; // meta data 
	private static final String URL_STR_RH = "https://api.ims.gov.il/v1/envista/stations/" + IMS_STATION_ID + "/data/8/latest"; // relative humidity
	private static final String URL_STR_WD = "https://api.ims.gov.il/v1/envista/stations/" + IMS_STATION_ID + "/data/5/latest"; // wind direction
	private static final String API_TOKEN = "ApiToken 1a901e45-9028-44ff-bd2c-35e82407fb9b";
	private static boolean isFirstRun = true;

	private ScheduledExecutorService execService;
	private AnnotationConfigApplicationContext context = ApplicationContextProvider.getApplicationContext();
	private IMeteorologyDAO meteorologyDAO = context.getBean(IMeteorologyDAO.class);

	public void startScheduledTask() {
		execService = Executors.newScheduledThreadPool(0);
		execService.scheduleAtFixedRate(() -> {
			// The repetitive task is to retrieve data from IMS API and save it to database
			System.out.println("scheduled Meteorology task starts");
			
			// get data from IMS
			Meteorology meteorology = getMeteorolgyData();

			// add record to database
			if (!saveToDB(meteorology)) {
				System.out.println("Failed to save meteorolgy data to Database");
			}
			
			if ( isFirstRun ) {
				// update risk map if needed
				RiskMapManager.getRiskMapManager().refreshRiskMap();
				isFirstRun = false;
			}
			
			System.out.println("scheduled Meteorology task ends");
			
		}, 2, INTERVAL, INTERVAL_UNITS);
	}

	public void stopScheduledTask() {
		execService.shutdown();
	}
	
	private Meteorology getMeteorolgyData() {
		// get Relative Humidity 
		String imsData = retrieveDataFromIMS(URL_STR_RH); 		
		MeteorologyApiRawData RHData = parseJsonData(imsData);
		
		// get Wind Direction
		imsData = retrieveDataFromIMS(URL_STR_WD); 
		MeteorologyApiRawData WDData = parseJsonData(imsData);
		boolean isValid = validateChannelData( RHData, "Relative Humidity" ) && validateChannelData( WDData,  "Wind Directions" );
		
		// set Meteorology object
		return new Meteorology(0, STATION_ID, LocalDateTime.now(), isValid, RHData.value, WDData.value );
	}

	private String retrieveDataFromIMS( String urlStr ) {
		String responseData = "";
		
		try {			
			URL url = new URL(urlStr);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty ("Authorization", API_TOKEN);
			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				responseData = response.toString();
			}
			else {
				System.out.println("Data request from the Meteorological Service failed. Response code: '" + responseCode + "'");
			}
			return responseData;
		} catch ( Exception e ) {
			System.out.println("Data request from the Meteorological Service threw exception. Exception: '" + e.getMessage() + "'");
			return responseData;
		}
	}
	
	private MeteorologyApiRawData parseJsonData( String jsonDataStr ) {
		MeteorologyApiRawData data = new MeteorologyApiRawData();
		if ( StringUtils.isBlank(jsonDataStr) ) {
			System.out.println("Null or empty data was received from the Meteorolgical Service.");
		} else {
			try {
				JSONObject jsonObject = new JSONObject(jsonDataStr);
				data.ims_stationId = jsonObject.getInt("stationId");
				JSONArray jsonArray = jsonObject.getJSONArray("data");
				JSONObject innerJsonObject = jsonArray.getJSONObject(0);
				JSONArray jsonChannelsArray = innerJsonObject.getJSONArray("channels");
				JSONObject innerJsonChannelsObject = jsonChannelsArray.getJSONObject(0);
				data.channelStatus = innerJsonChannelsObject.getInt("status");
				data.isValueValid = innerJsonChannelsObject.getBoolean("valid");
				data.value = innerJsonChannelsObject.getInt("value");
				data.isRawDataValid = true;
			} catch ( JSONException e ) {
				System.out.println( "JSONObject threw exception:'" + e.getMessage() + "'" );
			}		
		}
		return data;
	}
	
	private boolean validateChannelData( MeteorologyApiRawData data, String channelDescription ) {
		if ( data == null || !data.isRawDataValid ) {
			System.out.println( "Null or corrupt data was parsed from the Meteorological Service for channel '" + channelDescription + "'.");
			return false;
		}
		if (data.ims_stationId != IMS_STATION_ID ) {
			System.out.println( "Data request from the Meteorological Service for channel '" + channelDescription + "' returned incorrect station id. Requested IMS Station id is '" + IMS_STATION_ID + "' Rrequest returned id '" + data.ims_stationId + "'");
			return false;
		}
		if (data.channelStatus != CHANNEL_STATUS_VALID) {
			System.out.println( "Data request from the Meteorological Service for channel '" + channelDescription + "' informed on invalid channel status. Channel status is '" + data.channelStatus + "'." );
			return false;
		}
		if (!data.isValueValid) {
			System.out.println( "Data request from the Meteorological Service for channel '" + channelDescription + "' informed on invalid value." );
			return false;
		}
		return true;
	}
	
	private boolean saveToDB( Meteorology meteorology ) {
		if ( context == null ) {
			System.err.println( "At MeteorologyScheduler: Failed to get ApplicationContext (err)");
			return false;
		}
		
		return meteorologyDAO.createMeteorology(meteorology);
	}
	
	private class MeteorologyApiRawData {
		int ims_stationId;
		int value;
		int channelStatus;
		boolean isValueValid;
		boolean isRawDataValid = false;
	}
}

