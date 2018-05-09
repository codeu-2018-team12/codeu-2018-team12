<!DOCTYPE html>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.data.User" %>
<html>
  <head>
     <title>Register</title>
     <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet"
      id="bootstrap-css">
     <link rel="stylesheet" href="/css/main.css?DwvEeedsRFedreVeeedE1e" type="text/css">
     <link rel="stylesheet" href="/css/register.css?3ewerfdVeSeeFSeReddD1" type="text/css">
     <jsp:include page="./navbar.jsp" />
     <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
     <script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
  </head>
  <body>
     <div class="container">
        <% if(request.getAttribute("error") != null){ %>
          <div class="alert alert-warning">
             <strong>Error: Invalid username or password</strong>
          </div>
         <% } %>
        <div class="card card-container">
            <img id="profile-img" class="profile-img-card" src="../resources/codeU.png" />
            <p id="profile-name" class="profile-name-card"></p>
            <form action="/register" class="form-register" method="POST">
                <input type="text" id="inputUsername" class="form-control" name="username" placeholder="Username"
                required autofocus> <br/>
                <input type="password" id="inputPassword" class="form-control" name="password" placeholder="Password"
                required> <br/>
                <input type="password" id="inputPassword" class="form-control" name="confirmPassword"
                placeholder="Confirm Password" required> <br/>
                <input type="text" id="inputEmail" class="form-control" name="email" placeholder="Email"
                required autofocus> <br/>
                <button class="btn btn-lg btn-primary btn-block btn-register" type="submit">Register</button>
            </form>
            <a href="/login"> Have an account? </a>
         </div>
      </div>
  </body>
</html>
