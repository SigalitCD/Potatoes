package com.scd.potatoeslb.spring.dao;

import java.util.List;

import javax.sql.DataSource;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.scd.potatoeslb.data.RS2JsonExtractor;
import com.scd.potatoeslb.data.Report;
import com.scd.potatoeslb.data.ReportMapper;

@Component
public class ReportDAO implements IReportDAO {

	JdbcTemplate jdbcTemplate;

	private final String SQL_GET_BY_ID = "select * from report where id = ?";
	private final String SQL_GET_ALL = "select * from report";
	private final String SQL_GET_DISTINCT = "select distinct latitude as lat, longitude as lng, 0 as id, 0 as farmer_id, now() as report_time from report";
	private final String SQL_GET_REPORTS_BY_FARMER = "select * from report where farmer_id = ?";
	private final String SQL_DELETE = "delete from report where id = ?";
	private final String SQL_UPDATE = "update report set farmer_id = ?, report_time = ?, latitude = ?, longitude = ? where id = ?";
	private final String SQL_INSERT = "insert into potatoes.report(farmer_id, report_time, latitude, longitude) values(?,?,?,?)";

	@Autowired
	public ReportDAO(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public Report getReportById(int id) {
		return jdbcTemplate.queryForObject(SQL_GET_BY_ID, new Object[] { id }, new ReportMapper());
	}

	@Override
	public List<Report> getAllReports() {
		return jdbcTemplate.query(SQL_GET_ALL, new ReportMapper());		
	}

	@Override
	public JSONArray getDistinctReports() {
		return jdbcTemplate.query(SQL_GET_DISTINCT, new RS2JsonExtractor());
	}
	
	@Override
	public List<Report> getReportByFarmer(int FarmerId) {
		return jdbcTemplate.query(SQL_GET_REPORTS_BY_FARMER, new ReportMapper());
	}

	@Override
	public boolean deleteReport(int id) {
		return jdbcTemplate.update(SQL_DELETE, id) > 0;
	}

	@Override
	public boolean updateReport(Report report) {
		return jdbcTemplate.update(SQL_UPDATE, report.getFarmerId(), report.getReportTime(), report.getLatitude(), report.getLongitude(), report.getId()) > 0; 
	}

	@Override
	public boolean createReport(Report report) {
		return jdbcTemplate.update(SQL_INSERT, report.getFarmerId(), report.getReportTime(), report.getLatitude(), report.getLongitude()) > 0;
	}
}
