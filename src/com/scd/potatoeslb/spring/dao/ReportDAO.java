package com.scd.potatoeslb.spring.dao;

import java.time.LocalDateTime;
import java.util.List;

import javax.sql.DataSource;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
	private final String SQL_GET_SINCE = "select distinct latitude, longitude, 0 as id, 0 as farmer_id, report_time from report where report_time > ?";
	//private final String SQL_GET_DISTINCT = "select distinct latitude as lat, longitude as lng, id as id, farmer_id as farmer_id, report_time as report_time from report";
	private final String SQL_GET_DISTINCT = "select max(report.farmer_id) as farmer_id, report.latitude as lat, report.longitude as lng, report.report_time, max(report.id) as id, concat(farmer.first_name, ' ', farmer.last_name) as farmer_name from report join (  select latitude as lat, longitude as lng, max(report_time) as report_time from report group by lat, lng) as distinct_report on  report.latitude=distinct_report.lat and report.longitude=distinct_report.lng and report.report_time=distinct_report.report_time join farmer on  farmer.id=report.farmer_id group by report.latitude, report.longitude, report.report_time, farmer_name order by lat, lng";
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
	public List<Report> getLatestReports( LocalDateTime since ) {
//		List<Report> rep = null;
//		try {
//			rep = jdbcTemplate.query(SQL_GET_SINCE, new LocalDateTime[] {since}, new ReportMapper() );
//		} catch (DataAccessException e ) {
//			System.out.println(e.getMessage());
//		}
//		return rep;
		
		return jdbcTemplate.query(SQL_GET_SINCE, new LocalDateTime[] {since}, new ReportMapper() );
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
