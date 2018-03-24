<%@ page import="java.util.List" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.store.basic.ConversationStore" %>
<%@ page import="java.time.*" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
List<Message> messages = (List<Message>) request.getAttribute("messages");
User user = (User) request.getAttribute("user");
%>

<!DOCTYPE html>
<html>
<head>
  <% if (user != null) {%>
    <title><%= user.getName() %></title>
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
     #messages {
       background-color: white;
       height: 500px;
       overflow-y: scroll
     }
   </style>

   <script>
     // scroll the chat div to the bottom
     function scrollChat() {
       var msgDiv = document.getElementById('messages');
     };
   </script>
  </head>
  <body onload="scrollChat()">
  <h1 align ="center"><%= user.getName() %>'s Profile</h1>
  <div id="container">
  <h2>Biography</h2>
    <% if (user.getBio() != null) { 
         user.getBio(); %>
     } else {%>
        <p> This user has not yet set up their biography! </p>
     <% } %>
    <% if (request.getAttribute("user") == user) { //Right now, this condition executes even if not logged in!
     %>
     <p> You can change your biography below: </p>
     <form action='' user method="POST">
       <label for="newBio">New Bio: </label>
       <input type="text" name="newBio" id="newBio">
       <button type="submit">Submit</button> 
  </form>
  <% } %>
    <br>
    <br>
  
  </div>
  <div id="container">
   <h2>Sent Messages</h2>
   <div id="messages">
     <ul>
   <%
     for (Message message : messages) {
       String conversationTitle = ConversationStore.getInstance()
              .getConversationWithId(message.getConversationId())
              .getTitle();
       Instant instant =  ConversationStore.getInstance()
              .getConversationWithId(message.getConversationId())
              .getCreationTime();
       LocalDateTime ldt =
              LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
       DateTimeFormatter formatter =
              DateTimeFormatter.ofPattern("MM/dd/yy h:mm:ss a");
       String time = ldt.format(formatter);
   %>
     <li>
     <strong>At <%= time + " in " + conversationTitle %>:</strong>
     <%= message.getContent() %>
    </li>
   <%
     }
   %>
     </ul>
   </div>
  </div>
<% } else { %>
  <title>Profile Not Found</title>
  <link rel="stylesheet" href="/css/main.css" type="text/css">
  <nav>
   <a id="navTitle" href="/">CodeU Chat App</a>
   <% if (request.getSession().getAttribute("user") != null) { %>
     <a>Hello <%= request.getSession().getAttribute("user") %>!</a>
   <% } else { %>
     <a href="/login">Login</a>
     <a href="/register">Register</a>
   <% } %>
   <a href="/about.jsp">About</a>
  </nav>
  <h1 align="center">Profile Not Found</h1>
<% } %>
</body>
</html>
