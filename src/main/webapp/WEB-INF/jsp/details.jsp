<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.campusfind.model.Item" %>
<% Item item = (Item) request.getAttribute("item"); Boolean canClaim = (Boolean) request.getAttribute("canClaim"); %>
<jsp:include page="header.jsp" />
<a class="back-link" href="<%=request.getContextPath()%>/browse">← Back to Browse</a>
<% if (item.claimed) { %>
  <div class="detail-card"><div class="detail-info">
    <span class="type-badge type-found">Claimed</span>
    <h1><%= item.itemName %></h1>
    <p>This item has been claimed by its owner and removed from listings.</p>
  </div></div>
<% } else { %>
  <div class="detail-card">
    <% if (item.imageUrl != null && !item.imageUrl.isEmpty()) { %>
      <img class="detail-image" src="<%= item.imageUrl %>" alt=""/>
    <% } %>
    <div class="detail-info">
      <span class="type-badge <%= item.isLost() ? "type-lost" : "type-found" %>"><%= item.type %></span>
      <h1><%= item.itemName %></h1>
      <p class="detail-description"><%= item.description %></p>
      <div class="detail-meta">
        <div><strong>Location:</strong> <%= item.location %></div>
        <div><strong>Date:</strong> <%= item.date %></div>
        <div><strong>Contact:</strong> <%= item.contact %></div>
        <div><strong>Reported by:</strong> <%= item.reporterName == null ? "a member" : item.reporterName %></div>
      </div>
      <% if (canClaim != null && canClaim) { %>
        <form action="<%=request.getContextPath()%>/claim" method="post" onsubmit="return confirm('Confirm owner received this item? It will be removed from listings.')">
          <input type="hidden" name="id" value="<%= item.id %>"/>
          <button class="claim-btn" type="submit">✓ Claim — Owner received it</button>
        </form>
      <% } else { %>
        <p class="claim-note">Only the reporter or faculty can mark this as claimed. Log in to claim.</p>
      <% } %>
    </div>
  </div>
<% } %>
<jsp:include page="footer.jsp" />
