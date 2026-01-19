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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet implementation class TrainerGetDataServlet
 */
@WebServlet("/TrainerGetDataServlet")
public class TrainerGetDataServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
    	
    	
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
         
         System.out.println(school_id+school_name);

        List<Map<String, String>> teachers = new ArrayList<>();
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

            String sql = "SELECT teacher_id, name, email, subject_id, " +
                         "experience, joined_date, school_id, school_name, class_id FROM teachers where subject=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, subject_name);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, String> teacher = new HashMap<>();

                teacher.put("id", rs.getString("teacher_id"));
                teacher.put("name", rs.getString("name"));
                teacher.put("email", rs.getString("email"));
                teacher.put("sub_id", rs.getString("subject_id"));
                teacher.put("experience", rs.getString("experience"));
                teacher.put("join_date", rs.getString("joined_date"));
                teacher.put("school_id", rs.getString("school_id"));
                teacher.put("school_name", rs.getString("school_name"));
                teacher.put("class_id", rs.getString("class_id"));

                teachers.add(teacher);
            }
            
    
            
            System.out.println("Teachers count: " + teachers.size());

            req.setAttribute("teacherList", teachers);
            req.getRequestDispatcher("getdata.jsp").forward(req, res);

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
