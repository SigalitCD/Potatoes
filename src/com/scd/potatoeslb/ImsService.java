package com.scd.potatoeslb;

import java.util.List;

import com.scd.potatoeslb.data.MeteorologicalStation;

public class ImsService implements IMeteorologyService {

	public void init( List<MeteorologicalStation> stations ) {
		for ( MeteorologicalStation station : stations ) {
			
		}
		// get RH channel
		
		// get WD channel
	}
	
	public void getData() {
		
	}
	
	private void getChannels( int imsStationId ) {
	}
	private void getWDChannel( int imsStationId ) {
	}
	private int getRH( int imsStationId ) {
		return 0;
	}
	private int getWD( int imsStationId ) {
		return 0;
	}

	
	
}
