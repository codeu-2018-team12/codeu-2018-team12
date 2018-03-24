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
<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.data.User" %>
<%
Conversation conversation = (Conversation) request.getAttribute("conversation");
List<Message> messages = (List<Message>) request.getAttribute("messages");
List<User> conversationUsers = (List<User>) request.getAttribute("conversationUsers");
User user = (User) UserStore.getInstance().getUser((String) request.getSession().getAttribute("user"));
%>

<!DOCTYPE html>
<html>
<head>
  <title><%= conversation.getTitle() %></title>
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
 </nav>

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
      for (Message message : messages) {
        String author = UserStore.getInstance()
          .getUser(message.getAuthorId()).getName();
    %>
      <li><strong><%= author %>:</strong> <%= message.getContent() %></li>
    <%
      }
    %>
      </ul>
    </div>

    <hr/>

    <% if (request.getAttribute("error") != null) { %>
      <h2 style="color:red"><%= request.getAttribute("error") %></h2>
    <% } %>

<<<<<<< HEAD
    <% if (user != null && conversationUsers.contains(user)) { %>
||||||| merged common ancestors
    <% if (request.getSession().getAttribute("user") != null && conversationUsers.contains(request.getSession().getAttribute("user")) { %>
=======
    <% if (request.getSession().getAttribute("user") != null && conversationUsers.contains(request.getSession()
    .getAttribute("user"))) { %>
>>>>>>> 74ed601bdcd96513b0834584170866cb9823321a
    <form id="chatform" action="/chat/<%= conversation.getTitle() %>" method="POST">
        <textarea name="message"></textarea>
        </br>
        <button type="submit">Send</button>
        </br>
        <button type="submit" name="button" value="leaveButton">Leave Conversation</button>
    </form>
<<<<<<< HEAD
    <% } else if (user != null && !(conversationUsers.contains(user))) { %>
    <p> Join the conversation to send a message! </p>
||||||| merged common ancestors
    <% } %>

    <% if (request.getAttribute("user") != null && !(conversationUsers.contains(request.getSession().getAttribute("user"))) { %>
=======
    <% } %>

    <% if (request.getAttribute("user") != null && !(conversationUsers.contains(request.getSession().getAttribute
    ("user")))){ %>
>>>>>>> 74ed601bdcd96513b0834584170866cb9823321a
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
