package com.scd.potatoeslb.spring.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonRiskMapDAO implements IJsonRiskMapDAO{
	
	JdbcTemplate jdbcTemplate;

	private final String SQL_GET_LATEST = "select a.risk_map from json_risk_map a inner join ( select id, max(save_time) save_time from json_risk_map group by id ) b on a.id = b.id and a.save_time = b.save_time limit 1";
	private final String SQL_INSERT = "insert into json_risk_map(risk_map, save_time) values(?, now())";

	@Autowired
	public JsonRiskMapDAO(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public String getLatestRiskMap() {
		
//		try {
//			List<String> data = jdbcTemplate.query(SQL_GET_LATEST, new RowMapper<String>(){
//                public String mapRow(ResultSet rs, int rowNum) 
//                                             throws SQLException {
//                        return rs.getString(2);
//                }
//           });	
//			
//			return data.get(0);
//					 
//					 
//		} catch ( DataAccessException e ) {
//			System.out.println("Data Access Exception at JsonRiskMapDAO.getLatestRiskMap: " + e.getMessage());
//			return null;
//		}

		try {
			return jdbcTemplate.queryForObject(SQL_GET_LATEST, String.class );
		} catch ( DataAccessException e ) {
			System.out.println("Data Access Exception at JsonRiskMapDAO.getLatestRiskMap: " + e.getMessage());
			return null;
		}
	}
	
	@Override
	public boolean deleteRiskMap(Long id) {
		return false;
	}
	
	@Override
	public boolean updateRiskMap(JSONArray jsonRiskMap) {
		return false;
	}
	
	@Override
	public boolean createRiskMap(String jsonRiskMapStr) {
		try {
			return jdbcTemplate.update(SQL_INSERT, jsonRiskMapStr) > 0;
		} catch ( DataAccessException e ) {
			System.out.println("Data Access Exception at JsonRiskMapDAO.createRiskMap: " + e.getMessage());
			return false;
		}
	}

}
