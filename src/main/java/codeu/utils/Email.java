package codeu.utils;

import codeu.controller.SessionListener;
import codeu.model.data.Conversation;
import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email {

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
      User conversationUser = UserStore.getInstance().getUser(conversationUserUUID);
      if (conversationUser != user
          && conversationUser != null
          && !currentSession.isLoggedIn(conversationUser.getName())
          && conversationUser.getNotificationStatus()
              && !conversationUser.getEmail().equals("codeUChatTest@gmail.com")) {
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
