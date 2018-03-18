<%@ page import="java.util.List" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="java.time.*" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%
Conversation conversation = (Conversation) request.getAttribute("conversation");
List<Message> messages = (List<Message>) request.getAttribute("messages");
User user = (User) request.getAttribute("user");
%>

<!DOCTYPE html>
<html>
<head>
  <title>Activity</title>
  <link rel="stylesheet" href="/css/main.css">
   <nav>
     <a id="navTitle" href="/">CodeU Chat App</a>
     <% if(request.getSession().getAttribute("user") != null){ %>
       <a>Hello <%= request.getSession().getAttribute("user") %>!</a>
       <a href="/activityFeed">Activity Feed</a>
     <% } else{ %>
       <a href="/login">Login</a>
       <a href="/register">Register</a>
     <% } %>
     <a href="/conversations">Conversations</a>
     <a href="/about.jsp">About</a>
   </nav>
  <style>
    #activity {
      background-color: white;
      height: 500px;
      overflow: scroll
    }
  </style>

  <script>
    function scrollBox() {
      var activityDiv = document.getElementById('activity');
        activityDiv.scrollTop = activityDiv.scrollHeight;
    };
  </script>
</head>
<body onload="scrollBox()">
  <div id="container">
  	<h1>Activity</h1>
  	<p>Here&#39s everything that happened on the site so far!</p>
    <div id="activity">
    </div>
  </div>
</body>
</html>