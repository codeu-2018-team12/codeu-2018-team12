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
%>

<!DOCTYPE html>
<html>
<head>
  <% if (user != null) {%>
    <title><%= user.getName() %></title>
    <link rel="stylesheet" href="/css/main.css" type="text/css">
    <jsp:include page="./navbar.jsp" />
    <style>
      #activities {
        background-color: white;
        height: 500px;
        overflow-y: scroll
      }
    </style>

    <script>
      // scroll the chat div to the bottom
      function scrollChat() {
       var msgDiv = document.getElementById('messages');
      };
    </script>
  </head>
  <body onload="scrollChat()">
  <h1 id="title"><%= user.getName() %>'s Profile</h1>

  <form enctype="text/plain" method="get" name="putFile" id="putFile">
    <div>
    File Name: <input type="text" name="fileName" />
    <br />  Choose a File: <br />
    <input type="file" name="picToUpload">
    <input type="submit" onclick='uploadFile(this)' value="Upload Content" />
    </div>
    </form>

    <script>
    function uploadFile() {
      console.log("Start"); 
  var bucket = "chatu-196017.appspot.com";
  var filename = document.forms["putFile"]["fileName"].value;
  if (bucket == null || bucket == "" || filename == null || filename == "") {
    alert("Both Bucket and FileName are required");
    return false;
  } else {
    var postData = document.forms["putFile"]["content"].value;
    document.getElementById("content").value = null;

    var request = new XMLHttpRequest();
    request.open("POST", "/gcs/" + bucket + "/" + filename, false);
    request.setRequestHeader("Content-Type", "text/plain;charset=UTF-8");
    request.send(postData);   
  }
}

</script>

  <div id="container">
  <h2>Biography</h2>
    <% if (user.getBio() != null) { %>
       <%= user.getBio() %>
      <%} else {%>
        <p> This biography has not yet been set up! </p>
     <% } %>
 <% if (request.getSession().getAttribute("user") != null){
        if (request.getSession().getAttribute("user").equals(user.getName())) { %>
     <p> You can change your biography below: </p>
     <form action='' user method="POST">
       <label for="newBio">New Bio: </label>
       <input type="text" name="newBio" id="newBio">
       <button type="submit">Submit</button>
     </form>
<% } else{ %>
      <form action="/direct/<%= user.getName() %>">
       <input type="submit" value="Send <%= user.getName() %> a direct message" />
      </form>
  <% }
  } %>
    <br>
    <br>
  </div>
  <div id="container">
   <h2>Recent Activity</h2>
   <div id="activities">
     <ul>
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
        <%
         }
        %>
     </ul>
   </div>
  </div>
<% } else { %>
  <title>Profile Not Found</title>
  <link rel="stylesheet" href="/css/main.css" type="text/css">
  <jsp:include page="./navbar.jsp" />
  <h1 id="title">Profile Not Found</h1>
<% } %>
</body>
</html>