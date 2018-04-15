package codeu.controller;

import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;

/** Servlet class responsible for the settings page. */
public class SettingsServlet extends HttpServlet {

  /** Store class that gives access to Conversations. */
  private ConversationStore conversationStore;

  /** Store class that gives access to Messages. */
  private MessageStore messageStore;

  /** Store class that gives access to Users. */
  private UserStore userStore;

  /** Set up state for handling settings requests. */
  @Override
  public void init() throws ServletException {
    super.init();
    setConversationStore(ConversationStore.getInstance());
    setMessageStore(MessageStore.getInstance());
    setUserStore(UserStore.getInstance());
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

  /** This function fires when a user navigates to their settings page. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String username = (String) request.getSession().getAttribute("user");
    User user = userStore.getUser(username);
    request.setAttribute("user", user);
    request.getRequestDispatcher("/WEB-INF/view/settings.jsp").forward(request, response);
  }

  /** This function fires when a user submits a setting . */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String username = (String) request.getSession().getAttribute("user");
    User currentUser = userStore.getUser(username);
    request.setAttribute("user", currentUser);

    String password = request.getParameter("password");
    String confirmPassword = request.getParameter("confirmPassword");
    String email = request.getParameter("email");
    String status = request.getParameter("notificationStatus");
    boolean notificationStatus = true;
    if (status != null) {
      notificationStatus = status.equals("optIn");
    }
    String notificationFrequency = request.getParameter("notificationFrequency");
    String profilePrivacy = request.getParameter("profilePrivacy");
    String activityFeedPrivacy = request.getParameter("activityFeedPrivacy");

    if (email == null && password == null && confirmPassword == null) {
      if (request.getParameter("submitNotification") != null) {
        userStore.getUser(username).setNotificationFrequency(notificationFrequency);
        userStore.getUser(username).setNotificationStatus(notificationStatus);
        request.setAttribute(
            "successNotifications", "You have successfully updated your notification status.");
      }

      if (request.getParameter("submitSitePrivacy") != null) {
        userStore.getUser(username).setActivityFeedPrivacy(activityFeedPrivacy);
        userStore.getUser(username).setProfilePrivacy(profilePrivacy);
        request.setAttribute("successPrivacy", "You have successfully updated your privacy settings.");
      }
    } else {

      if (email == null && (password == null || password.length() < 8)) {
        request.setAttribute("error", "Please enter a password that is at least 8 characters.");
        request.getRequestDispatcher("/WEB-INF/view/settings.jsp").forward(request, response);
        return;
      }

      if (confirmPassword.equals("") && email.equals("")) {
        request.setAttribute(
            "error", "Please enter information for at least one field before submitting");
        request.getRequestDispatcher("/WEB-INF/view/settings.jsp").forward(request, response);
        return;
      }

      if (password != null && !password.equals(confirmPassword)) {
        request.setAttribute("error", "Your password and confirmation password do not match.");
        request.getRequestDispatcher("/WEB-INF/view/settings.jsp").forward(request, response);
        return;
      }

      if (request.getParameter("submitPassword") != null) {
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        userStore.getUser(username).setPassword(passwordHash);
        request.setAttribute("successInfo", "You have successfully updated your information.");
      } else if (request.getParameter("submitEmail") != null) {
        userStore.getUser(username).setEmail(email);
        request.setAttribute("successInfo", "You have successfully updated your information.");
      }
    }

    request.getRequestDispatcher("/WEB-INF/view/settings.jsp").forward(request, response);
  }
}
