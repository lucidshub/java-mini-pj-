<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="header.jsp" />
<div class="auth-page">
<form class="auth-card" action="<%=request.getContextPath()%>/login" method="post">
  <h1>Welcome back</h1>
  <p>Log in to report or claim items.</p>
  <% String err = (String) request.getAttribute("error"); %>
  <% if (err != null) { %><div class="form-error"><%= err %></div><% } %>
  <div class="form-group"><label>Username</label>
    <input type="text" name="username" placeholder="Student PRN or @acpce.ac.in email" required/></div>
  <div class="form-group"><label>Password</label><input type="password" name="password" required/></div>
  <button class="submit-btn" type="submit">Log In</button>
  <p>Don't have an account? <a href="<%=request.getContextPath()%>/register">Sign up</a></p>
</form>
</div>
<jsp:include page="footer.jsp" />
