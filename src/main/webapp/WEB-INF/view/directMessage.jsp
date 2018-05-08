<%@ page import="java.util.List" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.data.User" %>
<%@ page import="java.util.UUID" %>

<%
Conversation conversation = (Conversation) request.getAttribute("conversation");
List<Message> messages = (List<Message>) request.getAttribute("messages");
User loggedInUser = (User) request.getAttribute("loggedInUser");
User otherUser = (User) request.getAttribute("otherUser");
%>

<!DOCTYPE html>
<html>
  <head>
    <title>Messages With <%= otherUser.getName() %></title>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet"
     id="bootstrap-css">
    <link rel="stylesheet" href="/css/main.css?DwvEcerrgedrdrdEeE1e" type="text/css">
    <link rel="stylesheet" href="/css/chat.css?DwvEcerrgedrdrdEeE1e" type="text/css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
    <script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
    <jsp:include page="./navbar.jsp" />
    <script>
      function scrollChat() {
        var chatDiv = document.getElementById('chat');
        chatDiv.scrollTop = chatDiv.scrollHeight;
      };
    </script>
 </head>
<body onload="scrollChat()">

  <div id="container">
    <h1>Messages With <%= otherUser.getName() %>
      <a href="" style="float: right">&#8635;</a></h1><hr/>
    <div id="chat">
    <ul>
      <%
        for (Message message : messages) {
          String author = UserStore.getInstance()
            .getUser(message.getAuthorId()).getName();
      %>
          <li><strong><a href="/profile/<%= author %>"><%= author %></a>:</strong> <%= message.getContent() %></li>
      <% } %>
    </ul>
    </div><hr/>

    <% if (request.getAttribute("error") != null) { %>
      <h2 style="color:red"><%= request.getAttribute("error") %></h2>
    <% } %>

    <form id="chatform" action="/direct/<%= otherUser.getName() %>" method="POST">
        <textarea name="message"></textarea>
        </br>
        <button type="submit">Send</button>
        </br>
    </form>
    <hr/>
  </div>
</body>
</html>
