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

@WebServlet("/RaiseIssueServlet")
public class RaiseIssueServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Read form parameters
        String classIdStr   = req.getParameter("classId");
        String subjectIdStr = req.getParameter("subject_id");
        String teacherIdStr = req.getParameter("teacher_id");
        String category     = req.getParameter("category");
        String description  = req.getParameter("description");
        String subject = req.getParameter("subject");
        int subject_id=Integer.parseInt(subjectIdStr);
        int classId =Integer.parseInt(classIdStr);
        int teacher_id=Integer.parseInt(teacherIdStr);
        
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

            String sql = "INSERT INTO teacher_issues "
                    + "(teacher_id, subject_id, class_id, issue_description, category,school_id,school_name) "
                    + "VALUES (?, ?, ?, ?, ? , ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, teacher_id);
            ps.setInt(2, subject_id);
            ps.setInt(3, classId);
            ps.setString(5, category);
            ps.setString(4, description);
            ps.setInt(6, school_id);
            ps.setString(7, school_name);
     
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
