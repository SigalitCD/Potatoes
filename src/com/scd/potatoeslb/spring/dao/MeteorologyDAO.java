package com.scd.potatoeslb.spring.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.scd.potatoeslb.data.FarmerMapper;
import com.scd.potatoeslb.data.Meteorology;
import com.scd.potatoeslb.data.MeteorologyMapper;

@Component
public class MeteorologyDAO implements IMeteorologyDAO {
	
	JdbcTemplate jdbcTemplate;

	private final String SQL_GET_BY_ID = "select * from meteorology where id = ?";
	private final String SQL_GET_ALL = "select * from meteorology";
	private final String SQL_DELETE = "delete from meteorology where id = ?";
	private final String SQL_UPDATE = "update meteorology set station_id = ?, time = ?, relative_humidity = ?, wind_direction = ? where id = ?";
	private final String SQL_INSERT = "insert into meteorology(station_id, time, relative_humidity, wind_direction) values(?,?,?,?)";

	@Autowired
	public MeteorologyDAO(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}


	@Override
	public Meteorology getMeteorologyById(Long id) {
		return jdbcTemplate.queryForObject(SQL_GET_BY_ID, new Object[] { id }, new MeteorologyMapper());
	}

	@Override
	public List<Meteorology> getAllMeteorologies() {
		return jdbcTemplate.query(SQL_GET_ALL, new MeteorologyMapper());	
	}

	@Override
	public boolean deleteMeteorology(Long id) {
		return jdbcTemplate.update(SQL_DELETE, id) > 0;
	}

	@Override
	public boolean updateMeteorology(Meteorology meteorology) {
		return jdbcTemplate.update(SQL_UPDATE, meteorology.getStationId(), meteorology.getSampleTime(), meteorology.getRelativeHumidity(), meteorology.getWindDirection(), meteorology.getId()) > 0; 
	}

	@Override
	public boolean createMeteorology(Meteorology meteorology) {
		return jdbcTemplate.update(SQL_INSERT, meteorology.getStationId(), meteorology.getSampleTime(), meteorology.getRelativeHumidity(), meteorology.getWindDirection()) > 0;
	}

}
