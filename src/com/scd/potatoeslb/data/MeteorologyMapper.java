package com.scd.potatoeslb.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class MeteorologyMapper implements RowMapper<Meteorology> {
	
	@Override
	public Meteorology mapRow(ResultSet resultSet, int i) throws SQLException {
		Meteorology meteorology = new Meteorology();
		meteorology.setId(resultSet.getInt("id"));
		meteorology.setStationId(resultSet.getInt("station_id"));
		meteorology.setSampleTime(resultSet.getTimestamp("time").toLocalDateTime());
		meteorology.setValid(resultSet.getBoolean("is_valid"));
		meteorology.setRelativeHumidity(resultSet.getInt("relative_humidity"));
		meteorology.setWindDirection(resultSet.getInt("wind_direction"));
		return meteorology;
	}

}
