<%@ page import="java.util.List" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.store.basic.ConversationStore" %>
<%@ page import="java.time.*" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%
List<User> users = (List<User>) request.getAttribute("users");
%>

<!DOCTYPE html>
<html>
  <head>
    <title>Search Results</title>
    <link rel="stylesheet" href="/css/main.css">
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet"
     id="bootstrap-css">
    <link rel="stylesheet" href="/css/main.css?DwvEcerrgedrdrdEeE1e" type="text/css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
    <script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
    <jsp:include page="./navbar.jsp" />
    <style>
      #users {
         background-color: white;
         height: 500px;
         overflow-y: scroll
      }
    </style>
    <script>
      function scrollChat() {
         var userDiv = document.getElementById('users');
         userDiv.scrollTop = 0;
     };
   </script>
 </head>
  <body onload="scrollChat()">
    <h1 id="title">Search Results</h1>
    <div id="container">
      <h2>Users</h2>
      <div id="users">
        <ul>
      <% for (User user : users) { %>
         <li><a href="/profile/<%= user.getName() %>">
         <%= user.getName() %></a></li>
      <% } %>
     </ul>
   </div>
 </div>
</body>
</html>
