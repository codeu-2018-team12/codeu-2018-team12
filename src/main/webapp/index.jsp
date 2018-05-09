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

<%@ page import="codeu.model.data.User" %>


<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.data.User" %>

<!DOCTYPE html>
<html>
 <head>
   <title>CodeU Chat App</title>
   <meta charset="UTF-8">
   <meta name="viewport" content="width=device-width, initial-scale=1">
   <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet"
    id="bootstrap-css">
   <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
   <link rel="stylesheet" href="/css/main.css?34dddRd">
   <link rel="stylesheet" href="/css/index.css?2Rddd3d">
   <jsp:include page="/WEB-INF/view/navbar.jsp" />
   <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Raleway">
   <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
   <script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
 <body>
   <div class="bgimg w3-display-container w3-animate-opacity w3-text-white">
     <div class="w3-display-middle">
       <h1 id="title-homepage" class="w3-animate-top">Team 12's Chat App</h1>
         <div id="aboutButton">
           <a href="/about.jsp" class="btn btn-primary btn-round-md btn-md">Explore This Project</a>
        </div>
       </div>
    </div>
  </body>
</html>