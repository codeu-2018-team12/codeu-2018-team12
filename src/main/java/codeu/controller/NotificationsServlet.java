package codeu.controller;

import codeu.model.data.User;
import codeu.model.store.basic.ActivityStore;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.UserStore;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
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

public class NotificationsServlet extends HttpServlet {

  /** Store class that gives access to activities. */
  private ActivityStore activityStore;

  /** Store class that gives access to users. */
  private UserStore userStore;

  /** Store class that gives access to users. */
  private ConversationStore conversationStore;

  /** Set up state for handling activity-related requests */
  @Override
  public void init() throws ServletException {
    super.init();
    setActivityStore(ActivityStore.getInstance());
    setUserStore(UserStore.getInstance());
    setConversationStore(ConversationStore.getInstance());
  }

  /**
   * Sets the UserStore used by this servlet. This function provides a common setup method for use
   * by the test framework or the servlet's init() function.
   */
  void setUserStore(UserStore userStore) {
    this.userStore = userStore;
  }

  /**
   * Sets the ConversationStore used by this servlet. This function provides a common setup method
   * for use by the test framework or the servlet's init() function.
   */
  void setConversationStore(ConversationStore conversationStore) {
    this.conversationStore = conversationStore;
  }

  /**
   * Sets the Activity used by this servlet. This function provides a common setup method for use by
   * the test framework or the servlet's init() function.
   */
  void setActivityStore(ActivityStore activityStore) {
    this.activityStore = activityStore;
  }

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    List<User> users = userStore.getUsers();

    for (User user : users) {
      String emailBody =
          "Hello "
              + user.getName()
              + "! Here's what you missed while away. Please"
              + "log in to view these messages. \n \n";
      Queue<String> notifications = user.getStoredNotifications();
      while (!notifications.isEmpty()) {
        String message = notifications.peek();
        emailBody = emailBody + message + "\n";
        notifications.remove();
      }
      sendEmailNotification(user, emailBody);
      user.clearNotifications();
    }
  }

  /**
   * Method to retrieve all of the stored notifications for each user and send them out in an email
   */
  public void sendEmailNotification(User user, String emailBody) {

    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    try {
      javax.mail.Message msg = new MimeMessage(session);
      msg.setFrom(
          new InternetAddress("chatu-196017@appspot.gserviceaccount.com", "CodeU Team 12 Admin"));
      msg.addRecipient(
          javax.mail.Message.RecipientType.TO,
          new InternetAddress(user.getEmail(), user.getName()));
      msg.setSubject("Messages you may have missed");
      msg.setText(emailBody);
      Transport.send(msg);
    } catch (AddressException e) {
      System.err.println("Invalid email address formatting. Email not sent.");
    } catch (MessagingException e) {
      System.err.println("An error has occurred with this message. Email not sent.");
    } catch (UnsupportedEncodingException e) {
      System.err.println("This character encoding is not supported. Email not sent");
    }
  }

  public void doPost(HttpServletRequest req, HttpServletResponse resp) {}
}
