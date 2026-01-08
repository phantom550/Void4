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

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/void4",
                    "root",
                    "root"
            );

            String sql = "SELECT title, video_url FROM trainer_videos WHERE subject=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, subject);

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
        
        req.getRequestDispatcher("subjectdashboard.jsp").forward(req,res);
    }
}
