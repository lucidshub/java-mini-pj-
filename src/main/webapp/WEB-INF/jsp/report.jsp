<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="header.jsp" />
<h1>Report an Item</h1>
<p>Found something or lost something? Fill out this form to help get it returned.</p>
<% String err = (String) request.getAttribute("error"); %>
<% if (err != null) { %><div class="form-error"><%= err %></div><% } %>
<form class="auth-card" style="width:min(560px,94vw)" action="<%=request.getContextPath()%>/report" method="post" enctype="multipart/form-data">
  <div class="form-group">
    <label>Type</label>
    <select name="type"><option value="found">Found</option><option value="lost">Lost</option></select>
  </div>
  <div class="form-group"><label>Item name</label><input type="text" name="itemName" required/></div>
  <div class="form-group"><label>Description</label><textarea name="description" rows="3" required></textarea></div>
  <div class="form-group"><label>Location</label><input type="text" name="location" required/></div>
  <div class="form-group"><label>Date</label><input type="date" name="date" required/></div>
  <div class="form-group"><label>Photo (optional, max 2MB)</label><input type="file" name="image" accept="image/*"/></div>
  <div class="form-group"><label>Contact (email/phone)</label><input type="text" name="contact" required/></div>
  <button class="submit-btn" type="submit">Submit Item</button>
</form>
<jsp:include page="footer.jsp" />
