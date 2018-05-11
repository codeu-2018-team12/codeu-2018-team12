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
<%@ page import="codeu.model.data.User" %>

<!DOCTYPE html>
<html>
  <head>
    <title>Conversations</title>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet"
     id="bootstrap-css">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.0.13/css/all.css"
     integrity="sha384-DNOHZ68U8hZfKXOrtjWvjxusGo9WQnrNx2sqG0tfsghAvtVlRW3tvkXWZh58N9jp" crossorigin="anonymous">
     <jsp:include page="./navbar.jsp" />
    <link rel="stylesheet" href="/css/main.css?1ddfddddedffssdcscd2" type="text/css">
    <link rel="stylesheet" href="/css/conversations.css?f1ddcddcssdsfddfed2" type="text/css">
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
    <div class="conversation">
    <%
    List<Conversation> conversations =
      (List<Conversation>) request.getAttribute("conversations");
    List<Conversation> directMessages = (List<Conversation>) request.getAttribute("directMessages");
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
           <th scope="col">Last Activity</th>
         </tr>
       </thead>
    <% for(Conversation conversation : conversations){ %>
    <tr onclick="window.location='/chat/<%= conversation.getTitle() %>';">
      <th scope="row"><%= conversation.getTitle() %></a></th>
      <% String userName = UserStore.getInstance().getUser(conversation.getOwnerId()).getName(); %>
       <td><%= userName %></td>
       <td>Otto</td>
     </tr>
    <% } %>
    </tbody>
    </table>
    <% } %>
    </div>
            <% if(request.getSession().getAttribute("user") != null){ %>
              <form action="/conversations" method="POST">
                 <div class="form-group">
                    <label class="form-control-label">New Conversation:</label>
                      <input type="text" name="conversationTitle">
                      <button class="btn btn-info" type="submit">Create</button>
                 </div>
              </form>
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
                   <th scope="col">Message Name</th>
                   <th scope="col">Message Creator</th>
                   <th scope="col">Last Activity</th>
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
      %>
          <tr onclick="window.location='/direct/<%= otherUser.getName()%>';">
            <th scope="row"><%= otherUser.getName() %></a></th>
            <% String userName = UserStore.getInstance().getUser(conversation.getOwnerId()).getName(); %>
             <td><%= userName %></td>
             <td>Otto</td>
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
