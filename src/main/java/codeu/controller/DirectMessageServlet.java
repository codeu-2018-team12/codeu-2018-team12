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

/** Servlet class responsible for the direct message page. */
public class DirectMessageServlet extends HttpServlet {

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
   * This function fires when a user navigates to the direct message page. It gets the user's name
   * from the url and then retrieves the direct message between the logged in user and that user.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String requestUrl = request.getRequestURI();
    String loggedInUsername = (String) request.getSession().getAttribute("user");
    String otherUsername = requestUrl.substring("/direct/".length());
    User loggedInUser = userStore.getUser(loggedInUsername);
    User otherUser = userStore.getUser(otherUsername);

    if (loggedInUser == null) {
      response.sendRedirect("/login");
      return;
    }

    if (otherUser == null || loggedInUser.getId().equals(otherUser.getId())) {
      response.sendRedirect("/conversations");
      return;
    }

    String convoName =
        loggedInUsername.compareTo(otherUsername) < 0
            ? "direct:" + loggedInUsername + "-" + otherUsername
            : "direct:" + otherUsername + "-" + loggedInUsername;
    Conversation conversation = conversationStore.getConversationWithTitle(convoName);

    if (conversation == null) {
      conversation =
          new Conversation(
              UUID.randomUUID(), loggedInUser.getId(), convoName, Instant.now(), false);
      conversation.addUser(otherUser.getId());
      conversationStore.addConversation(conversation);
    }
    List<UUID> conversationUsers = conversation.getConversationUsers();
    List<Message> messages = messageStore.getMessagesInConversation(conversation.getId());

    request.setAttribute("conversationUsers", conversationUsers);
    request.setAttribute("conversation", conversation);
    request.setAttribute("messages", messages);
    request.setAttribute("loggedInUser", loggedInUser);
    request.setAttribute("otherUser", otherUser);
    request.getRequestDispatcher("/WEB-INF/view/directMessage.jsp").forward(request, response);
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
    String requestUrl = request.getRequestURI();
    String loggedInUsername = (String) request.getSession().getAttribute("user");
    String otherUsername = requestUrl.substring("/direct/".length());
    User loggedInUser = userStore.getUser(loggedInUsername);
    User otherUser = userStore.getUser(otherUsername);

    if (loggedInUser == null) {
      // user was not found, don't let them add a message
      response.sendRedirect("/login");
      return;
    }

    if (otherUser == null || loggedInUser.getId().equals(otherUser.getId())) {
      response.sendRedirect("/conversations");
      return;
    }

    String convoName =
        loggedInUsername.compareTo(otherUsername) < 0
            ? "direct:" + loggedInUsername + "-" + otherUsername
            : "direct:" + otherUsername + "-" + loggedInUsername;

    Conversation conversation = conversationStore.getConversationWithTitle(convoName);
    if (conversation == null) {
      // couldn't find conversation, redirect to conversation list
      response.sendRedirect("/conversations");
      return;
    }

    if (conversation.getConversationUsers().contains(loggedInUser.getId())) {
      String messageContent = request.getParameter("message");
      if (messageContent.isEmpty()) {
        request.setAttribute("error", "Message body cannot be empty.");
        request.setAttribute("conversationUsers", conversation.getConversationUsers());
        request.setAttribute("conversation", conversation);
        request.setAttribute("messages", messageStore.getMessagesInConversation(conversation.getId()));
        request.setAttribute("loggedInUser", loggedInUser);
        request.setAttribute("otherUser", otherUser);
        request.getRequestDispatcher("/WEB-INF/view/chat.jsp").forward(request, response);
        return;
      }

      // this removes any HTML from the message content
      String cleanedMessageContent =
          Jsoup.clean(
              messageContent, "", Whitelist.none(), new OutputSettings().prettyPrint(false));
      String finalMessageContent = TextFormatter.formatForDisplay(cleanedMessageContent);

      Message message =
          new codeu.model.data.Message(
              UUID.randomUUID(),
              conversation.getId(),
              loggedInUser.getId(),
              finalMessageContent,
              Instant.now());
      messageStore.addMessage(message);

      String activityMessage =
          " sent a direct message to " + otherUser.getName() + ": " + finalMessageContent;
      Activity activity =
          new Activity(
              UUID.randomUUID(),
              loggedInUser.getId(),
              conversation.getId(),
              Instant.now(),
              "messageSent",
              activityMessage,
              conversation.getConversationUsers(),
              conversation.getIsPublic());
      activityStore.addActivity(activity);
      sendEmailNotification(loggedInUser, conversation);
    }
    // redirect to a GET request
    response.sendRedirect("/direct/" + otherUser.getName());
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
            + " while you were away. \n \n ";

    SessionListener currentSession = SessionListener.getInstance();

    for (UUID conversationUserUUID : conversationUsers) {
      User conversationUser = userStore.getUser(conversationUserUUID);
      if (conversationUser != user
          && conversationUser != null
          && !currentSession.isLoggedIn(conversationUser.getName())
          && conversationUser.getNotificationStatus()) {

        if (user.getNotificationFrequency().equals("everyMessage")) {
          try {
            javax.mail.Message msg = new MimeMessage(session);
            msg.setFrom(
                new InternetAddress(
                    "chatMessageAdmin@chatu-196017.appspotmail.com", "CodeU Team 12 Admin"));
            msg.addRecipient(
                javax.mail.Message.RecipientType.TO,
                new InternetAddress(conversationUser.getEmail(), conversationUser.getName()));
            msg.setSubject(user.getName() + " has sent you a message");
            msgBody += " Please log in to view this message";
            msg.setText(msgBody);
            Transport.send(msg);
          } catch (AddressException e) {
            System.out.println("Invalid email address formatting. Email not sent.");
            System.out.println("AddressException:" + e);
          } catch (MessagingException e) {
            System.out.println("An error has occurred with this message. Email not sent.");
            System.out.println("MessagingException:" + e);
          } catch (UnsupportedEncodingException e) {
            System.out.println("This character encoding is not supported. Email not sent");
            System.out.println("UnsupportedEncodingException:" + e);
          }
        }
      } else {
        user.addNotification(msgBody);
      }
    }
  }
}
