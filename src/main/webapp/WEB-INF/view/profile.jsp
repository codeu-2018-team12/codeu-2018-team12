<%@ page import="java.util.List" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.data.Activity" %>
<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.store.basic.ConversationStore" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="java.time.*" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%
List<Activity> activities = (List<Activity>) request.getAttribute("activities");
User user = (User) request.getAttribute("user");
User loggedInUser = (User) request.getAttribute("loggedInUser");
%>

<!DOCTYPE html>
<html>
  <head>
    <% if (user != null) {%>
      <title><%= user.getName() %></title>
      <link rel="stylesheet" href="/css/main.css">
      <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet"
       id="bootstrap-css">
      <link rel="stylesheet" href="/css/main.css?DwvEcerrgedfdfreEeE1e" type="text/css">
      <link rel="stylesheet" href="/css/profile.css?DwvEcefrrgrfredEeE1e" type="text/css">
      <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
      <script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
      <script>
      function scrollChat() {
       var msgDiv = document.getElementById('messages');
      };
     </script>
     <jsp:include page="./navbar.jsp" />
  </head>
  <body onload="scrollChat()">
    <div class="container">
      <div class="row">
        <div class="col-md-2">
          <% if (request.getSession().getAttribute("user") != null){
             if (request.getSession().getAttribute("user").equals(user.getName())) { %>
            <form id="pictureUpload" method="POST" action="/profile/<%= user.getName() %>" enctype="multipart/form-data">
              <label for="image">
                 <% if (loggedInUser.getProfilePicture() != null) { %>
                   <img id="profile-picture" src="http://storage.googleapis.com/chatu-196017.appspot.com/<%= user
                    .getProfilePicture()%>">
                 <% } else { %>
                   <img id="profile-picture" src="../resources/codeu.png">
                 <% } %>
                 <input type="file" name="image" id="image" onchange="form.submit()" accept="image/*" style="display:
                  none;"/>
               </label>
             </form>
        <% } else { %>
          <img id="profile-picture" src="../resources/codeu.png">
        <% }
        }%>
      </div>
      <div class="col-md-10">
        <h1 id="title"><%= user.getName() %>'s Profile</h1>
        <h2>Biography</h2>
        <% if (user.getBio() != null) { %>
           <%= user.getBio() %>
        <%} else {%>
           <p> This biography has not yet been set up! </p>
        <% } %>
        <% if (request.getSession().getAttribute("user") != null){
           if (request.getSession().getAttribute("user").equals(user.getName())) { %>
           <p> You can change your biography below: </p>
        <form action="/profile/<%= user.getName() %>" method="POST">
          <label for="newBio">New Bio: </label>
          <input type="text" name="newBio" id="newBio">
          <button type="submit" name="submitBiography" value="submitBiography">Submit</button>
        </form>
      <% } else {
         if ((!(user.getProfilePrivacy().equals("noContent"))) || ((user.getProfilePrivacy().equals("someContent"))
          && (loggedInUser.getConversationFriends().contains(user.getId())))) { %>
         <form action="/direct/<%= user.getName() %>">
            <input type="submit" value="Send <%= user.getName() %> a direct message" />
         </form>
         <% } %>
      <% } %>
    <% } %>
      <div id="container">
        <h2>Recent Activity</h2>
        <div id="activities">
          <ul>
           <% if ((!(user.getProfilePrivacy().equals("noContent"))) || ((user.getProfilePrivacy().equals("someContent"))
              && (loggedInUser.getConversationFriends().contains(user.getId())))) { %>
           <%
             for (Activity activity : activities) {
               String type = activity.getActivityType();
               String message = activity.getActivityMessage();
               Instant creationTime = activity.getCreationTime();
               LocalDateTime ldt = LocalDateTime.ofInstant(creationTime, ZoneId.systemDefault());
               DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy h:mm:ss a");
               String time = ldt.format(formatter);
           %>
           <li>
             <strong><%= time %>:</strong>
             <%= user.getName() %> <%= message %>
           </li>
            <% }
              } %>
           </ul>
         </div>
       </div>
     <% } else { %>
       <title>Profile Not Found</title>
       <link rel="stylesheet" href="/css/main.css" type="text/css">
       <jsp:include page="./navbar.jsp" />
       <h1 id="title">Profile Not Found</h1>
    <% }%>
 </div>
</div>
</div>
</body>
</html>
