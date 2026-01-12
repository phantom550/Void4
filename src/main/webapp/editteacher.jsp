<%
    int teacher_id = Integer.parseInt(request.getParameter("teacher_id"));
    String classId = request.getParameter("classId");
    String subject = request.getParameter("subject");
    String subject_id = request.getParameter("subject_id");
%>

<form action="EditTeacherServlet" method="post">

<input type="hidden" name="classId" value="<%= classId %>">
<input type="hidden" name="subject" value="<%= subject %>">
<input type="hidden" name="subject_id" value="<%= subject_id %>">

<p>Teacher ID</p>
<input type="text" name="teacher_id" value="<%= teacher_id %>" readonly>

<p>Teacher Name</p>
<input type="text" name="teacher_name" required>

<p>Email</p>
<input type="email" name="email" required>

<p>Joined Date</p>
<input type="date" name="joined_date" required>

<br><br>
<button type="submit">Update</button>

</form>
