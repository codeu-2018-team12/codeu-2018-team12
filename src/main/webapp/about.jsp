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
 <title>CodeU Chat App</title>
 <link rel="stylesheet" href="/css/main.css">
 <jsp:include page="/WEB-INF/view/navbar.jsp" />
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

      <h1>Info</h1>

      <h2>Search</h2>
      <p>
        You can search for conversations and messages using a variety of filters (listed below).
        You can even combine these filters using AND, OR, and parentheses for priority! For example,
        if you wanted to search for all messages before 5-11-2018 by user_one or user_two, you could enter
        "before:5-11-2018 AND (by:user_one OR by:user_two)". Or, if you wanted to search for all conversations
        that user_one is a member of that also have "convo" in their name, you could enter "with:user_one AND convo".
      </p>

      <h3>Conversation Filters</h3>
      <ul>
        <li><strong>before:</strong>MM-dd-YYYY - finds all conversations created before the given day</li>
        <li><strong>on:</strong>MM-dd-YYYY - finds all conversations created on the given day</li>
        <li><strong>after:</strong>MM-dd-YYYY - finds all conversations created after the given day</li>
        <li><strong>with:</strong>username - finds all conversations the given user has joined</li>
        <li>If none of these filters are given, the app will find any conversations whose title contains the given string</li>
      </ul>

      <h3>Message Filters</h3>
      <ul>
        <li><strong>before:</strong>MM-dd-YYYY - finds all messages in the current conversation posted before the given day</li>
        <li><strong>on:</strong>MM-dd-YYYY - finds all messages in the current conversation posted on the given day</li>
        <li><strong>after:</strong>MM-dd-YYYY - finds all messages in the current conversation posted after the given day</li>
        <li><strong>with:</strong>username - finds all messages in the current conversation posted by the given user</li>
        <li>If none of these filters are given, the app will find any messages whose content contains the given string</li>
      </ul>

      <h2>Markdown</h2>
      <p>
        Messages posted to both the chat and direct message pages support formatting using Markdown. For more information on the
        options supported by Markdown, visit <a href="http://commonmark.org/help/">http://commonmark.org/help/</a>
      </p>

      <h2>Emojis</h2>
      <p>
        Messages posted to both the chat and direct messages pages support the use of Emojis. For more information on the
        supported emojis, visit <a href="https://github.com/vdurmont/emoji-java">https://github.com/vdurmont/emoji-java</a>
    </div>
  </div>

</body>
</html>
