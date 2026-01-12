import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("text/html");

        String idStr = req.getParameter("id");
        String password = req.getParameter("password");
        String subject = req.getParameter("subject");

        // Validation
        if (idStr == null || idStr.isEmpty() ||
            password == null || password.isEmpty()) {

            res.getWriter().println("ID and Password are required");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            res.getWriter().println("ID must be a number");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/void4",
                "root",
                "root"
            );

            String sql = "SELECT password FROM login WHERE subject_id=? AND subject=?";
            PreparedStatement st = con.prepareStatement(sql);
            st.setInt(1, id);
            st.setString(2, subject);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");

                if (dbPassword.equals(password)) {
                    res.getWriter().println("Login Successful");
                } else {
                    res.getWriter().println("Invalid Password");
                }
            } else {
                res.getWriter().println("User not found");
            }

            rs.close();
            st.close();
            con.close();
            req.setAttribute("subject",subject);
            req.setAttribute("subject_id",idStr);
            req.getRequestDispatcher("/SubjectDashboardServlet").forward(req, res);


        } catch (Exception e) {
            e.printStackTrace();
            res.getWriter().println("Database Error");
        }
    }
}
