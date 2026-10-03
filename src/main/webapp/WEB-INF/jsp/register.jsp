<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="header.jsp" />
<div class="auth-page">
<form class="auth-card" action="<%=request.getContextPath()%>/register" method="post">
  <h1>Create account</h1>
  <p>Students: 9-digit PRN. Faculty: @acpce.ac.in email.</p>
  <% String err = (String) request.getAttribute("error"); %>
  <% if (err != null) { %><div class="form-error"><%= err %></div><% } %>
  <div class="form-group"><label>Full name</label><input type="text" name="name" required/></div>
  <div class="form-group"><label>Username</label><input type="text" name="username" placeholder="e.g. 123456789 or name@acpce.ac.in" required/></div>
  <div class="form-group"><label>Password (min 4 chars)</label><input type="password" name="password" required/></div>
  <button class="submit-btn" type="submit">Sign Up</button>
  <p>Already have an account? <a href="<%=request.getContextPath()%>/login">Log in</a></p>
</form>
</div>
<jsp:include page="footer.jsp" />
