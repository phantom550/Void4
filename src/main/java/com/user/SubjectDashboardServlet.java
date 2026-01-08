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

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Send data to JSP
        req.setAttribute("videos", videos);
        req.getRequestDispatcher("subjectdashboard.jsp").forward(req, res);
    }
}
