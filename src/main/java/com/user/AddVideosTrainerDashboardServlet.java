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

@WebServlet("/AddVideosTrainerDashboardServlet")
public class AddVideosTrainerDashboardServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        // 1. Set Encoding
        req.setCharacterEncoding("UTF-8");
        
        String title = req.getParameter("video_title");
        String url = req.getParameter("video_url");
        String classIdParam = req.getParameter("class_id"); // Capture class_id
        
        HttpSession session = req.getSession(false);
        if (session == null) {
            res.sendRedirect("login.jsp");
            return;
        }

        Integer school_id = (Integer) session.getAttribute("school_id");
        String school_name = (String) session.getAttribute("school_name");
        Integer subject_id = (Integer) session.getAttribute("subject_id");
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
            
            // Prepare SQL with class_id
            String sql = "INSERT INTO videos (subject_id, title, video_url, school_id, school_name, description, class_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement st = con.prepareStatement(sql);
            
            // Logic to handle "All Classes" or specific class
            if ("all".equals(classIdParam)) {
                // Loop to insert for all classes (assuming 1 to 10)
                for (int i = 1; i <= 10; i++) {
                    st.setInt(1, subject_id);
                    st.setString(2, title);
                    st.setString(3, url);
                    st.setInt(4, school_id);
                    st.setString(5, school_name);
                    st.setString(6, "not defined");
                    st.setInt(7, i); // Set class ID
                    st.executeUpdate();
                }
            } else {
                // Insert for single class
                int classId = Integer.parseInt(classIdParam);
                st.setInt(1, subject_id);
                st.setString(2, title);
                st.setString(3, url);
                st.setInt(4, school_id);
                st.setString(5, school_name);
                st.setString(6, "not defined");
                st.setInt(7, classId); // Set class ID
                st.executeUpdate();
            }

            st.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        req.getRequestDispatcher("TrainerClassesServlet").forward(req, res);       
    }
}