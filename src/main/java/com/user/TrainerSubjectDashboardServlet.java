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
import java.util.HashMap;
import java.util.Map;
@WebServlet("/TrainerSubjectDashboardServlet")
public class TrainerSubjectDashboardServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
    	

    	
// starting of the code
        HttpSession session = req.getSession(false);

        if (session == null) {
            res.sendRedirect("login.jsp");
            return;
        }

        Integer school_id = (Integer) session.getAttribute("school_id");
        String school_name = (String) session.getAttribute("school_name");
        Integer subject_id = (Integer) session.getAttribute("subject_id");
        String subject_name = (String) session.getAttribute("subject_name");
        

        if (school_id == null || school_name == null) {
            res.sendRedirect("login.jsp");
            return;
        }

        req.setAttribute("school_id", school_id);
        req.setAttribute("school_name", school_name);
        req.setAttribute("subject",subject_name);
        req.setAttribute("subject_id", subject_id);

        System.out.println(school_name + subject_name);
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

            String sql = "SELECT COUNT(*) FROM trainer_videos where subject=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, subject_name);
            ResultSet rs = ps.executeQuery();

            int totalVideos = 0;
            if (rs.next()) {
                totalVideos = rs.getInt(1);
            }
            req.setAttribute("totalVideos", totalVideos);
            System.out.println(totalVideos);
            
            String sql1 = "SELECT COUNT(*) FROM notices where subject=?";
            PreparedStatement ps1 = con.prepareStatement(sql1);
            ps1.setString(1, subject_name);
            ResultSet rs1 = ps1.executeQuery();

            int totalNotices = 0;
            if (rs1.next()) {
                totalNotices = rs1.getInt(1);
            }
            req.setAttribute("totalNotices", totalNotices);
            System.out.println(totalNotices);

            rs.close();
            ps.close();
            rs1.close();
            ps1.close();
            
            con.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        //ending of the code
        
        
        req.getRequestDispatcher("trainersubjectdashboard.jsp").forward(req,res);
    }
}