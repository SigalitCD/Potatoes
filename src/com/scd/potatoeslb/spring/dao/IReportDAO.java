package com.scd.potatoeslb.spring.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONArray;

import com.scd.potatoeslb.data.Report;

public interface IReportDAO {
	public Report getReportById(int id);
	public List<Report> getAllReports();
	public List<Report> getLatestReports( LocalDateTime since );
	public JSONArray getDistinctReports();
	public List<Report> getReportByFarmer(int FarmerId);
	public boolean deleteReport(int id);
	public boolean updateReport(Report report);
	public boolean createReport(Report report);
}
