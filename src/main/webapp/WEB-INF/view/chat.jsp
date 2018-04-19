<%--
  Copyright 2017 Google Inc.
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
--%>
<%@ page import="java.util.List" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.data.User" %>
<%@ page import="java.util.UUID" %>

<%
Conversation conversation = (Conversation) request.getAttribute("conversation");
List<Message> messages = (List<Message>) request.getAttribute("messages");
List<UUID> conversationUsers = (List<UUID>) request.getAttribute("conversationUsers");
User user = (User) UserStore.getInstance().getUser((String) request.getSession().getAttribute("user"));
%>

<!DOCTYPE html>
<html>
<head>
  <title><%= conversation.getTitle() %></title>
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

    <h1><%= conversation.getTitle() %>
      <a href="" style="float: right">&#8635;</a></h1>

    <hr/>

    <div id="chat">
      <ul>
    <%
      if (user != null && conversationUsers.contains(user.getId())) {
        for (Message message : messages) {
          String author = UserStore.getInstance().getUser(message.getAuthorId()).getName();
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

    <% if (user != null && conversationUsers.contains(user.getId())) { %>
    <form id="chatform" action="/chat/<%= conversation.getTitle() %>" method="POST">
        <textarea name="message"></textarea>
        </br>
        <button type="submit">Send</button>
        </br>
        <button type="submit" name="button" value="leaveButton">Leave Conversation</button>
    </form>
    <% } else if (user != null && !(conversationUsers.contains(user.getId()))) { %>
    <p> Join the conversation to send a message! </p>
    <form id="chatform" action="/chat/<%= conversation.getTitle() %>" method="POST">
            <button type="submit" name="button" value="joinButton">Join Conversation</button>
    </form>
    <% } else { %>
      <p><a href="/login">Login</a> to send a message.</p>
    <% } %>

    <hr/>

  </div>

</body>
</html>
