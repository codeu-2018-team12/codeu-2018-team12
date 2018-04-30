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
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <link rel="stylesheet" href="/css/main.css" type="text/css">
  <link rel="stylesheet" href="/css/chat.css" type="text/css">
  <style>
   [hidden] {
     display: none !important;
   }
  </style>
  <jsp:include page="./navbar.jsp" />
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  <script>
    // scroll the chat div to the bottom
    function scrollChat() {
      var chatDiv = document.getElementById('chat');
      chatDiv.scrollTop = chatDiv.scrollHeight;
    };

    document.getElementById("image").onchange = function() {
        document.getElementById("chatForm").submit();
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
      }
    %>
    <%
      if (user != null && !conversationUsers.contains(user.getId())) {
    %>
      <h2> Messages from this conversation will appear here! </h2>
      <p> To see these messages, you must first join the conversation. </p>
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
    <form id="chatForm" action="/chat/<%= conversation.getTitle() %>" method="POST" enctype="multipart/form-data">
        <textarea placeholder="Enter your message here" name="message"></textarea>
        </br>
        <label class="btn btn-info">
        <span class="glyphicon glyphicon-camera"></span>  Upload Photo
        <input type="file" name="image" id="image" accept="image/*" hidden>
        </label>
        <button type="submit" class="btn btn-info" value="submitMessage"> Submit
        </button>
        <button type="submit" class="btn btn-info" name="button" value="leaveButton"> Leave Conversation
        </button>
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
