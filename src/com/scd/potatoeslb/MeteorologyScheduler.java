package com.scd.potatoeslb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.scd.potatoeslb.data.Meteorology;

public class MeteorologyScheduler {

	private static final long INTERVAL = 10; // a fixed period between invocations, in minutes. // TODO: change to 10 minutes on production! insert into json or properties file
	private static final String URL_STR_META_DATA = "https://api.ims.gov.il/v1/envista/stations/58"; // meta data 
	private static final String URL_STR_WD = "https://api.ims.gov.il/v1/envista/stations/58/data/5/latest"; // wind direction
	private static final String URL_STR_RH = "https://api.ims.gov.il/v1/envista/stations/58/data/8/latest"; // relative humidity
	private static final String API_TOKEN = "ApiToken 1a901e45-9028-44ff-bd2c-35e82407fb9b";

	private ScheduledExecutorService execService;
	
	public void startScheduledTask() {
		execService = Executors.newScheduledThreadPool(0);
		execService.scheduleAtFixedRate(() -> {
			// The repetitive task is to retrieve data from IMS API and save it to database
			
			// get current time as sample time
			LocalDateTime sampleTime = LocalDateTime.now();
			
			String imsData = retrieveDataFromIMS("5");
			parseJsonData(imsData, sampleTime);
			
		}, 0, INTERVAL, TimeUnit.MINUTES); // TODO: change TimeUnit to minutes on production!
	}
	
	public void stopScheduledTask() {
		execService.shutdown();
	}

	private String retrieveDataFromIMS( String strChannel ) {
		String responseData = "";
		
		try {			
			URL url = new URL(URL_STR_RH);
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
		} catch (  Exception e) {
			System.out.println("Data request from the Meteorological Service threw exception. Exception: '" + e.getMessage() + "'");
			return responseData;
		}
	}
	
	private void parseJsonData( String jsonDataStr, LocalDateTime sampleTime ) {
		JSONObject jsonObject = new JSONObject(jsonDataStr);
		Meteorology meteorology;
/*		meteorology = new Meteorology( 0, jsonObject.getString("stationId"), sampleTime, jsonObject.getString("value"))
		System.out.println(myresponse);
		System.out.println("base -" + myresponse.getString("base"));
		System.out.println("date -" + myresponse.getString("date"));
		JSONObject rates_object = new JSONObject(myresponse.getJSONObject("rates").toString());
		System.out.println("rates -" + rates_object);
		System.out.println("AUD -" + rates_object.getDouble("AUD"));
		System.out.println("BGN -" + rates_object.getDouble("BGN"));
		System.out.println("BRL -" + rates_object.getDouble("BRL"));
		System.out.println("CAD -" + rates_object.getDouble("CAD"));
*/
	}
}
