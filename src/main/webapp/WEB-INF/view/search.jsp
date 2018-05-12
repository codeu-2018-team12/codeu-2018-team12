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
List<Conversation> conversations = (List<Conversation>) request.getAttribute("conversations");
List<Message> messages = (List<Message>) request.getAttribute("messages");
%>

<!DOCTYPE html>
<html>
  <head>
    <title>Search Results</title>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet"
     id="bootstrap-css">
    <jsp:include page="./navbar.jsp" />
    <link rel="stylesheet" href="/css/main.css" type="text/css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
    <script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
    <style>
     #results {
       background-color: white;
       height: 500px;
       overflow-y: scroll
     }
    </style>
    <script>
     // scroll the chat div to the bottom
     function scrollChat() {
       var resultsDiv = document.getElementById('results');
       resultsDiv.scrollTop = 0;
     };
   </script>
 </head>
  <body onload="scrollChat()">
  <h1 id="title">Search Results</h1>
  <div id="container">
   <%
    if (users != null) {
   %>
     <h2>Users</h2>
     <div id="results">
       <ul>
     <%
       for (User user : users) {
     %>
        <li><a href="/profile/<%= user.getName() %>">
         <%= user.getName() %></a></li>
     <%
      }
    } else if (conversations != null) { %>
      <h2>Conversations</h2>
      <div id="results">
        <ul>
      <%
        for (Conversation convo : conversations) {
      %>
          <li><a href="/chat/<%= convo.getTitle() %>">
          <%= convo.getTitle() %></a></li>
     <%
        }
    } else if (messages != null) { %>
      <h2>Messages</h2>
      <div id="results">
        <ul>
      <%
      for (Message msg : messages) {
        String author = UserStore.getInstance().getUser(msg.getAuthorId()).getName();
        %>
        <li><strong><a href="/profile/<%= author %>"><%= author %></a>:</strong> <%= msg.getContent() %></li>
      <%
      }
    }
   %>
     </ul>
   </div>
 </div>
</body>
</html>
