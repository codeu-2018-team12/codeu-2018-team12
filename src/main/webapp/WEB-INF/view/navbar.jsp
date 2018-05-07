<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.data.User" %>
<html>
<body>
  <style>
    #search-container {
      padding-left:16px;
      padding-bottom:20px
    }
    #logo {
       width: 40px;
       margin: 10px;
       margin-top: 2px;
    }
  </style>
  <nav>
    <a id="navTitle" href="/"><img id="logo" src="../resources/chat-icon.png">CodeU Chat App</a>
    <% if (request.getSession().getAttribute("user") != null) { %>
      <a href="/activityFeed">Activity Feed</a>
      <a href="/conversations">Conversations</a>
      <% String profileaddress = (String) "/profile/" + request.getSession().getAttribute("user"); %>
     <a href="<%=profileaddress %>">Your Profile</a>
      <a href="/settings">Settings</a>
      <a href="/logout">Logout</a>
    <% } else { %>
      <a href="/login">Login</a>
      <a href="/register">Register</a>
    <% } %>
    <a href="/about.jsp">About</a>
    <div id="search-container">
      <form action="/search" id="search" class="form-inline" method="GET">
        <input class="form-control mr-sm-2" type="search" placeholder="Search" name="search" aria-label="Search">
        <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
      </form>
    </div>
  </nav>
</body>
</html>
