<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.campusfind.model.User" %>
<%
  User me = (User) session.getAttribute("user");
  String ctx = request.getContextPath();
  String path = request.getServletPath();
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>CampusFind</title>
  <link rel="stylesheet" href="<%=ctx%>/css/style.css"/>
</head>
<body>
<nav class="navbar">
  <a href="<%=ctx%>/" class="nav-brand">CampusFind</a>
  <div class="nav-links">
    <a href="<%=ctx%>/" class="<%= "/".equals(path) || "/home".equals(path) || path == null ? "active" : "" %>">Home</a>
    <a href="<%=ctx%>/browse" class="<%="/browse".equals(path) ? "active" : ""%>">Browse</a>
    <% if (me != null) { %>
      <span class="nav-user"><%= me.name %> (<%= me.role %>)</span>
      <a href="<%=ctx%>/logout">Log out</a>
    <% } else { %>
      <a href="<%=ctx%>/login">Log in</a>
      <a href="<%=ctx%>/register">Sign up</a>
    <% } %>
  </div>
  <div class="nav-action">
    <a href="<%=ctx%>/report" class="nav-report-btn">+ Report Item</a>
  </div>
</nav>
<main class="main-content">
