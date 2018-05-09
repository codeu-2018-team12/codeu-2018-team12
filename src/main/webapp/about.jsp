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
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet"
     id="bootstrap-css">
    <jsp:include page="/WEB-INF/view/navbar.jsp" />
    <link rel="stylesheet" href="/css/main.css?d5eeddF3dFrRddFefdd">
    <link rel="stylesheet" href="/css/about.css?ddefF3eddRFdsefFerd5">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
    <script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
  </head>
  <body>
    <div class="container">
     <div id="about">
      <h1>About This Project</h1>
     <p>
        This project was created by Team 12 as part of CodeU, a program where groups of students, with the guidance
        of an engineer mentor, collaborate remotely over the course of 12 weeks to build a chat web app and
        practice their technical skills.<br><br>

        This project is still under construction, but a list of existing features can be found below.
        To get started exploring this app, sign up <b><a href="/register">
        here</a></b> and load some <b><a href="/testdata"> test data</a></b>, or check out our project on
        <b><a href="https://github.com/codeu-2018-team12/codeu-2018-team12">Github</a></b>.<br>
    </p>
   </div>
  </div>
  <div class="container">
    <h1>About Team 12</h1>
    <div class="row">
      <div class="col-md-2 col-md-offset-1">
        <img class="img-responsive" src="../resources/about-5.png" />
        <p class="contributorName">Jeremy Archer</p>
        <p>Jeremy is from Chicago, IL and works as a site reliability engineer in  New York.</p>
      </div>
      <div class="col-md-2">
        <img class="img-responsive" src="../resources/about-4.png" />
        <p class="contributorName">Alison Rosenman</p>
        <p>Alison is from Mercer Island, WA and is a student at Haverford College.</p>
      </div>
      <div class="col-md-2">
        <img class="img-responsive" src="../resources/about-3.png" />
        <p class="contributorName">Marie Zimmerman</p>
        <p>Marie is from Atlanta, Georgia and is a student at Georgia Tech.</p>
      </div>
      <div class="col-md-2">
        <img class="img-responsive" src="../resources/about-2.png" />
        <p class="contributorName">Kevin Kane</p>
        <p>Kevin is from Laurel, Maryland and is a student at the University of Maryland, College Park.</p>
      </div>
      <div class="col-md-2">
        <img class="img-responsive" src="../resources/about-1.png" />
        <p class="contributorName">Maria Mahin</p>
        <p>Maria is from Queens, NY and is a student at Hunter College.</p>
      </div>
    </div>
 </div>
</div>

<div class="container">
  <div class="page-header">
    <h1 id="timeline">Project Milestones</h1>
  </div>
  <ul class="timeline">
    <li>
      <div class="timeline-badge"></div>
      <div class="timeline-panel">
        <div class="timeline-heading">
          <h4 class="timeline-title"> Users can now register for the site</h4>
          <p><small class="text-muted"></i>March 2, 2018</small></p>
        </div>
      </div>
    </li>
    <li class="timeline-inverted">
      <div class="timeline-badge warning"></div>
      <div class="timeline-panel">
        <div class="timeline-heading">
          <h4 class="timeline-title">Users have their own customizable profile pages</h4>
          <p><small class="text-muted"></i>March 18, 2018</small></p>
        </div>
      </div>
    </li>
    <li>
      <div class="timeline-badge">
      </div>
        <div class="timeline-panel">
          <div class="timeline-heading">
            <h4 class="timeline-title">Users can now log out of the application</h4>
            <p><small class="text-muted"></i>March 19, 2018</small></p>
           </div>
         </div>
       </li>
        <li class="timeline-inverted">
          <div class="timeline-badge warning"></div>
             <div class="timeline-panel">
              <div class="timeline-heading">
                <h4 class="timeline-title">Users can stay updated with the current activity of other
                 users on the site through an activity feed</h4>
                <p><small class="text-muted"></i>March 20, 2018</small></p>
              </div>
           </div>
        </li>
        <li>
          <div class="timeline-badge"></div>
            <div class="timeline-panel">
              <div class="timeline-heading">
                <h4 class="timeline-title">Text markdown and emojis are now supported in messages</h4>
                <p><small class="text-muted"></i>March 21, 2018</small></p>
              </div>
            </div>
          </li>
          <li class="timeline-inverted">
            <div class="timeline-badge warning"></div>
              <div class="timeline-panel">
                <div class="timeline-heading">
                  <h4 class="timeline-title">Users can join and leave conversations</h4>
                  <p><small class="text-muted"></i>March 21, 2018</small></p>
                </div>
              </div>
            </li>
            <li>
            <div class="timeline-badge"></div>
              <div class="timeline-panel">
                <div class="timeline-heading">
                  <h4 class="timeline-title">Users can search for other users on the site</h4>
                  <p><small class="text-muted"></i>March 27, 2018</small></p>
                </div>
              </div>
            </li>
            <li class="timeline-inverted">
              <div class="timeline-badge warning"></div>
                <div class="timeline-panel">
                   <div class="timeline-heading">
                      <h4 class="timeline-title">Users can edit and display a biography on their profile pages</h4>
                      <p><small class="text-muted"></i>March 27, 2018</small></p>
                    </div>
                  </div>
                </li>
              <li>
               <div class="timeline-badge"></div>
                 <div class="timeline-panel">
                   <div class="timeline-heading">
                     <h4 class="timeline-title">Email notifications are now sent to users who receive a message in
                      a conversation but are currently not logged into the chat app</h4>
                   <p><small class="text-muted"></i>April 4, 2018</small></p>
               </div>
            </div>
          </li>
        <li class="timeline-inverted">
          <div class="timeline-badge warning"></div>
             <div class="timeline-panel">
               <div class="timeline-heading">
                 <h4 class="timeline-title">Activities on a user's activity feed can be personalized to
                  each user</h4>
                 <p><small class="text-muted"></i>April 5, 2018</small></p>
               </div>
            </div>
          </li>
        <li>
          <div class="timeline-badge"></div>
            <div class="timeline-panel">
               <div class="timeline-heading">
                 <h4 class="timeline-title"> Users can now send private, direct-messages to one another</h4>
                 <p><small class="text-muted"></i>April 14, 2018</small></p>
              </div>
            </div>
         </li>
        <li class="timeline-inverted">
           <div class="timeline-badge warning"></div>
             <div class="timeline-panel">
               <div class="timeline-heading">
                 <h4 class="timeline-title">Users can update their personal information, change their settings, and
                  specify their preferences for site privacy</h4>
                  <p><small class="text-muted"></i>April 18, 2018</small></p>
                </div>
              </div>
            </li>
          <li>
           <div class="timeline-badge"></div>
             <div class="timeline-panel">
              <div class="timeline-heading">
               <h4 class="timeline-title"> Users can now upload profile pictures</h4>
               <p><small class="text-muted"></i>April 28, 2018</small></p>
             </div>
           </div>
        </li>
        <li class="timeline-inverted">
          <div class="timeline-badge warning"></div>
            <div class="timeline-panel">
              <div class="timeline-heading">
               <h4 class="timeline-title">Users can search for conversations and messages</h4>
                 <p><small class="text-muted"></i>April 29, 2018</small></p>
             </div>
          </div>
        </li>
        <li>
          <div class="timeline-badge"></div>
            <div class="timeline-panel">
              <div class="timeline-heading">
                <h4 class="timeline-title">Images can be sent from one user to another through messages</h4>
                <p><small class="text-muted"></i>May 2, 2018</small></p>
              </div>
            </div>
          </li>
       </ul>
   </div>
  </body>
</html>
