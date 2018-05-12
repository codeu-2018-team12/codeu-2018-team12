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
<%@ page import="java.util.UUID" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.store.basic.ConversationStore" %>
<%@ page import="codeu.model.store.basic.MessageStore" %>
<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.data.Message" %>

<!DOCTYPE html>
<html>
  <head>
    <title>Conversations</title>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet"
     id="bootstrap-css">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.0.13/css/all.css"
     integrity="sha384-DNOHZ68U8hZfKXOrtjWvjxusGo9WQnrNx2sqG0tfsghAvtVlRW3tvkXWZh58N9jp" crossorigin="anonymous">
     <jsp:include page="./navbar.jsp" />
    <link rel="stylesheet" href="/css/main.css" type="text/css">
    <link rel="stylesheet" href="/css/navbar.css" type="text/css">
    <link rel="stylesheet" href="/css/conversations.css" type="text/css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
    <script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
    <script>
      function scrollBox() {
         var activityDiv = document.getElementsByClassName('conversation');
      };
   </script>
  </head>
  <body onload="scrollBox()">
   <div id="container">
    <% if(request.getAttribute("error") != null){ %>
       <h2 style="color:red"><%= request.getAttribute("error") %></h2>
    <% } %>
    <div class="table-top"><div id="section-title"><i class="far fa-comments fa-lg"></i>Conversations
    </div>
    </div>
    <div class="conversation" id="group-message">
    <%
    List<Conversation> conversations =
      (List<Conversation>) request.getAttribute("conversations");
      ConversationStore.getInstance().sort(conversations);
    List<Conversation> directMessages = (List<Conversation>) request.getAttribute("directMessages");
    ConversationStore.getInstance().sort(directMessages);
    if(conversations == null || conversations.isEmpty()){
    %>
      <p>Create a conversation to get started.</p>
    <% } else{ %>
     <table class="table table-hover">
     <tbody>
       <thead>
         <tr>
           <th scope="col">Conversation Name</th>
           <th scope="col">Conversation Creator</th>
           <th scope="col">Last Active</th>
         </tr>
       </thead>
    <% for(Conversation conversation : conversations){
         String time = null;
         UUID conversationId = conversation.getId();
         List <Message> messageList = MessageStore.getInstance().getMessagesInConversation(conversationId);
         if (!messageList.isEmpty()) {
           Message latestMessage = MessageStore.getInstance().sort(messageList).get(messageList.size() - 1);
           time = latestMessage.getCreationTimeFormatted();
         } else {
          time = conversation.getCreationTimeFormatted();
         }
         %>
    <tr onclick="window.location='/chat/<%= conversation.getTitle() %>';">
      <th scope="row"><%= conversation.getTitle() %></a></th>
      <% String userName = UserStore.getInstance().getUser(conversation.getOwnerId()).getName(); %>
       <td><%= userName %></td>
       <td><%=time%></td>
     </tr>
    <% } %>
   </tbody>
  </table>
  <% } %>
  </div>
  <% if(request.getSession().getAttribute("user") != null){ %>
   <form action="/conversations" class="form-inline" method="POST">
     <div class="form-group">
        <label class="form-control-label" >New Conversation:</label>
        <input type="text" class="form-control mr-sm-2" name="conversationTitle"
        placeholder="Conversation Name">
        <button class="btn btn-info" type="submit">Create</button>
     </div>
   </form>
   <br>
       <div>
         <form action="/search" id="search" class="form-inline" method="GET">
         <label class="form-control-label" >Search:</label>
           <input type="search" class="form-control mr-sm-2" placeholder="Find a conversation..." name="searchconvo" id="searchconvo">
           <button type="submit" class="btn btn-info">Search</button>
         </form>
       </div>
 <% } %>
  <hr/>
  <% if(directMessages != null && !directMessages.isEmpty()){ %>
    <div class="table-top"><div id="section-title"><i class="far fa-comments fa-lg"></i>Direct Messages
        </div>
       </div>
        <div class="conversation" id="direct-message">
         <table class="table table-hover">
           <tbody>
             <thead>
               <tr>
                 <th scope="col">Recipient</th>
                 <th scope="col">Message Creator</th>
                 <th scope="col">Last Active</th>
               </tr>
             </thead>
             <%
               for(Conversation conversation : directMessages){
                 User otherUser = null;
                 User loggedInUser = UserStore.getInstance()
                 .getUser((String) request.getSession().getAttribute("user"));
                 for(UUID id : conversation.getConversationUsers()){
                   if(!id.equals(loggedInUser.getId())){
                      otherUser = UserStore.getInstance().getUser(id);
                      break;
                  }
               }
               String time = null;
                 UUID conversationId = conversation.getId();
                  List <Message> messageList = MessageStore.getInstance().getMessagesInConversation(conversationId);
                  if (!messageList.isEmpty()){
                     Message latestMessage = MessageStore.getInstance().sort(messageList).get(messageList.size() - 1);
                     time = latestMessage.getCreationTimeFormatted();
                  } else {
                     time = conversation.getCreationTimeFormatted();
                  }
                 %>
                  <tr onclick="window.location='/direct/<%= otherUser.getName()%>';">
                    <th scope="row"><%= otherUser.getName() %></a></th>
                    <% String userName = UserStore.getInstance().getUser(conversation.getOwnerId()).getName(); %>
                    <td><%= userName %></td>
                    <td><%=time%></td>
                 </tr>
              <% } %>
             </ul>
         <% } %>
       </div>
    </tbody>
    </table>
  </div>
 </body>
</html>
