package com.scd.potatoeslb.spring.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.scd.potatoeslb.data.MeteorologicalStation;
import com.scd.potatoeslb.data.MeteorologicalStationMapper;

@Component
public class MeteorologicalStationDAO implements IMeteorologicalStationDAO {

	JdbcTemplate jdbcTemplate;

	private final String SQL_GET_BY_ID = "select * from meteorological_station where id = ?";
	private final String SQL_GET_ALL = "select * from meteorological_station";
	private final String SQL_GET_ACTIVE_STATIONS = "select * from meteorological_station where is_active=true";
	private final String SQL_DELETE = "delete from meteorological_station where id = ?";
	private final String SQL_UPDATE = "update meteorological_station set name = ?, ims_id = ?, rh_channel = ?, wd_channel = ?, is_active = ?, last_update_time = CURRENT_TIMESTAMP where id = ?";
	private final String SQL_INSERT = "insert into meteorological_station(name, ims_id, rh_channel, wd_channel, is_active) values(?,?,?,?,?,?)";

	@Autowired
	public MeteorologicalStationDAO(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public MeteorologicalStation getStationById(int id) {
		return jdbcTemplate.queryForObject(SQL_GET_BY_ID, new Object[] { id }, new MeteorologicalStationMapper());
	}

	@Override
	public List<MeteorologicalStation> getAllStations() {
		return jdbcTemplate.query(SQL_GET_ALL, new MeteorologicalStationMapper());		
	}

	@Override
	public List<MeteorologicalStation> getActiveStations() {
		return jdbcTemplate.query(SQL_GET_ACTIVE_STATIONS, new MeteorologicalStationMapper());
	}
	
	@Override
	public boolean deleteStation(int id) {
		return jdbcTemplate.update(SQL_DELETE, id) > 0;
	}

	@Override
	public boolean updateStation(MeteorologicalStation station) {
		return jdbcTemplate.update(SQL_UPDATE, station.getName(), station.getImsId(), station.getRhChannel(), station.getWdChannel(), station.isActive(), station.getId()) > 0; 
	}

	@Override
	public boolean createStation(MeteorologicalStation station) {
		return jdbcTemplate.update(SQL_INSERT, station.getName(), station.getImsId(), station.getRhChannel(), station.getWdChannel(), station.isActive()) > 0;
	}

}
