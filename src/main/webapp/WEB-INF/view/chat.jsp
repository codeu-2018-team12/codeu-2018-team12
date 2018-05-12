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
<%@ page import="codeu.model.store.basic.ConversationStore" %>
<%@ page import="codeu.model.data.User" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.*" %>
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
    <jsp:include page="./navbar.jsp" />
    <link rel="stylesheet" href="/css/main.css?" type="text/css">
    <link rel="stylesheet" href="/css/chat.css?" type="text/css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <script>
      function scrollChat() {
        var chatDiv = document.getElementById('chat');
        chatDiv.scrollTop = chatDiv.scrollHeight;
      }

      function openNav() {
         document.getElementById("mySidenav").style.width = "250px";
         document.getElementById("main").style.marginLeft = "250px";
      }
      function closeNav() {
        document.getElementById("mySidenav").style.width = "0";
        document.getElementById("main").style.marginLeft= "0";
      }
    </script>
 </head>
 <body onload="scrollChat()">
 <div id="mySidenav" class="sidenav">
 <%List<Conversation> chatConversations = ConversationStore.getInstance().getAllConversations();%>
   <a href="javascript:void(0)" class="closebtn" onclick="closeNav()">&times;</a>
   <div id="conversations">
      <a class="section-title" href="../conversations#group-message">Conversations</a>
      <%
        int counterOne = 0;
        for (int i = 0 ; i < chatConversations.size(); i++) {
          if (counterOne == 8) {
            break;
          }
          if(!chatConversations.get(i).getTitle().startsWith("direct:")) {
       %>
          <div class="conversation-entry">
            <p>
              <% String title = chatConversations.get(i).getTitle(); %>
              <a class="convo-title" href="/chat/<%=title%>" ><%=title%></a>
           </p>
         </div>
       <% counterOne++;
         }
       }%>
   </div>
   <div id="Direct Messages">
   <a class="section-title" href="../conversations#direct-message">Direct Messages</a>
     <% List<Conversation> permittedConversations = ConversationStore.getInstance().getAllPermittedConversations
     (user.getId());
        int counterTwo = 0;
         for (int i = 0 ; i < permittedConversations.size(); i++) {
           if (counterTwo == 5) {
             break;
           }
           if (permittedConversations.get(i).getTitle().startsWith("direct:")) {
             String recipient = null;
             List<UUID> convoUsers = permittedConversations.get(i).getConversationUsers();
              String firstUser = UserStore.getInstance().getUser(convoUsers.get(0)).getName();
              String secondUser = UserStore.getInstance().getUser(convoUsers.get(1)).getName();
           if (!user.getName().equals(firstUser)){
              recipient = firstUser;
           } else {
              recipient = secondUser;
           }
          %>
           <div class="message-entry">
             <p>
               <a class="convo-title" href="/direct/<%=recipient%>"><%=recipient%></a>
            </p>
          </div>
          <% counterTwo++;
            }
          }%>
     </div>
   </div>
 <label onclick="openNav()" class="btn btn-primary">Chat With Other Users
 <span id="glyph" class="glyphicon glyphicon-align-justify"></span></button>
 </label>
 <div id="main">
  <div id="container">
    <% if (request.getAttribute("error") != null) { %>
      <h2 style="color:red"><%= request.getAttribute("error") %></h2>
    <% } %>
     <h1><%= conversation.getTitle() %>
     <a href="" style="float: right">&#8635;</a></h1>
     <div id="chat" class="col-md-8">
        <ul class="chat">
       <%
        if (user != null && conversationUsers.contains(user.getId())) {
          for (Message message : messages) {
            User authorOfMessage = UserStore.getInstance().getUser(message.getAuthorId());
            String author = UserStore.getInstance().getUser(message.getAuthorId()).getName();
            String username = user.getName();
            if (!author.equals(username)) {
            %>
            <li class="left clearfix">
            <span class="chat-img pull-left">
           <% if (UserStore.getInstance().getUser(message.getAuthorId()).getProfilePicture() == null) { %>
              <a href="/profile/<%= author %>"><img class="profile-pic" src="../resources/codeU.png" alt="User
               Avatar"></a>
           <% } else { %>
              <a href="/profile/<%= author %>">
              <img class="profile-pic" src="http://storage.googleapis.com/chatu-196017.appspot.com/<%= authorOfMessage
              .getProfilePicture() %>" alt="User Avatar"></a>
          <% } %>
         </span>
         <div class="chat-body clearfix">
           <div class="header">
             <strong class="primary-font"><a href="/profile/<%= author %>"><%= author %></a></strong>
             <small class="pull-right text-muted"><i class="fa fa-clock-o"></i>
             <% String time = message.getCreationTimeFormatted();%>
             <%= time %></small>
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
             <a href="/profile/<%= author %>"><img class="profile-pic" src="../resources/codeU.png" alt="User
              Avatar"></a>
          <% } else { %>
          <a href="/profile/<%= author %>">
          <img class="profile-pic" src="http://storage.googleapis.com/chatu-196017.appspot.com/<%= authorOfMessage
          .getProfilePicture
          ()%>"
          alt="User Avatar"></a>
         <% } %>
       </span>
       <div class="chat-body clearfix">
         <div class="header">
            <strong class="primary-font"><a href="/profile/<%= author %>"><%= author %></a></strong>
             <small class="pull-right text-muted"><i class="fa fa-clock-o"></i>
             <% String time = message.getCreationTimeFormatted();%>
              <%= time %></small>
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
       }
      }
     %>
     <% if (user != null && !conversationUsers.contains(user.getId())) { %>
       <h2 id="join-conversation"> Messages from this conversation will appear here! </h2>
       <p> To see these messages, you must first join the conversation. </p>
     <% } %>
   </div>
    <% if (user != null && conversationUsers.contains(user.getId())) { %>
    <form id="chatForm" action="/chat/<%= conversation.getTitle() %>" method="POST" enctype="multipart/form-data">
        <textarea placeholder="Enter your message here" data-gramm_editor="false" name="message"></textarea></br>
        <label class="btn btn-info image">
           <span class="glyphicon glyphicon-camera"></span>  Upload Photo
           <input type="file" id="image" onchange="chatForm.submit()"  name="image" accept="image/*" hidden>
        </label>
        <button type="submit" class="btn btn-info" name="submitText" value="submitText"> Submit</button>
        <button type="submit" class="btn btn-info" name="button" value="leaveButton"> Leave Conversation</button>
    </form>
    <br>
          <div id="search-container">
             <form action="/search" id="search" class="form-inline" method="GET">
                <input type="search" class="form-control mr-sm-2" placeholder="Find a message" name="searchmessage"
                 id="searchmessage">
                  <button type="submit" class="btn btn-info" name="searchbutton" value="<%= conversation.getTitle()
                  %>">Search</button>
              </form>
           </div>
    <% } else if (user != null && !(conversationUsers.contains(user.getId()))) { %>
         <p> Join the conversation to send a message! </p>
         <form id="chatform" style="margin-left: 0px;" action="/chat/<%= conversation.getTitle() %>" method="POST"
         enctype='multipart/form-data'>
            <button type="submit" name="button" class="btn btn-info" value="joinButton">Join Conversation</button>
         </form>
    <% } else { %>
       <p><a href="/login">Login</a> to send a message.</p>
    <% } %>
  </div>
</body>
</html>

