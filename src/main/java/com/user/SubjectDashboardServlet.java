package com.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/SubjectDashboardServlet")
public class SubjectDashboardServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        List<Map<String, String>> videos = new ArrayList<>();
        List<Map<String, String>> notice = new ArrayList<>();

        String subject = (String) req.getAttribute("subject");
        String subject_id = (String) req.getAttribute("subject_id");
        
        HttpSession session = req.getSession();
        int school_id = (int) session.getAttribute("school_id");
        String school_name = (String) session.getAttribute("school_name");
      
       

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

            String sql = "SELECT title, video_url FROM trainer_videos WHERE subject=? and school_id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, subject);
            ps.setInt(2, school_id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, String> video = new HashMap<>();
                video.put("title", rs.getString("title"));
                video.put("url", rs.getString("video_url"));
                videos.add(video);
            }
            
            String sql1 = "SELECT * FROM notices WHERE subject=?";
            PreparedStatement ps1 = con.prepareStatement(sql1);
            ps1.setString(1, subject);

            ResultSet rs1 = ps1.executeQuery();

            while (rs1.next()) {
                Map<String, String> notices = new HashMap<>();
                notices.put("description", rs1.getString("description"));
                notices.put("date", rs1.getString("created_date"));
                notice.add(notices);
            }

            rs.close();
            ps.close();
            rs1.close();
            ps1.close();
            con.close();
            
            

        } catch (Exception e) {
            e.printStackTrace();
        }

        req.setAttribute("videos", videos);
       
        
        req.setAttribute("notice",notice);
        req.setAttribute("subject",subject);
        req.setAttribute("subject_id",subject_id);
        
        req.getRequestDispatcher("subjectdashboard.jsp").forward(req,res);
    }
}
