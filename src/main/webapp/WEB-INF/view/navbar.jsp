<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.data.User" %>
<!DOCTYPE html>
<html>
 <head>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet"
     id="bootstrap-css">
    <link rel="stylesheet" href="/css/main.css">
    <link rel="stylesheet" href="/css/navbar.css">
 </head>
 <body>
  <nav>
    <a id="navTitle" href="/"><img id="logo" src="../resources/Chat-icon.png">CodeU Chat App</a>
    <% if (request.getSession().getAttribute("user") != null) { %>
      <a href="/activityFeed">Activity Feed</a>
      <a href="/conversations">Conversations</a>
      <% String profileaddress = (String) "/profile/" + request.getSession().getAttribute("user"); %>
     <a href="<%=profileaddress %>">Your Profile</a>
      <a href="/settings">Settings</a>
      <a href="/about.jsp">About</a>
      <a href="/logout">Logout</a>
    <% } else { %>
      <a href="/login">Login</a>
      <a href="/register">Register</a>
      <a href="/about.jsp">About</a>
    <% } %>
    <div id="search-container">
      <form action="/search" id="search" class="form-inline" method="GET">
        <input type="search" class="form-control mr-sm-2" list="autocomplete" placeholder="Enter a user..."
        name="searchuser" id="searchuser" aria-label="Search">
        <datalist id="autocomplete">
        <% for (User user : UserStore.getInstance().getUsers()) { %>
          <option value="<%= user.getName() %>">
        <% } %>
        </datalist>
        <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
      </form>
    </div>
  </nav>
 </body>
</html>
