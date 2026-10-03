<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, com.campusfind.model.Item" %>
<% List<Item> recent = (List<Item>) request.getAttribute("recentItems"); if (recent == null) recent = new java.util.ArrayList<>(); %>
<jsp:include page="header.jsp" />
<section class="hero-section">
  <h1>CampusFind</h1>
  <p class="hero-subtitle">Lost something? Found something? Help your fellow students.</p>
  <form class="search-form" action="<%=request.getContextPath()%>/browse" method="get">
    <input type="text" name="q" placeholder="Search wallet, bottle, calculator..."/>
    <button type="submit">Search</button>
  </form>
  <a href="<%=request.getContextPath()%>/report" class="hero-report-btn">+ Report an Item</a>
</section>

<section>
  <h2>Recent Items</h2>
  <% if (recent.isEmpty()) { %>
    <p class="empty-text">No items yet. Be the first to report one!</p>
  <% } %>
  <div class="items-grid">
    <% for (Item it : recent) { %>
      <a class="item-card" href="<%=request.getContextPath()%>/item?id=<%=it.id%>">
        <% if (it.imageUrl != null && !it.imageUrl.isEmpty()) { %><img src="<%=it.imageUrl%>" alt=""/><% } %>
        <div class="item-card-body">
          <span class="type-badge <%= it.isLost() ? "type-lost" : "type-found" %>"><%= it.type %></span>
          <h3><%= it.itemName %></h3>
          <p><%= it.location %></p>
        </div>
      </a>
    <% } %>
  </div>
  <% if (!recent.isEmpty()) { %>
    <a class="view-all-link" href="<%=request.getContextPath()%>/browse">View all items →</a>
  <% } %>
</section>
<jsp:include page="footer.jsp" />
