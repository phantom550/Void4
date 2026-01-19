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

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("text/html");

        // 1. Get Parameters
        String idStr = req.getParameter("id");
        String password = req.getParameter("password");
        String subject = req.getParameter("subject");
        String schoolid = req.getParameter("school_id");
        String school_name = req.getParameter("school_name");

        // 2. Validate Input
        if (idStr == null || idStr.isEmpty() || password == null || password.isEmpty() || schoolid == null) {
            res.getWriter().println("<h3>Error: ID, Password, and School ID are required.</h3>");
            return;
        }

        int school_id;
        int id;
        try {
            school_id = Integer.parseInt(schoolid);
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            res.getWriter().println("<h3>Error: ID and School ID must be numeric.</h3>");
            return;
        }

        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;

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
            
            // 3. Authenticate User
            String sql = "SELECT password FROM login WHERE subject_id=? AND subject=? and school_id=?";
            st = con.prepareStatement(sql);
            st.setInt(1, id);
            st.setString(2, subject);
            st.setInt(3, school_id);
            rs = st.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");

                if (dbPassword.equals(password)) {
                    // --- LOGIN SUCCESS ---
                    
                    // Start Session
                    HttpSession session = req.getSession();
                    session.setAttribute("school_id", school_id);
                    session.setAttribute("school_name", school_name);
                    session.setAttribute("db_source", connectedUrl); // Save DB source to show on dashboard

                    req.setAttribute("subject", subject);
                    req.setAttribute("subject_id", idStr);
                    
                    // Forward to Dashboard
                    req.getRequestDispatcher("/SubjectDashboardServlet").forward(req, res);
                } else {
                    res.getWriter().println("<h3 style='color:red;'>Login Failed: Invalid Password</h3>");
                }
            } else {
                res.getWriter().println("<h3 style='color:red;'>Login Failed: User not found</h3>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.getWriter().println("<h3>Database Connection Error: " + e.getMessage() + "</h3>");
        } finally {
            try { if(rs != null) rs.close(); } catch(Exception e){}
            try { if(st != null) st.close(); } catch(Exception e){}
            try { if(con != null) con.close(); } catch(Exception e){}
        }
    }
}