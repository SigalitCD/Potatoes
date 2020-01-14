package com.scd.potatoeslb.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class MeteorologicalStationMapper implements RowMapper<MeteorologicalStation> {

	@Override
	public MeteorologicalStation mapRow(ResultSet resultSet, int i) throws SQLException {
		MeteorologicalStation station = new MeteorologicalStation();
		station.setId(resultSet.getInt("id"));
		station.setName(resultSet.getString("name"));
		station.setImsId(resultSet.getInt("ims_id"));
		station.setRhChannel(resultSet.getInt("rh_channel"));
		station.setWdChannel(resultSet.getInt("wd_channel"));
		station.setActive(resultSet.getBoolean("is_active"));
		station.setLastUpdateTime(resultSet.getTimestamp("last_update_time").toLocalDateTime());
		return station;
	}

}
