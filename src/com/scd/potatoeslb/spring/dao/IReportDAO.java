package com.scd.potatoeslb.spring.dao;

import java.util.List;
import com.scd.potatoeslb.data.Report;

public interface IReportDAO {
	Report getReportById(int id);
	List<Report> getAllReports();
	List<Report> getReportByFarmer(int FarmerId);
	boolean deleteReport(int id);
	boolean updateReport(Report report);
	boolean createReport(Report report);
}
