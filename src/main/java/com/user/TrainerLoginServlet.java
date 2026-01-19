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
import java.sql.ResultSet;

/**
 * Servlet implementation class TrainerLoginServlet
 */
@WebServlet("/TrainerLoginServlet")
public class TrainerLoginServlet extends HttpServlet {
	

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String sid = req.getParameter("subject_id");
		String password = req.getParameter("password");
		String subject = req.getParameter("subject");
		String school_name = req.getParameter("school_name");
		 int school_id=0;
		int subject_id = Integer.parseInt(sid);
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
	          
	          String sql1 = "select school_id from trainer_videos where school_name=? ";
	          PreparedStatement st1 = con.prepareStatement(sql1);
	          
	          st1.setString(1,school_name);
	          ResultSet rs1=st1.executeQuery();
	          if (rs1.next()) {
	                school_id = rs1.getInt("school_id");
	          }
	          

	        // Insert data into trainers table
	        String sql = "select password from trainers where subject_id=?";
	        PreparedStatement st = con.prepareStatement(sql);
	       

	        st.setInt(1, subject_id);
	        
	        
	        
	        ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");

                if (dbPassword.equals(password)) {
                    res.getWriter().println("Login Successful");
                } else {
                    res.getWriter().println("Invalid Password");
                }
            } else {
                res.getWriter().println("User not found");
            }
	        st.close();
	        st1.close();
	        rs.close();
	        rs1.close();
	       
	        con.close();


	    } 
	    catch (Exception e) {
	        e.printStackTrace();
	    }
		
        HttpSession session = req.getSession();
        session.setAttribute("school_id", school_id);
        session.setAttribute("school_name", school_name);
        session.setAttribute("subject_name", subject);
        session.setAttribute("subject_id", subject_id);

        req.getRequestDispatcher("/TrainerSubjectDashboardServlet").forward(req, res);
		
	}

}
