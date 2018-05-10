<%@ page import="java.util.List" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="java.time.*" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
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
    <jsp:include page="./navbar.jsp" />
    <link rel="stylesheet" href="/css/main.css" type="text/css">
    <link rel="stylesheet" href="/css/chat.css" type="text/css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
    <script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
    <script>
      function scrollChat() {
        var chatDiv = document.getElementById('chat');
        chatDiv.scrollTop = chatDiv.scrollHeight;
      };
    </script>
 </head>
 <body onload="scrollChat()">

  <div id="container">
    <% String loggedInName = loggedInUser.getName();
       String otherName = otherUser.getName();%>
    <h1> <%= loggedInName + " and " + otherName %>
    <a href="" style="float: right">&#8635;</a></h1>
    <div id="chat" class="col-md-8">
      <ul class="chat">
       <%
         int boxNum = 1;
         for (Message message : messages) {
            String author = UserStore.getInstance()
            .getUser(message.getAuthorId()).getName();
            if (boxNum % 2 == 0) {
          %>
           <li class="left clearfix">
            <span class="chat-img pull-left">
               <% User messageUser = UserStore.getInstance().getUser(message.getAuthorId());
               if (messageUser.getProfilePicture() == null) { %>
                 <a href="/profile/<%= author %>"><img class="profile-pic" src="../resources/codeU.png" alt="User
                 Avatar"></a>
              <% } else { %>
                 <a href="/profile/<%= author %>">
                 <img "profile-pic" src="http://storage.googleapis.com/chatu-196017.appspot.com/<%= messageUser.getProfilePicture()%>"
                 alt="User Avatar"></a>
              <% } %>
              </span>
              <div class="chat-body clearfix">
                <div class="header">
                  <strong class="primary-font"><a href="/profile/<%= author %>"><%= author %></a></strong>
                  <small class="pull-right text-muted"><i class="fa fa-clock-o"></i>
                  <%
                    Instant creationTime = message.getCreationTime();
                    LocalDateTime ldt = LocalDateTime.ofInstant(creationTime, ZoneId.systemDefault());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy h:mm:ss a");
                    String time = ldt.format(formatter);
                   %> <%= time %></small>
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
              <% User messageUser = UserStore.getInstance().getUser(message.getAuthorId());
              if (UserStore.getInstance().getUser(message.getAuthorId()).getProfilePicture() == null) { %>
                <a href="/profile/<%= author %>"><img class="profile-pic" src="../resources/codeU.png" alt="User Avatar"></a>
              <% } else { %>
                <a href="/profile/<%= author %>">
                <img "profile-pic" src="http://storage.googleapis.com/chatu-196017.appspot.com/<%= messageUser.getProfilePicture()%>"
                alt="User Avatar"></a>
             <% } %>
               </span>
               <div class="chat-body clearfix">
                 <div class="header">
                   <strong class="primary-font"><a href="/profile/<%= author %>"><%= author %></a></strong>
                   <small class="pull-right text-muted"><i class="fa fa-clock-o"></i>
                   <%
                     Instant creationTime = message.getCreationTime();
                     LocalDateTime ldt = LocalDateTime.ofInstant(creationTime, ZoneId.systemDefault());
                     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy h:mm:ss a");
                     String time = ldt.format(formatter);
                   %> <%= time %></small>
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
           %>
       </ul>
    </div>

    <% if (request.getAttribute("error") != null) { %>
      <h2 style="color:red"><%= request.getAttribute("error") %></h2>
    <% } %>

   <%if (loggedInUser != null) {%>
      <form id="chatForm" action="/direct/<%= otherUser.getName() %>" method="POST" enctype="multipart/form-data">
         <textarea placeholder="Enter your message here" data-gramm_editor="false" name="message"></textarea></br>
         <label class="btn btn-info image">
           <span class="glyphicon glyphicon-camera"></span>  Upload Photo
           <input type="file" id="image" onchange="chatForm.submit()"  name="image" accept="image/*" hidden>
         </label>
        <button type="submit" class="btn btn-info" name="submitText" value="submitText"> Submit</button>
     </form>
    <% } else { %>
      <p><a href="/login">Login</a> to send a message.</p>
    <% } %>
  </div>
 </body>
</html>

