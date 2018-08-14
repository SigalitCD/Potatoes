package com.scd.potatoeslb.spring.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.scd.potatoeslb.data.Farmer;
import com.scd.potatoeslb.data.FarmerMapper;

@Component
public class FarmerDAO implements IFarmerDAO {

	JdbcTemplate jdbcTemplate;

	private final String SQL_GET_BY_ID = "select * from farmer where id = ?";
	private final String SQL_GET_ALL = "select * from farmer";
	private final String SQL_DELETE = "delete from farmer where id = ?";
	private final String SQL_UPDATE = "update farmer set first_name = ?, last_name = ?, phone_number = ? where id = ?";
	private final String SQL_INSERT = "insert into farmer(first_name, last_name, phone_number) values(?,?,?)";

	@Autowired
	public FarmerDAO(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public Farmer getFarmerById(Long id) {
		return jdbcTemplate.queryForObject(SQL_GET_BY_ID, new Object[] { id }, new FarmerMapper());
	}

	@Override
	public List<Farmer> getAllFarmers() {
		return jdbcTemplate.query(SQL_GET_ALL, new FarmerMapper());	
	}

	@Override
	public boolean deleteFarmer(Long id) {
		return jdbcTemplate.update(SQL_DELETE, id) > 0;
	}

	@Override
	public boolean updateFarmer(Farmer farmer) {
		return jdbcTemplate.update(SQL_UPDATE, farmer.getFirstName(), farmer.getLastName(), farmer.getPhoneNumber(), farmer.getId()) > 0; 
	}

	@Override
	public boolean createFarmer(Farmer farmer) {
		return jdbcTemplate.update(SQL_INSERT, farmer.getFirstName(), farmer.getLastName(), farmer.getPhoneNumber()) > 0;
	}

}
