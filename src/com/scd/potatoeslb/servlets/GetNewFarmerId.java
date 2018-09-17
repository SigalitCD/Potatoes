package com.scd.potatoeslb.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.scd.potatoeslb.ApplicationContextProvider;
import com.scd.potatoeslb.data.Farmer;
import com.scd.potatoeslb.spring.dao.IFarmerDAO;

/**
 * Servlet implementation class GetNewFarmerId
 */
@WebServlet(description = "This servlet creates a new Farmer record and returns its id in the response.", urlPatterns = { "/GetNewFarmerId" })
public class GetNewFarmerId extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetNewFarmerId() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String firstName = request.getParameter("fName");
		String lastName = request.getParameter("lName");
		String phone = request.getParameter("phone");

		AnnotationConfigApplicationContext context = ApplicationContextProvider.getApplicationContext();
		if ( context == null ) {
			System.err.println( "Failed to get ApplicationContext (err)");
			return;
		}
		
		IFarmerDAO farmerDAO = context.getBean(IFarmerDAO.class);
		Farmer farmer = new Farmer(0, firstName, lastName, phone);	
		int farmerId = farmerDAO.createFarmer(farmer);

		response.getWriter().append(String.valueOf(farmerId));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
