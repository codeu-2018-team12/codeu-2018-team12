<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.data.User" %>
<html>
<body>
  <style>
    #search-container {
      padding-left:16px;
      padding-bottom:20px
    }
  </style>
  <nav>
    <a id="navTitle" href="/">CodeU Chat App</a>
    <% if (request.getSession().getAttribute("user") != null) { %>
      <a>Hello <%= request.getSession().getAttribute("user") %>!</a>
      <a href="/activityFeed">Activity Feed</a>
      <a href="/conversations">Conversations</a>
      <% String profileaddress = (String) "/profile/" + request.getSession().getAttribute("user"); %>  
     <a href="<%=profileaddress %>">Your Profile</a> 
      <a href="/logout">Logout</a>
    <% } else { %>
      <a href="/login">Login</a>
      <a href="/register">Register</a>
    <% } %>
    <a href="/about.jsp">About</a>
    <div id="search-container">
      <form action="/search" method="GET">
        <input type="text" list="autocomplete" placeholder="Search for a user.." name="search" id="search">
        <datalist id="autocomplete">
        <% for (User user : UserStore.getInstance().getUsers()) { %>
          <option value="<%= user.getName() %>">
        <% } %>
        </datalist>
        <button type="submit">Search</button>
      </form>
    </div>
  </nav>
</body>
</html>
