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
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.data.User" %>
<!DOCTYPE html>
<html>
<head>
  <title>Login</title>
 <link rel="stylesheet" href="/css/main.css">
 <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
 <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
   <link rel="stylesheet" href="/css/main.css?DwvEcerrgedrdrdEeE1e" type="text/css">
   <link rel="stylesheet" href="/css/login.css?3eweedserhrdrfrdeSSeeddD1" type="text/css">
 <script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>

 <jsp:include page="./navbar.jsp" />
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
            <form action="/login" class="form-signin" method="POST">
                <input type="text" id="inputUsername" class="form-control" name="username" placeholder="Username"
                required
                autofocus> <br/>
                <input type="password" id="inputPassword" class="form-control" name="password" placeholder="Password"
                required> <br/>
                <button class="btn btn-lg btn-primary btn-block btn-signin" type="submit">Sign in</button>
            </form>
            <a href="/register"> Need an account? </a>
        </div>
    </div>
</body>
</html>

