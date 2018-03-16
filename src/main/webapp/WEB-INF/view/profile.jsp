<%@ page import="java.util.List" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.store.basic.ConversationStore" %>
<%
Conversation conversation = (Conversation) request.getAttribute("conversation");
List<Message> messages = (List<Message>) request.getAttribute("messages");
User user = (User) request.getAttribute("user");
%>

<!DOCTYPE html>
<html>
<head>
  <title><%= user.getName() %></title>
  <link rel="stylesheet" href="/css/main.css" type="text/css">
  <nav>
   <a id="navTitle" href="/">CodeU Chat App</a>
   <a href="/conversations">Conversations</a>
   <% if (request.getSession().getAttribute("user") != null) { %>
     <a>Hello <%= request.getSession().getAttribute("user") %>!</a>
   <% } else { %>
     <a href="/login">Login</a>
     <a href="/register">Register</a>
   <% } %>
   <a href="/about.jsp">About</a>
 </nav>
</head>
<body>
 <div id="messages">
   <ul>
 <%
   for (Message message : messages) {
     String conversationTitle = ConversationStore.getInstance().getConversationWithId(message.getConversationId()).getTitle();
 %>
   <li><strong><%= conversationTitle %>:</strong> <%= message.getContent() %></li>
 <%
   }
 %>
   </ul>
 </div>
</body>
</html>
