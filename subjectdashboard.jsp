<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.Map" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Teacher Training Portal</title>
    <link rel="stylesheet" href="subjectdashboard.css">
</head>
<body>

<!-- Navigation Bar -->
<nav>
    <div class="nav-subject">Subject: Placeholder</div>
    <div class="profile-container">
        <span>Welcome, User</span>
        <img src="https://ui-avatars.com/api/?name=Teacher+User&background=random"
             alt="Profile" class="profile-photo">
    </div>
</nav>

<div class="main-container">

    <!-- Sidebar -->
    <aside class="sidebar">
        <h3>Grades</h3>
        <button class="class-btn" onclick="location.href='ClassesServlet?classId=1'">1<sup>st</sup> Std</button>
        <button class="class-btn" onclick="location.href='ClassesServlet?classId=2'">2<sup>nd</sup> Std</button>
        <button class="class-btn" onclick="location.href='ClassesServlet?classId=3'">3<sup>rd</sup> Std</button>
        <button class="class-btn" onclick="location.href='ClassesServlet?classId=4'">4<sup>th</sup> Std</button>
        <button class="class-btn" onclick="location.href='ClassesServlet?classId=5'">5<sup>th</sup> Std</button>
        <button class="class-btn" onclick="location.href='ClassesServlet?classId=6'">6<sup>th</sup> Std</button>
        <button class="class-btn" onclick="location.href='ClassesServlet?classId=7'">7<sup>th</sup> Std</button>
        <button class="class-btn" onclick="location.href='ClassesServlet?classId=8'">8<sup>th</sup> Std</button>
        <button class="class-btn" onclick="location.href='ClassesServlet?classId=9'">9<sup>th</sup> Std</button>
        <button class="class-btn" onclick="location.href='ClassesServlet?classId=10'">10<sup>th</sup> Std</button>
    </aside>

    <!-- Content Area -->
    <main class="content-area">

        <!-- Video Section -->
        <section class="video-section">
            <h2 class="section-header">Resources & Lessons</h2>

            <div class="video-list">
                <%
                    List<Map<String, String>> videos =
                            (List<Map<String, String>>) request.getAttribute("videos");

                    if (videos != null && !videos.isEmpty()) {
                        for (Map<String, String> video : videos) {
                %>

                <div class="video-card">
    <div class="thumbnail-box">
        <video width="100%" controls>
            <source src="<%= video.get("url") %>" type="video/mp4">
            Your browser does not support the video tag.
        </video>
    </div>

    <div class="video-info">
        <h4><%= video.get("title") %></h4>

        <a href="<%= video.get("url") %>" target="_blank" class="video-link">
            ▶ Watch Full Video
        </a>
    </div>
</div>


                <%
                        }
                    } else {
                %>
                    <p>No videos available.</p>
                <%
                    }
                %>
            </div>
        </section>

        <!-- Notices Section -->
        <section class="notices-section">
            <h2 class="section-header">Important Notices</h2>

            <div class="notice-card">
                <span>Exam schedule for Term 1 released.</span>
                <span>Oct 24, 2023</span>
            </div>

            <div class="notice-card">
                <span>Holiday declared for Deepavali.</span>
                <span>Oct 22, 2023</span>
            </div>
        </section>

    </main>
</div>

</body>
</html>
