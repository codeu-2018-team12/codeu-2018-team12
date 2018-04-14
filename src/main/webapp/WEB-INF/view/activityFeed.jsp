<%@ page import="java.util.List" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.data.Activity" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.store.basic.MessageStore" %>
<%@ page import="codeu.model.store.basic.ConversationStore" %>
<%@ page import="java.time.*" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.UUID" %>

<%
List<Activity> activities = (List<Activity>) request.getAttribute("activities");
%>

<!DOCTYPE html>
<html>
<head>
  <title>Activity</title>
  <link rel="stylesheet" href="/css/main.css">
  <jsp:include page="./navbar.jsp" />
  <style>
    #activity {
      background-color: white;
      height: 500px;
      overflow-y: scroll
    }
  </style>

  <script>
    function scrollBox() {
      var activityDiv = document.getElementById('activity');
    };
  </script>
</head>
<body onload="scrollBox()">
  <div id="container">
  	<h1 id="title">Activity</h1>
  	<p>Here&#39s everything that happened on the site so far!</p>
    <div id="activity">
      <ul>
        <%
        User user = (User) UserStore.getInstance().getUser((String) request.getSession().getAttribute("user"));
          for (Activity activity : activities) {
            String type = activity.getActivityType();
            String message = activity.getActivityMessage();
            UUID userID = activity.getUserId();
            User userObject = UserStore.getInstance().getUser(userID);
            String username = userObject.getName();
            Instant creationTime = activity.getCreationTime();
            LocalDateTime ldt = LocalDateTime.ofInstant(creationTime, ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy h:mm:ss a");
            String time = ldt.format(formatter);
        %>
        <li>
          <strong><%= time %>:</strong>
          <a href="/profile/<%= username %>"><%= username %></a> <%= message %>
        </li>
         <%
          }
         %>
      </ul>
    </div>
    <div id="personalizeActivities">
      <p>Want to see activities that pertain to you and the conversations you have joined?<p>
      <form id="personalizeForm" action="/activityFeed" method="POST">
        <button type="submit" name="button" value="personalizeActivities">Personalize Activity Feed</button>
      </form>
    </div>
  </div>
</body>
</html>
