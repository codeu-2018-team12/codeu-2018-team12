<%@ page import="java.util.List" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.store.basic.MessageStore" %>
<%@ page import="codeu.model.store.basic.ConversationStore" %>
<%@ page import="java.time.*" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.UUID" %>

<%
List<Message> messages = (List<Message>) request.getAttribute("messages");
%>

<!DOCTYPE html>
<html>
<head>
  <title>Activity</title>
  <link rel="stylesheet" href="/css/main.css">
   <nav>
     <a id="navTitle" href="/">CodeU Chat App</a>
     <% if(request.getSession().getAttribute("user") != null){ %>
       <a>Hello <%= request.getSession().getAttribute("user") %>!</a>
       <a href="/activityFeed">Activity Feed</a>
     <% } else{ %>
       <a href="/login">Login</a>
       <a href="/register">Register</a>
     <% } %>
     <a href="/conversations">Conversations</a>
     <a href="/about.jsp">About</a>
   </nav>
  <style>
    #activity {
      background-color: white;
      height: 500px;
      overflow-y: scroll
    }
  </style>

  <script>
    function scrollBox() {
      var activityDiv = document.getElementById('activity');
        activityDiv.scrollTop = activityDiv.scrollHeight;
    };
  </script>
</head>
<body onload="scrollBox()">
  <div id="container">
  	<h1>Activity</h1>
  	<p>Here&#39s everything that happened on the site so far!</p>
    <div id="activity">
      <ul>
        <%
          for (Message message : messages) {
            UUID userId = message.getAuthorId();
            String userName = UserStore.getInstance().getUser(userId).getName();
            String content = message.getContent();
            UUID conversationId = message.getConversationId();
            String conversationName = ConversationStore.getInstance().getConversationWithId(conversationId).getTitle();

            Instant creationTime = message.getCreationTime();
            LocalDateTime ldt = LocalDateTime.ofInstant(creationTime, ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy h:mm:ss a");
            String time = ldt.format(formatter);
         %>
         <li>
           <strong><%= time %>:</strong>
              <%= userName + " sent a message to " + conversationName + ": " %>
              <q><%= content %></q>
         </li>
         <%
          }
         %>
      </ul>
    </div>
  </div>
</body>
</html>