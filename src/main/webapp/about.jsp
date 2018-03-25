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
  <title>CodeU Chat App</title>
 <link rel="stylesheet" href="/css/main.css">
  <nav>
   <a id="navTitle" href="/">CodeU Chat App</a>
   <% if(request.getSession().getAttribute("user") != null){ %>
     <a>Hello <%= request.getSession().getAttribute("user") %>!</a>
     <a href="/activityFeed">Activity Feed</a>
     <a href="/conversations">Conversations</a>
      <% String profileaddress = (String) "/profile/" + request.getSession().getAttribute("user"); %>  
     <a href="<%=profileaddress %>">Your Profile</a> 
     <a href="/logout">Logout</a>
   <% } else{ %>
     <a href="/login">Login</a>
     <a href="/register">Register</a>
   <% } %>
   <a href="/about.jsp">About</a>
 </nav>
</head>

</head>
<body>


  <div id="container">
    <div
      style="width:75%; margin-left:auto; margin-right:auto; margin-top: 50px;">

      <h1>About the CodeU Chat App</h1>
      <p>
        This is an example chat application designed to be a starting point
        for your CodeU project team work. Here's some stuff to think about:
      </p>

      <ul>
        <li><strong>Algorithms and data structures:</strong> We've made the app
            and the code as simple as possible. You will have to extend the
            existing data structures to support your enhancements to the app,
            and also make changes for performance and scalability as your app
            increases in complexity.</li>
        <li><strong>Look and feel:</strong> The focus of CodeU is on the Java
          side of things, but if you're particularly interested you might use
          HTML, CSS, and JavaScript to make the chat app prettier.</li>
        <li><strong>Customization:</strong> Think about a group you care about.
          What needs do they have? How could you help? Think about technical
          requirements, privacy concerns, and accessibility and
          internationalization.</li>
      </ul>

      <p>
        This is your code now. Get familiar with it and get comfortable
        working with your team to plan and make changes. Start by updating the
        homepage and this about page to tell your users more about your team.
        This page should also be used to describe the features and improvements
        you've added.
      </p>
    </div>
  </div>

  <div id="container">
    <div style="width:75%; margin-left:auto; margin-right:auto; margin-top: 50px;">

      <h1>About Team 12</h1>

      <p>Welcome to our page!</p>

      <ul>
        <li><strong>Jeremy Archer:</strong> Jeremy is from Chicago, IL and works as a site reliability engineer in  New York.</li>
        <li><strong>Alison Rosenman:</strong> Alison is from Mercer
        Island, WA and is a student at Haverford College.</li>
        <li><strong>Marie Zimmerman:</strong> Marie is from Atlanta,
        Georgia and is a student at Georgia Tech.</li>
        <li><strong>Maria Mahin:</strong> Maria is from Queens, NY and
        is a student at Hunter College.</li>
        <li><strong>Kevin Kane:</strong> Kevin is from Laurel, Maryland
        and is a student at the University of Maryland, College Park.</li>
      </ul>
    </div>
  </div>

</body>
</html>
