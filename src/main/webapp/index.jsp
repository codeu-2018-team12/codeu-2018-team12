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
<!DOCTYPE html>
<html>
<head>
 <link rel="stylesheet" href="/css/main.css">
  <nav>
   <a id="navTitle" href="/">CodeU Chat App</a>
   <% if(request.getSession().getAttribute("user") != null){ %>
     <a>Hello <%= request.getSession().getAttribute("user") %>!</a>
     <a href="/activityFeed">Activity Feed</a>
     <a href="/conversations">Conversations</a>
     <a href="/logout">Logout</a>
   <% } else{ %>
     <a href="/login">Login</a>
     <a href="/register">Register</a>
   <% } %>
   <a href="/about.jsp">About</a>
 </nav>
</head>


<body>


  <div id="container">
    <div
      style="width:75%; margin-left:auto; margin-right:auto; margin-top: 50px;">

      <h1>CodeU Chat App</h1>
      <img src="/resources/codeU.png">
      <h2>Welcome to Team 12's CodeU 2018 Project!</h2>

      <ul>
        <li><a href="/login">Login</a> to get started.</li>
        <li>Go to the <a href="/conversations">conversations</a> page to
            create or join a conversation.</li>
        <li>View the <a href="/about.jsp">about</a> page to learn more about us
            and the project.</li>
        <li>You can <a href="/testdata">load test data</a> to fill the site with
            example data.</li>
        <li>You can view the project's GitHub page
            <a href="https://github.com/codeu-2018-team12/codeu-2018-team12">
              here</a>.</li>
      </ul>
    </div>
  </div>
</body>
</html>
