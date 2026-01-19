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

@WebServlet("/AddNoticeTrainerClassesServlet")
public class AddNoticeTrainerClassesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        req.setCharacterEncoding("UTF-8"); // Fix encoding
        
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String classIdParam = req.getParameter("class_id"); // Capture class_id
    
        HttpSession session = req.getSession(false);
        if (session == null) {
            res.sendRedirect("login.jsp");
            return;
        }

        Integer school_id = (Integer) session.getAttribute("school_id");
        String school_name = (String) session.getAttribute("school_name");
        String subject = (String) session.getAttribute("subject_name");
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
            String sql = "INSERT INTO notices (subject, description, title, school_id, school_name, class_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement st = con.prepareStatement(sql);
            
            if ("all".equals(classIdParam)) {
                for (int i = 1; i <= 10; i++) {
                    st.setString(1, subject);
                    st.setString(2, description);
                    st.setString(3, title);
                    st.setInt(4, school_id);
                    st.setString(5, school_name);
                    st.setInt(6, i); // Set class_id
                    st.executeUpdate();
                }
            } else {
                int classId = Integer.parseInt(classIdParam);
                st.setString(1, subject);
                st.setString(2, description);
                st.setString(3, title);
                st.setInt(4, school_id);
                st.setString(5, school_name);
                st.setInt(6, classId); // Set class_id
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