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
 * Servlet implementation class AddStudent
 */
@WebServlet("/AddStudent")
public class AddStudent extends HttpServlet {
	
	  @Override
	    protected void doPost(HttpServletRequest req, HttpServletResponse res)
	            throws ServletException, IOException {

	        // Read form parameters
	        String classIdStr   = req.getParameter("classId");
	        String subjectIdStr = req.getParameter("subject_id");
	        String teacherIdStr = req.getParameter("teacher_id");
	        String student_name     = req.getParameter("student_name");
	        String college_id1  = req.getParameter("college_id");
	        String attendence1 = req.getParameter("attendance");
	        String behaviour1     = req.getParameter("behaviour");
	        String marks1  = req.getParameter("marks");
	        String subject = req.getParameter("subject");
	        int subject_id=Integer.parseInt(subjectIdStr);
	        int classId =Integer.parseInt(classIdStr);
	        int teacher_id=Integer.parseInt(teacherIdStr);
	        int behaviour=Integer.parseInt(behaviour1);
	        int marks =Integer.parseInt(marks1);
	        int attendence=Integer.parseInt(attendence1);
	        int college_id=Integer.parseInt(college_id1);
	        
	        
	        HttpSession session = req.getSession();
	        int school_id = (int) session.getAttribute("school_id");
	        String school_name = (String) session.getAttribute("school_name");
	        Connection con = null;
	        
	        // Convert to int
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

	            String sql = "INSERT INTO students "
	                    + "(student_name, class_id, college_id, attendance,marks,behaviour_score, school_id, school_name) "
	                    + "VALUES (?, ?, ?, ?, ?,?,?,?)";

	            PreparedStatement ps = con.prepareStatement(sql);
	            ps.setString(1, student_name);
	            ps.setInt(3, college_id);
	            ps.setInt(2, classId);
	            ps.setInt(5, marks);
	            ps.setInt(4, attendence);
	            ps.setInt(6, behaviour);
	            ps.setInt(7, school_id);
	            ps.setString(8, school_name);
	     
	            ps.executeUpdate();
	            
	          
	            ps.close();
	            con.close();
	            
	            
	            res.sendRedirect(
	                    "ClassesServlet?classId=" + classId +
	                    "&subject=" + subject +
	                    "&subject_id=" + subject_id
	                );
	            
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	       
	 	   

	        
	    }


}
