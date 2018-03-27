<!DOCTYPE html>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.data.User" %>
<html>
<head>
  <title>Register</title>
  <link rel="stylesheet" href="/css/main.css">
  <style>
    label {
      display: inline-block;
      width: 100px;
    }
  </style>
</head>
<body>
<jsp:include page="./navbar.jsp" />
  <div id="container">
    <h1>Register</h1>

    <% if(request.getAttribute("error") != null){ %>
       <h2 style="color:red"><%= request.getAttribute("error") %></h2>
    <% } %>

    <form action="/register" method="POST">
      <label for="username">Username: </label>
      <input type="text" name="username" id="username">
      <br/>
      <label for="password">Password: </label>
      <input type="password" name="password" id="password">
      <br/>
      <label for="confirmPassword">Confirm Password: </label>
      <input type="password" name="confirmPassword" id="confirmPassword">
      <br/>
      <br/><br/>
      <button type="submit">Submit</button>
    </form>
  </div>
</body>
</html>
