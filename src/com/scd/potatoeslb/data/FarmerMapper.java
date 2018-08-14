package com.scd.potatoeslb.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class FarmerMapper implements RowMapper<Farmer> {

	public Farmer mapRow(ResultSet resultSet, int i) throws SQLException {

		Farmer farmer = new Farmer();
		farmer.setId(resultSet.getInt("id"));
		farmer.setFirstName(resultSet.getString("first_name"));
		farmer.setLastName(resultSet.getString("last_name"));
		farmer.setPhoneNumber(resultSet.getString("phone_number"));
		return farmer;
	}
}
