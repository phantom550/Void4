package com.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/ClassesServlet")
public class ClassesServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String classId = req.getParameter("classId");
        String subject = req.getParameter("subject");
        String subject_i = req.getParameter("subject_id");
        int subject_id=Integer.parseInt(subject_i);
        int classI =Integer.parseInt(classId);
        List<Map<String, String>> videos = new ArrayList<>();
        List<Map<String, String>> notice = new ArrayList<>();
        ArrayList<Integer> attend = new ArrayList();
        ArrayList<Integer> beheviour = new ArrayList();
        int low=0;
        int avg=0;
        int good=0;
        int teacher_id = 0;
        String email = null;
        String tname=null;
         java.sql.Date jDate = null;
         String sub = null ;
         int total=0;
        String m="math";

        System.out.println("Aisha topped class " + classId + " in " + subject+" "+subject_id);
        	   
        	   try {
                   Class.forName("com.mysql.cj.jdbc.Driver");
                   Connection con = DriverManager.getConnection(
                           "jdbc:mysql://localhost:3306/void4",
                           "root",
                           "root"
                   );

                   String sql = "SELECT title, video_url FROM videos WHERE subject_id=? and class_id=?";
                   PreparedStatement ps = con.prepareStatement(sql);
                   ps.setInt(1, subject_id);
                   ps.setInt(2, classI);

                   ResultSet rs = ps.executeQuery();

                   while (rs.next()) {
                       Map<String, String> video = new HashMap<>();
                       video.put("title", rs.getString("title"));
                       video.put("url", rs.getString("video_url"));
                       videos.add(video);
                   }
                   
                   String sql1 = "SELECT * FROM notices WHERE subject=? and class_id=?";
                   PreparedStatement ps1 = con.prepareStatement(sql1);
                   ps1.setString(1, subject);
                   ps1.setInt(2, classI);

                   ResultSet rs1 = ps1.executeQuery();

                   while (rs1.next()) {
                       Map<String, String> notices = new HashMap<>();
                       notices.put("description", rs1.getString("description"));
                       notices.put("date", rs1.getString("created_date"));
                       notice.add(notices);
                   }
                   
                   String sql2 = "SELECT attendance FROM students WHERE class_id=?";
                   PreparedStatement ps2 = con.prepareStatement(sql2);
                   ps2.setInt(1, classI);

                   ResultSet rs2 = ps2.executeQuery();
                   while (rs2.next()) {
                       
                       attend.add(rs2.getInt("attendance"));
                       
                   }
                   
                   String sql3 = "SELECT name,teacher_id,email,joined_date,subject FROM teachers WHERE class_id=? and subject=?";
                   PreparedStatement ps3 = con.prepareStatement(sql3);
                   ps3.setInt(1, classI);
                   ps3.setString(2, subject);

                   ResultSet rs3 = ps3.executeQuery();
                       
                   if (rs3.next()) { 
                	   tname=rs3.getString("name");
                       teacher_id = rs3.getInt("teacher_id");
                       email = rs3.getString("email");
                       jDate = rs3.getDate("joined_date");
                       sub = rs3.getString("subject");
                  
                   }
    
                   

					
					  String sql4 = "SELECT behaviour_score FROM students WHERE class_id=?";
					  PreparedStatement ps4 = con.prepareStatement(sql4);
					  ps4.setInt(1, classI);
					  
					 
					  ResultSet rs4 = ps4.executeQuery();
					  
					  while(rs4.next())
					  {
						  beheviour.add(rs4.getInt("behaviour_score"));
					 
					 }
					  
					  for(int i : beheviour) {
						  
						  if(i>=0 && i<=4) {
							  low++;
						  }
						  else if(i>4 && i<=7) {
							  avg++;
						  }
						  else {
							  good++;
						  }
						  
					  }
					 
                   
                   String sql5 = "SELECT count(*) as total FROM students WHERE class_id=?";
                   PreparedStatement ps5 = con.prepareStatement(sql5);
                   ps5.setInt(1, classI);
                   
                   ResultSet rs5 = ps5.executeQuery();
                   
                   if (rs5.next()) { 
                	  total=rs5.getInt("total");
                  
                   }
                   

                   rs4.close();
                   ps4.close();
                   rs5.close();
                   ps5.close();
                   rs3.close();
                   ps3.close();
                   rs2.close();
                   ps2.close();
                   rs1.close();
                   ps1.close();
                   rs.close();
                   ps.close();
                   con.close();
                   
               } catch (Exception e) {
                   e.printStackTrace();
               }
        	   
           
        // Send data to JSP
        req.setAttribute("classId", classId);
        req.setAttribute("subject", subject);
        req.setAttribute("subject_id", subject_i);
        req.setAttribute("videos", videos);
        req.setAttribute("notice",notice);
        req.setAttribute("attend", attend);
        req.setAttribute("email", email);
        req.setAttribute("sub", sub);
        req.setAttribute("jDate", jDate);
        req.setAttribute("teacher_id", teacher_id);
        req.setAttribute("name", tname);
        req.setAttribute("total", total);
        req.setAttribute("low", low);
        req.setAttribute("avg", avg);
        req.setAttribute("good", good);

        RequestDispatcher rd = req.getRequestDispatcher("classes.jsp");
        rd.forward(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doGet(req, res);
    }
}
