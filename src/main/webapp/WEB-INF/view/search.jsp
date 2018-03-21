<%@ page import="java.util.List" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.store.basic.ConversationStore" %>
<%@ page import="java.time.*" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
List<User> users = (List<User>) request.getAttribute("users");
%>

<!DOCTYPE html>
<html>
<head>
    <title>Search Results</title>
    <link rel="stylesheet" href="/css/main.css" type="text/css">
    <nav>
     <a id="navTitle" href="/">CodeU Chat App</a>
     <% if (request.getSession().getAttribute("user") != null) { %>
       <a>Hello <%= request.getSession().getAttribute("user") %>!</a>
       <a href="/activityFeed">Activity Feed</a>
       <a href="/conversations">Conversations</a>
       <a href="/logout">Logout</a>
     <% } else { %>
       <a href="/login">Login</a>
       <a href="/register">Register</a>
     <% } %>
     <a href="/about.jsp">About</a>
     <div id="search-container" style="padding-left:16px;">
       <form action="/search" method="GET">
         <input type="text" placeholder="Search for a user.." name="search" id="search">
         <button type="submit">Search</button>
       </form>
     </div>
   </nav>
   <style>
     #users {
       background-color: white;
       height: 500px;
       overflow-y: scroll
     }
   </style>

   <script>
     // scroll the chat div to the bottom
     function scrollChat() {
       var msgDiv = document.getElementById('messages');
       msgDiv.scrollTop = 0;
     };
   </script>
  </head>
  <body onload="scrollChat()">
  <h1 align ="center">Search Results</h1>
  <div id="container">
   <h2>Users</h2>
   <div id="users">
     <ul>
   <%
     for (User user : users) {
   %>
   <li><a href="/profile/<%= user.getName() %>">
     <%= user.getName() %></a></li>
   <%
     }
   %>
     </ul>
   </div>
 </div>
</body>
</html>
