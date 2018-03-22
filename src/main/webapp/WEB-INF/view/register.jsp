<!DOCTYPE html>
<html>
<head>
  <title>Register</title>
  <link rel="stylesheet" href="/css/main.css">
  <style>
    label {
      display: inline-block;
      width: 100px;
    }
  </style>
</head>
<body>
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
 <div id="search-container" style="padding-left:16px;padding-bottom:20px">
   <form action="/search" method="GET">
     <input type="text" placeholder="Search for a user.." name="search" id="search">
     <button type="submit">Search</button>
   </form>
 </div>
</nav>
  <div id="container">
  	<h1>Register</h1>

    <% if(request.getAttribute("error") != null){ %>
       <h2 style="color:red"><%= request.getAttribute("error") %></h2>
    <% } %>

  	<form action="/register" method="POST">
      <label for="username">Username: </label>
      <input type="text" name="username" id="username">
      <br/>
      <label for="password">Password: </label>
      <input type="password" name="password" id="password">
      <br/>
      <label for="confirmPassword">Confirm Password: </label>
      <input type="password" name="confirmPassword" id="confirmPassword">
      <br/>
      <br/><br/>
      <button type="submit">Submit</button>
   	</form>
  </div>
</body>
</html>
