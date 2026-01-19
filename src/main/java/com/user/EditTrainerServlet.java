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

/**
 * Servlet implementation class EditTrainerServlet
 */
@WebServlet("/EditTrainerServlet")
public class EditTrainerServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	
		 int trainerId = Integer.parseInt(req.getParameter("trainer_id"));
	        String name = req.getParameter("trainer_name");
	        String email = req.getParameter("email");
	        String status = req.getParameter("status");
	        String classId = req.getParameter("classId");
	        
	    	 HttpSession session = req.getSession(false);

	         if (session == null) {
	             res.sendRedirect("login.jsp");
	             return;
	         }

	         Integer school_id = (Integer) session.getAttribute("school_id");
	         String school_name = (String) session.getAttribute("school_name");
	         Integer subject_id = (Integer) session.getAttribute("subject_id");
	         String subject_name = (String) session.getAttribute("subject_name");
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

	             String sql = "UPDATE trainers SET trainer_name=?, email=?, status=? WHERE trainer_id=?";
	             PreparedStatement ps = con.prepareStatement(sql);
	             ps.setString(1, name);
	             ps.setString(2, email);
	             ps.setString(3, status);
	             ps.setInt(4, trainerId);

	             ps.executeUpdate();

	             ps.close();
	             con.close();

	             // ✅ Redirect back to ClassesServlet (BEST PRACTICE)
	             res.sendRedirect(
	                 "TrainerClassesServlet?classId=" + classId 
	                 
	             );

	         } catch (Exception e) {
	             e.printStackTrace();
	             res.getWriter().println("Database Error");
	         }
	         
	         
	
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
