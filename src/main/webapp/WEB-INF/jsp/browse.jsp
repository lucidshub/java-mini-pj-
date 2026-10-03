<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, com.campusfind.model.Item" %>
<%
  List<Item> items = (List<Item>) request.getAttribute("items");
  if (items == null) items = new java.util.ArrayList<>();
  String q = (String) request.getAttribute("q"); if (q == null) q = "";
  String filter = (String) request.getAttribute("filter"); if (filter == null) filter = "all";
  String ctx = request.getContextPath();
%>
<jsp:include page="header.jsp" />
<h1>Browse Items</h1>
<form class="search-form" action="<%=ctx%>/browse" method="get" style="justify-content:flex-start">
  <input type="text" name="q" value="<%= q.replace("\"","&quot;") %>" placeholder="Search items..."/>
  <input type="hidden" name="type" value="<%= filter %>"/>
  <button type="submit">Search</button>
</form>
<div class="filter-buttons">
  <a class="filter-btn <%= "all".equals(filter) ? "active" : "" %>" href="<%=ctx%>/browse?q=<%= java.net.URLEncoder.encode(q, "UTF-8") %>&type=all">All</a>
  <a class="filter-btn <%= "lost".equals(filter) ? "active" : "" %>" href="<%=ctx%>/browse?q=<%= java.net.URLEncoder.encode(q, "UTF-8") %>&type=lost">Lost</a>
  <a class="filter-btn <%= "found".equals(filter) ? "active" : "" %>" href="<%=ctx%>/browse?q=<%= java.net.URLEncoder.encode(q, "UTF-8") %>&type=found">Found</a>
</div>
<% if (items.isEmpty()) { %><p class="empty-text">No items found.</p><% } %>
<div class="items-grid">
  <% for (Item it : items) { %>
    <a class="item-card" href="<%=ctx%>/item?id=<%=it.id%>">
      <% if (it.imageUrl != null && !it.imageUrl.isEmpty()) { %><img src="<%=it.imageUrl%>" alt=""/><% } %>
      <div class="item-card-body">
        <span class="type-badge <%= it.isLost() ? "type-lost" : "type-found" %>"><%= it.type %></span>
        <h3><%= it.itemName %></h3>
        <p><%= it.location %></p>
      </div>
    </a>
  <% } %>
</div>
<jsp:include page="footer.jsp" />
