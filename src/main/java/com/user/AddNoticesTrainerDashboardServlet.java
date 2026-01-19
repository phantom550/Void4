package com.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;


@WebServlet("/AddNoticesTrainerDashboardServlet")
public class AddNoticesTrainerDashboardServlet extends HttpServlet {
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		String title = req.getParameter("title");
		String description = req.getParameter("description");
	
		
		    HttpSession session = req.getSession(false);
	        Integer school_id = (Integer) session.getAttribute("school_id");
	        String school_name = (String) session.getAttribute("school_name");
	        Integer subject_id = (Integer) session.getAttribute("subject_id");
	        String subject = (String) session.getAttribute("subject_name");
	        Connection con = null;
	        try {
	            Class.forName("com.mysql.cj.jdbc.Driver");

	            // --- CONNECTION LOGIC START ---
	            
	            // A. Check for Cloud Environment Variables (Koyeb)
	            String envUrl = System.getenv("DB_URL");
	            String envUser = System.getenv("DB_USER");
	            String envPass = System.getenv("DB_PASSWORD");

	            String dbUrl, dbUser, dbPass;

	            if (envUrl != null) {
	                // We are on the Cloud (Koyeb)
	                dbUrl = envUrl;
	                dbUser = envUser;
	                dbPass = envPass;
	            } else {
	                // We are on Localhost -> Use the Aiven credentials directly
	                // Note: The URL is reformatted to valid JDBC syntax
	            	dbUrl = "jdbc:mysql://localhost:3306/void4"; 
	                dbUser = "root";
	                dbPass = "root"; // Your LOCAL password, not the Aiven one
	            }

	            // B. Establish Connection
	            con = DriverManager.getConnection(dbUrl, dbUser, dbPass);

	            // --- CONNECTION LOGIC END ---

	            // --- DIAGNOSTIC: Check which DB we are connected to ---
	            DatabaseMetaData meta = con.getMetaData();
	            String connectedUrl = meta.getURL();
	            String connectedUser = meta.getUserName();
	            System.out.println(">>> CONNECTED TO: " + connectedUrl); // Prints to Server Logs
		          String sql1 = "INSERT INTO notices "
		                    + "(subject ,description, title, school_id, school_name) "
		                    + "VALUES (?, ?, ?, ?, ?)";
		          PreparedStatement st1 = con.prepareStatement(sql1);
		          
		          st1.setString(1,subject);
		          st1.setString(3,title);
		          st1.setString(2,description);
		          st1.setInt(4,school_id);
		          st1.setString(5,school_name);
		          
		          st1.executeUpdate();
		          

		       
		        
		        
		      
		        st1.close();
		         con.close();


		    } 
		    catch (Exception e) {
		        e.printStackTrace();
		    }
	        
	        req.getRequestDispatcher("TrainerSubjectDashboardServlet")
	           .forward(req, res);		
	}

		

}
