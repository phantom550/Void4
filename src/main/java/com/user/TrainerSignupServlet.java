package com.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@WebServlet("/TrainerSignupServlet")
public class TrainerSignupServlet extends HttpServlet {
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) {
	 // Get form parameters
    String trainer_id = req.getParameter("trainer_id");
    String trainer_name = req.getParameter("trainer_name");
    String email = req.getParameter("email");
    String designation = req.getParameter("designation");
    String subject_id_str = req.getParameter("subject_id");
    String password = req.getParameter("password");
    String subject = req.getParameter("subject");
    String status = "Active";
    int subject_id = Integer.parseInt(subject_id_str);
    int active = 2; // default value, assuming from .class constants
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

        // Insert data into trainers table
        String sql = "INSERT INTO trainers VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement st = con.prepareStatement(sql);
       

        st.setInt(1, Integer.parseInt(trainer_id));
        st.setString(2, trainer_name);
        st.setString(3, email);
        st.setString(5, designation);
        st.setInt(7, subject_id);
        st.setString(4, password);
        st.setString(8, subject);
        st.setString(6, status);

        st.executeUpdate();
        st.close();
       
        con.close();

        // Redirect to login page
        res.sendRedirect("trainerlogin.html");

    } 
    catch (Exception e) {
        e.printStackTrace();
    }
	}
}
    