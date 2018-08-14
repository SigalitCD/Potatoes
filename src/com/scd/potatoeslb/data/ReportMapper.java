package com.scd.potatoeslb.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ReportMapper implements RowMapper<Report> {
	
	Report report = new Report();

	@Override
	public Report mapRow(ResultSet resultSet, int i) throws SQLException {
		report.setId(resultSet.getInt("id"));
		report.setFarmerId(resultSet.getInt("farmer_id"));
		report.setReportTime(resultSet.getTimestamp("report_time").toLocalDateTime());
		report.setLatitude(resultSet.getString("latitude"));
		report.setLongitude(resultSet.getString("longitude"));
		return report;
	}
}
