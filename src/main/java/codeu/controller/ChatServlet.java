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
import codeu.utils.Email;
import codeu.utils.ImageStorage;
import codeu.utils.TextFormatter;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;

@MultipartConfig(
  maxFileSize = 10 * 1024 * 1024, // max size for uploaded files
  maxRequestSize = 20 * 1024 * 1024, // max size for multipart/form-data
  fileSizeThreshold = 5 * 1024 * 1024 // start writing to Cloud Storage after 5MB
)

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
    User loggedInUser = userStore.getUser((String) request.getSession().getAttribute("user"));

    Conversation conversation = conversationStore.getConversationWithTitle(conversationTitle);
    if (conversation == null
        || (!conversation.getIsPublic()
            && (loggedInUser == null || !conversation.hasPermission(loggedInUser.getId())))) {
      // couldn't access conversation, redirect to conversation list
      System.out.println("Could not access: " + conversationTitle);
      response.sendRedirect("/conversations");
      return;
    }

    UUID conversationId = conversation.getId();

    List<Message> messages = messageStore.getMessagesInConversation(conversationId);
    messages = messageStore.sort(messages);
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

    Part image = request.getPart("image");
    String button = request.getParameter("button");
    String submitText = request.getParameter("submitText");

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
      joinConversation(user, conversation);

    } else if ("leaveButton".equals(button)) {
      leaveConversation(user, conversation);

    } else if (submitText != null && conversation.getConversationUsers().contains(user.getId())) {
      String messageContent = request.getParameter("message");
      if (messageContent.isEmpty()) {
        request.setAttribute("error", "Message body cannot be empty.");
        request.setAttribute("conversation", conversation);
        request.setAttribute(
            "messages", messageStore.getMessagesInConversation(conversation.getId()));
        request.setAttribute("conversationUsers", conversation.getConversationUsers());
        request.getRequestDispatcher("/WEB-INF/view/chat.jsp").forward(request, response);
        return;
      }
      // this removes any HTML from the message content
      String cleanedMessageContent =
          Jsoup.clean(
              messageContent, "", Whitelist.none(), new OutputSettings().prettyPrint(false));
      String finalMessageContent = TextFormatter.formatForDisplay(cleanedMessageContent);
      createMessage(request, finalMessageContent, user, conversation, false);

    } else if (image != null && conversation.getConversationUsers().contains(user.getId())) {
      ImageStorage imageStorage = new ImageStorage();
      String imageName = imageStorage.storeImage(image);
      createMessage(request, imageName, user, conversation, true);
    }

    // redirect to a GET request
    response.sendRedirect("/chat/" + conversationTitle);
  }

  /**
   * Determines if two users have exactly one shared conversation that they have joined
   *
   * @return boolean
   * @param u1 first user
   * @param u2 second user
   */
  private boolean hasSingleCommonConversation(UUID u1, UUID u2) {
    int count = 0;
    List<Conversation> conversations = conversationStore.getAllConversations();
    for (Conversation c : conversations) {
      if (c.getConversationUsers().contains(u1) && c.getConversationUsers().contains(u2)) {
        count++;
      }
    }
    return count == 1;
  }

  /**
   * If the current user leaves the conversation, ensure that the user is not in other conversations
   * with other users in the current conversation. If this is true, then remove users from to the
   * current user's conversationFriends list
   *
   * @param currentUser current user
   * @param conversation current conversation
   */
  private void removeConversationFriends(User currentUser, Conversation conversation) {
    List<User> oldFriends = new ArrayList<>();
    for (UUID u : conversation.getConversationUsers()) {
      // checking to see that the user and any users in the conversation
      // have only this conversation in common
      if (hasSingleCommonConversation(currentUser.getId(), u)) {
        // avoid ConcurrentModificationException
        oldFriends.add(UserStore.getInstance().getUser(u));
      }
      for (User u1 : oldFriends) {
        // if the two users are friends in only one conversation,
        // ensure they are no longer friends
        currentUser.removeConversationFriend(u1);
        u1.removeConversationFriend(currentUser);
      }
    }
  }

  /**
   * If the current user joins the conversation, ensure that the user is not already friends with
   * other users in the current conversation. If this is true, then add new users to the current
   * user's conversationFriends list
   *
   * @param currentUser current user
   * @param conversation current conversation
   */
  private void addConversationFriends(User currentUser, Conversation conversation) {
    List<User> newFriends = new ArrayList<>();
    for (UUID u : conversation.getConversationUsers()) {
      // checking to see that the user and any users in the conversation
      // are not friends yet
      if ((!(hasSingleCommonConversation(currentUser.getId(), u))
          && (!(currentUser.getConversationFriends().contains(u))))) {
        // avoid ConcurrentModificationException
        newFriends.add(UserStore.getInstance().getUser(u));
      }
      for (User u1 : newFriends) {
        // if the two users are not friends, ensure they become friends
        currentUser.addConversationFriend(u1);
        u1.addConversationFriend(currentUser);
      }
    }
  }

  /** Constructs a method object and adds it to messageStore */
  private void createMessage(
      HttpServletRequest request,
      String messageContent,
      User user,
      Conversation conversation,
      boolean containsImage) {

    Message message =
        new codeu.model.data.Message(
            UUID.randomUUID(),
            conversation.getId(),
            user.getId(),
            messageContent,
            Instant.now(),
            containsImage);
    messageStore.addMessage(message);

    createActivity(conversation, user, messageContent, containsImage);
  }

  /** Constructs an activity object and adds it to activityStore */
  private void createActivity(
      Conversation conversation, User user, String messageContent, boolean containsImage) {

    String activityMessage;
    if (containsImage) {
      activityMessage =
          " sent a picture in "
              + "<a href=\"/chat/"
              + conversation.getTitle()
              + "\">"
              + conversation.getTitle()
              + "</a>.";
    } else {
      activityMessage =
          " sent a message in "
              + "<a href=\"/chat/"
              + conversation.getTitle()
              + "\">"
              + conversation.getTitle()
              + "</a>"
              + ": "
              + messageContent;
    }

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

    Email email = new Email();
    email.sendEmailNotification(user, conversation);
  }

  /** Removes a user from a conversation */
  private void leaveConversation(User user, Conversation conversation) {
    removeConversationFriends(user, conversation);
    conversation.removeUser(user.getId());

    String activityMessage =
        " left "
            + "<a href=\"/chat/"
            + conversation.getTitle()
            + "\">"
            + conversation.getTitle()
            + "</a>.";

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

  /** Adds a user to a conversation */
  private void joinConversation(User user, Conversation conversation) {
    addConversationFriends(user, conversation);
    conversation.addUser(user.getId());

    String activityMessage =
        " joined "
            + "<a href=\"/chat/"
            + conversation.getTitle()
            + "\">"
            + conversation.getTitle()
            + "</a>.";

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
}
