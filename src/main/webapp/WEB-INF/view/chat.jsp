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
    <link rel="stylesheet" href="/css/main.css?esens1" type="text/css">
    <link rel="stylesheet" href="/css/chat.css?2seesn" type="text/css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
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
    <h1><%= conversation.getTitle() %>
    <a href="" style="float: right">&#8635;</a></h1>
    <div id="chat" class="col-md-8">
      <ul class="chat">
      <%
      if (user != null && conversationUsers.contains(user.getId())) {
        int boxNum = 1;
        for (Message message : messages) {
          String author = UserStore.getInstance().getUser(message.getAuthorId()).getName();
          if (boxNum % 2 == 0) {
      %>
      <li class="left clearfix">
        <span class="chat-img pull-left">
        <% if (UserStore.getInstance().getUser(message.getAuthorId()).getProfilePicture() == null) { %>
          <a href="/profile/<%= author %>"><img class="profile-pic" src="../resources/codeU.png" alt="User Avatar"></a>
        <% } else { %>
           <a href="/profile/<%= author %>">
           <img "profile-pic" src="http://storage.googleapis.com/chatu-196017.appspot.com/<%= user.getProfilePicture()
           %>" alt="User
           Avatar"></a>
        <% } %>
        </span>
        <div class="chat-body clearfix">
          <div class="header">
            <strong class="primary-font"><a href="/profile/<%= author %>"><%= author %></a></strong>
            <small class="pull-right text-muted"><i class="fa fa-clock-o"></i> 12 mins ago</small>
          </div>
          <% if (!message.containsImage()){%>
            <p><%= message.getContent()%></p>
          <% } else { %>
             <img class="chat-image" src="http://storage.googleapis.com/chatu-196017.appspot.com/<%=message.getContent()%>"
          <% } %>
       </div>
      </li>
      <%
        } else {
      %>
      <li class="right clearfix">
      <span class="chat-img pull-right">
        <% if (UserStore.getInstance().getUser(message.getAuthorId()).getProfilePicture() == null) { %>
          <a href="/profile/<%= author %>"><img class="profile-pic" src="../resources/codeU.png" alt="User Avatar"></a>
        <% } else { %>
          <a href="/profile/<%= author %>">
          <img "profile-pic" src="http://storage.googleapis.com/chatu-196017.appspot.com/<%= user.getProfilePicture()
          %>"
          alt="User Avatar"></a>
       <% } %>
         </span>
         <div class="chat-body clearfix">
           <div class="header">
             <strong class="primary-font"><a href="/profile/<%= author %>"><%= author %></a></strong>
             <small class="pull-right text-muted"><i class="fa fa-clock-o"></i> 13 mins ago</small>
           </div>
             <% if (!message.containsImage()){%>
                <p> <%= message.getContent()%> </p>
             <% } else { %>
                <img class="chat-image" src="http://storage.googleapis.com/chatu-196017.appspot.com/<%=message.getContent()%>"
             <% } %>
         </div>
       </li>
      <%
        }
        boxNum++;
       }
      }
     %>
     <% if (user != null && !conversationUsers.contains(user.getId())) { %>
       <h2> Messages from this conversation will appear here! </h2>
       <p> To see these messages, you must first join the conversation. </p>
     <% } %>
   </div>
    <hr/>

    <% if (request.getAttribute("error") != null) { %>
      <h2 style="color:red"><%= request.getAttribute("error") %></h2>
    <% } %>

    <% if (user != null && conversationUsers.contains(user.getId())) { %>
    <form id="chatForm" action="/chat/<%= conversation.getTitle() %>" method="POST" enctype="multipart/form-data">
        <textarea placeholder="Enter your message here" name="message"></textarea></br>
        <label class="btn btn-info image">
          <span class="glyphicon glyphicon-camera"></span>  Upload Photo
          <input type="file" id="image" onchange="chatForm.submit()"  name="image" accept="image/*" hidden>
        </label>
        <button type="submit" class="btn btn-info" name="submitText" value="submitText"> Submit</button>
        <button type="submit" class="btn btn-info" name="button" value="leaveButton"> Leave Conversation</button>
    </form>
    <% } else if (user != null && !(conversationUsers.contains(user.getId()))) { %>
       <p> Join the conversation to send a message! </p>
       <form id="chatform" action="/chat/<%= conversation.getTitle() %>" method="POST" enctype='multipart/form-data'>
          <button type="submit" name="button" value="joinButton">Join Conversation</button>
       </form>
    <% } else { %>
      <p><a href="/login">Login</a> to send a message.</p>
    <% } %>

    <hr/>
  </div>
</body>
</html>
