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
  <link rel="stylesheet" href="/css/main.css" type="text/css">
  <jsp:include page="./navbar.jsp" />

  <style>
    #chat {
      background-color: white;
      height: 500px;
      overflow-y: scroll;
      word-break: break-all;
      word-wrap: break-word;
    }
  </style>

  <script>
    // scroll the chat div to the bottom
    function scrollChat() {
      var chatDiv = document.getElementById('chat');
      chatDiv.scrollTop = chatDiv.scrollHeight;
    };
  </script>
</head>
<body onload="scrollChat()">

  <div id="container">
    <h1>Messages With <%= otherUser.getName() %>
      <a href="" style="float: right">&#8635;</a></h1>

    <hr/>

    <div id="chat">
      <ul>
    <%
      for (Message message : messages) {
        String author = UserStore.getInstance()
          .getUser(message.getAuthorId()).getName();
    %>
      <li><strong><a href="/profile/<%= author %>"><%= author %></a>:</strong> <%= message.getContent() %></li>
    <%
      }
    %>
      </ul>
    </div>

    <hr/>

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
