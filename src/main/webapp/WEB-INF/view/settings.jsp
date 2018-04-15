<%@ page import="java.util.List" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.data.User" %>

<% User user = (User) request.getAttribute("user"); %>

<!DOCTYPE html>
<html>
<head>
  <title>Settings</title>
  <link rel="stylesheet" href="/css/main.css">
  <jsp:include page="./navbar.jsp" />
</head>
<body>
    <h1 id="title">Settings Page</h1>
    <div id="container">
        <p>
          Welcome! This is your settings page, where you can adjust your preferences for this site, update <br>
          your personal information, or choose what you would like to share with other site users.
        </p>
        <hr/>
          <% if (request.getAttribute("error") != null) { %>
            <h2 style="color:red"><%= request.getAttribute("error") %></h2>
          <% }
            if (request.getAttribute("successInfo") != null) { %>
            <h2 style="color:green"><%= request.getAttribute("successInfo") %></h2>
          <% } %>

        <h2><u>Update Information:</u></h2>
        <form action="/settings" method="POST">
          <label for="password">Password:</label><br>
          <input type="password" name="password" id="password"><br>
          <label for="confirmPassword">Confirm Password:</label><br>
          <input type="password" name="confirmPassword" id="confirmPassword">
          <button type="submit" name="submitPassword" value="submitPassword">Submit</button><br>
          <label for="email">Email:</label><br>
          <input type="text" name="email" id="email">
          <button type="submit" name="submitEmail" value="submitEmail">Submit</button><br>
        </form>
        <br>
        <hr/>
        <h2><u>Site Privacy:</u></h2>
        <% if (request.getAttribute("successPrivacy") != null) { %>
          <h2 style="color:green"><%= request.getAttribute("successPrivacy") %></h2>
        <% } %>
        <p>Adjust what other users can see about your activity on the site.</p>
        <% String profilePrivacy = user.getProfilePrivacy();
           String activityFeedPrivacy = user.getActivityFeedPrivacy();
           if (profilePrivacy.equals("allContent")) {
             profilePrivacy = "Allow all users to direct message me and see my sent messages";
           }
           if (profilePrivacy.equals("someContent")) {
             profilePrivacy = "Allow only users in conversations I have joined to direct message me and see my sent messages";
           }
           if (profilePrivacy.equals("noContent")) {
             profilePrivacy = "Prohibit direct messages from all users and allow only myself to see my sent messages";
           }
           if (activityFeedPrivacy.equals("allContent")) {
             activityFeedPrivacy = "Allow all users to see my activities";
           }
           if (activityFeedPrivacy.equals("someContent")) {
             activityFeedPrivacy = "Allow only users in conversations I have joined to see my activities";
           }
           if (activityFeedPrivacy.equals("noContent")) {
             activityFeedPrivacy = "Prohibit all users besides myself from seeing my activities";
           } %>
        <p>Your current privacy settings: <br>
          <b><u>Profile Privacy</b></u>: <%= profilePrivacy %><br>
          <b><u>Activity Feed Privacy</b></u>: <%= activityFeedPrivacy %><br>
        </p>
        <form action="/settings" method="POST">
        <label for="profilePrivacy">Profile Privacy: </label> <br>
        <select name ="profilePrivacy" >
          <option value="allContent">Allow all users to direct message me and see my sent messages</option>
          <option value="someContent">Allow only users in conversations I have joined to direct message me and see my sent messages</option>
          <option value="noContent">Prohibit direct messages from all users and allow only myself to see my sent messages</option>
        </select>
        <button type="submit" name="submitProfilePrivacy" value="submitProfilePrivacy">Submit</button>
        <br><br>
        <label for="activityFeedPrivacy">Activity Feed Privacy: </label> <br>
        <select name ="activityFeedPrivacy" >
          <option value="allContent">Allow all users to see my activities</option>
          <option value="someContent">Allow only users in conversations I have joined to see my activities</option>
          <option value="noContent">Prohibit all users besides myself from seeing my activities</option>
        </select>
        <button type="submit" name="submitActivityFeedPrivacy" value="submitActivityFeedPrivacy">Submit</button>
        <br><br>
        <p>Note: Activities for CodeU Chat App include the following: </p>
        <ul>
          <li>Registering an account</li>
          <li>Creating a conversation</li>
          <li>Joining a conversation</li>
          <li>Leaving a conversation</li>
          <li>Sending a message in a conversation</li>
        </ul>
        </form>
        <br><br>
        <hr/>
        <h2><u>Notifications:</u></h2>
        <% if (request.getAttribute("successNotifications") != null) { %>
              <h2 style="color:green"><%= request.getAttribute("successNotifications") %></h2>
        <% } %>
        <p>
           Select when you want to receive email notifications, or disable them all together. Please note that <br>
           by default, email notifications are sent with each new message when you are not logged in.
        </p>
        <% Boolean status = user.getNotificationStatus();
           String curStatus = status ? "Enabled" : "Disabled";
           String frequency = user.getNotificationFrequency();
           if (frequency.equals("everyMessage")) {
               frequency = "Every Message";
           } else if (frequency.equals("everyHour")) {
               frequency = "Every Hour";
           } else if (frequency.equals("everyFourHours")) {
               frequency = "Every Four Hours";
           } else {
               frequency = "Every Day";
           } %>
        <p> Your current preferences: <br>
           <b><u>Notification Status</b></u> : <%= curStatus %> <br>
           <% if (curStatus.equals("Enabled")) { %>
           <b><u>Frequency</b></u>: <%= frequency %> <br>
           <% } %>
        </p>

        <form action="/settings" method="POST">
        <label for="notificationStatus">Notifications: </label><br>
        <select name= "notificationStatus">
           <option value="optIn">Yes, I would like to receive notifications</option>
           <option value="optOut">No, I would not like receive notifications</option>
        </select>
        <br><br>
        <label for="notificationFrequency">Notification Frequency: </label> <br>
        <select name ="notificationFrequency" >
           <option value="everyMessage">Every message</option>
           <option value="everyhour">Every hour</option>
           <option value="everyFourHours">Every four hours</option>
           <option value="everyDay">Every day</option>
        </select>
        <br><br>
        <button type="submit" name="submitNotification" value="submitNotification">Submit</button>
        </form>
        <br><br>
    </div>
</body>



