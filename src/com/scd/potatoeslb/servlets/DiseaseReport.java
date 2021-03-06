package com.scd.potatoeslb.servlets;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.scd.potatoeslb.ApplicationContextProvider;
import com.scd.potatoeslb.data.Report;
import com.scd.potatoeslb.spring.dao.IReportDAO;

/**
 * Servlet implementation class DiseaseReport
 */
@WebServlet(description = "This class handles the farmers report about disease spread: geolocation and current time", urlPatterns = { "/DiseaseReport" })
public class DiseaseReport extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DiseaseReport() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println( "Starting doGet (out)");
		System.err.println( "Starting doGet (err)");
		
		String latitude = request.getParameter("latitude");
		String longitude = request.getParameter("longitude");
		String timestampStr = request.getParameter("timestamp");
		Long timestampLong = Long.valueOf(timestampStr);
		LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampLong), TimeZone.getDefault().toZoneId());  
		String farmerIdStr = request.getParameter("farmerId");
		int farmerId = Integer.valueOf(farmerIdStr);
		
		AnnotationConfigApplicationContext context = ApplicationContextProvider.getApplicationContext();
		if ( context == null ) {
			System.err.println( "Failed to get ApplicationContext (err)");
		}

		IReportDAO reportDAO = context.getBean(IReportDAO.class);
		
		if ( reportDAO == null ) {
			System.err.println( "Failed to get ReportDAO (err)");
		}
			
		response.getWriter().append("Disease Report accepted at: ").append(request.getContextPath());

		// validate data structure, put data to database and maybe awake Risk map calculator 
		boolean b = reportDAO.createReport(new Report( 0, farmerId, localDateTime, latitude, longitude ));
		if ( b ) {
			System.out.println("Succeeded to add new Report record to database");
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
