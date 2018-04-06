// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.controller;

import codeu.model.data.Activity;
import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.ActivityStore;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import codeu.utils.TextFormatter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;

/** Servlet class responsible for the chat page. */
public class ChatServlet extends HttpServlet {

  /** Store class that gives access to Conversations. */
  private ConversationStore conversationStore;

  /** Store class that gives access to Messages. */
  private MessageStore messageStore;

  /** Store class that gives access to Users. */
  private UserStore userStore;

  /** Store class that gives access to Users. */
  private ActivityStore activityStore;

  /** Set up state for handling chat requests. */
  @Override
  public void init() throws ServletException {
    super.init();
    setConversationStore(ConversationStore.getInstance());
    setMessageStore(MessageStore.getInstance());
    setUserStore(UserStore.getInstance());
    setActivityStore(ActivityStore.getInstance());
  }

  /**
   * Sets the ConversationStore used by this servlet. This function provides a common setup method
   * for use by the test framework or the servlet's init() function.
   */
  void setConversationStore(ConversationStore conversationStore) {
    this.conversationStore = conversationStore;
  }

  /**
   * Sets the MessageStore used by this servlet. This function provides a common setup method for
   * use by the test framework or the servlet's init() function.
   */
  void setMessageStore(MessageStore messageStore) {
    this.messageStore = messageStore;
  }

  /**
   * Sets the UserStore used by this servlet. This function provides a common setup method for use
   * by the test framework or the servlet's init() function.
   */
  void setUserStore(UserStore userStore) {
    this.userStore = userStore;
  }

  /**
   * Sets the ActivityStore used by this servlet. This function provides a common setup method for
   * use by the test framework or the servlet's init() function.
   */
  void setActivityStore(ActivityStore activityStore) {
    this.activityStore = activityStore;
  }

  /**
   * This function fires when a user navigates to the chat page. It gets the conversation title from
   * the URL, finds the corresponding Conversation, and fetches the messages in that Conversation.
   * It then forwards to chat.jsp for rendering.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String requestUrl = request.getRequestURI();
    String conversationTitle = requestUrl.substring("/chat/".length());

    Conversation conversation = conversationStore.getConversationWithTitle(conversationTitle);
    if (conversation == null) {
      // couldn't find conversation, redirect to conversation list
      System.out.println("Conversation was null: " + conversationTitle);
      response.sendRedirect("/conversations");
      return;
    }

    UUID conversationId = conversation.getId();

    List<Message> messages = messageStore.getMessagesInConversation(conversationId);
    List<UUID> conversationUsers = conversation.getConversationUsers();

    request.setAttribute("conversation", conversation);
    request.setAttribute("messages", messages);
    request.setAttribute("conversationUsers", conversationUsers);
    request.getRequestDispatcher("/WEB-INF/view/chat.jsp").forward(request, response);
  }

  /**
   * This function fires when a user submits the form on the chat page. It gets the logged-in
   * username from the session, the conversation title from the URL, and the chat message from the
   * submitted form data. It creates a new Message from that data, adds it to the model, and then
   * redirects back to the chat page.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String button = request.getParameter("button");

    String username = (String) request.getSession().getAttribute("user");
    if (username == null) {
      // user is not logged in, don't let them add a message
      response.sendRedirect("/login");
      return;
    }

    User user = userStore.getUser(username);
    if (user == null) {
      // user was not found, don't let them add a message
      response.sendRedirect("/login");
      return;
    }

    String requestUrl = request.getRequestURI();
    String conversationTitle = requestUrl.substring("/chat/".length());

    Conversation conversation = conversationStore.getConversationWithTitle(conversationTitle);
    if (conversation == null) {
      // couldn't find conversation, redirect to conversation list
      response.sendRedirect("/conversations");
      return;
    }

    if ("joinButton".equals(button)) {
      conversation.addUser(user.getId());
      String activityMessage =
          " joined " + "<a href=\"/chat/" + conversationTitle + "\">" + conversationTitle + "</a>.";
      Activity activity =
          new Activity(
              UUID.randomUUID(),
              user.getId(),
              conversation.getId(),
              Instant.now(),
              "joinedConvo",
              activityMessage,
              conversation.getConversationUsers(),
              conversation.getIsPublic());
      activityStore.addActivity(activity);
    }

    if ("leaveButton".equals(button)) {
      conversation.removeUser(user.getId());
      String activityMessage =
          " left " + "<a href=\"/chat/" + conversationTitle + "\">" + conversationTitle + "</a>.";
      Activity activity =
          new Activity(
              UUID.randomUUID(),
              user.getId(),
              conversation.getId(),
              Instant.now(),
              "leftConvo",
              activityMessage,
              conversation.getConversationUsers(),
              conversation.getIsPublic());
      activityStore.addActivity(activity);
    }

    if (button == null && conversation.getConversationUsers().contains(user.getId())) {
      String messageContent = request.getParameter("message");

      // this removes any HTML from the message content
      String cleanedMessageContent =
          Jsoup.clean(
              messageContent, "", Whitelist.none(), new OutputSettings().prettyPrint(false));
      String finalMessageContent = TextFormatter.formatForDisplay(cleanedMessageContent);

      Message message =
          new codeu.model.data.Message(
              UUID.randomUUID(),
              conversation.getId(),
              user.getId(),
              finalMessageContent,
              Instant.now());
      messageStore.addMessage(message);

      String activityMessage =
          " sent a message in "
              + "<a href=\"/chat/"
              + conversationTitle
              + "\">"
              + conversationTitle
              + "</a>"
              + ": "
              + finalMessageContent;

      Activity activity =
          new Activity(
              UUID.randomUUID(),
              user.getId(),
              conversation.getId(),
              Instant.now(),
              "messageSent",
              activityMessage,
              conversation.getConversationUsers(),
              conversation.getIsPublic());
      activityStore.addActivity(activity);
      sendEmailNotification(user, conversation);
    }
    // redirect to a GET request
    response.sendRedirect("/chat/" + conversationTitle);
  }

  /**
   * Method to send an email notification to all users in a conversation who are not logged on other
   * than the message sender
   */
  public void sendEmailNotification(User user, Conversation conversation) {

    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    List<UUID> conversationUsers = conversation.getConversationUsers();

    String msgBody =
        user.getName()
            + " sent a message in "
            + conversation.getTitle()
            + " on "
            + conversation.getCreationTime()
            + " while you were away. \n \n "
            + "Please log in to view this message.";

    SessionListener currentSession = SessionListener.getInstance();

    for (UUID conversationUserUUID : conversationUsers) {
      User conversationUser = userStore.getUser(conversationUserUUID);
      if (conversationUser != user
          && conversationUser != null
          && !currentSession.isLoggedIn(conversationUser.getName())) {
        try {
          javax.mail.Message msg = new MimeMessage(session);
          msg.setFrom(
              new InternetAddress(
                  "chatu-196017@appspot.gserviceaccount.com", "CodeU Team 12 Admin"));
          msg.addRecipient(
              javax.mail.Message.RecipientType.TO,
              new InternetAddress(conversationUser.getEmail(), conversationUser.getName()));
          msg.setSubject(user.getName() + " has sent you a message");
          msg.setText(msgBody);
          Transport.send(msg);
        } catch (AddressException e) {
          System.err.println("Invalid email address formatting. Email not sent.");
        } catch (MessagingException e) {
          System.err.println("An error has occurred with this message. Email not sent.");
        } catch (UnsupportedEncodingException e) {
          System.err.println("This character encoding is not supported. Email not sent");
        }
      }
    }
  }
}
