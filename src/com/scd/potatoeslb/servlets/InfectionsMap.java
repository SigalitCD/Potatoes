package com.scd.potatoeslb.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.scd.potatoeslb.ApplicationContextProvider;
import com.scd.potatoeslb.risks.RiskMapManager;
import com.scd.potatoeslb.spring.dao.IReportDAO;

/**
 * Servlet implementation class InfectionsMap
 */
@WebServlet(description = "This servlet returns JSON structure of a list of infected coordinates.", urlPatterns = { "/InfectionsMap" })
public class InfectionsMap extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InfectionsMap() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// get all infected coordinates from database 
		AnnotationConfigApplicationContext context = ApplicationContextProvider.getApplicationContext();
		if ( context == null ) {
			System.err.println( "Failed to get ApplicationContext (err)");
		}

		IReportDAO reportDAO = context.getBean(IReportDAO.class);
		if ( reportDAO == null ) {
			System.err.println( "Failed to get ReportDAO (err)");
		}
		JSONArray jsonArray = reportDAO.getLatestReportsExtended(RiskMapManager.getReportsStartDate());
		if( jsonArray != null ) {
			response.getWriter().append( jsonArray.toString());
		}		
	}	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
