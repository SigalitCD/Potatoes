package com.scd.potatoeslb;

import java.util.List;

import com.scd.potatoeslb.data.MeteorologicalStation;

public interface IMeteorologyService {
	public void init( List<MeteorologicalStation> stations );
	public void getData();
}
