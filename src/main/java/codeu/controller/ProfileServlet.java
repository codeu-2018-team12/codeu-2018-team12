package codeu.controller;

import codeu.model.data.Activity;
import codeu.model.data.User;
import codeu.model.store.basic.ActivityStore;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

/** Servlet class responsible for the profile page. */
public class ProfileServlet extends HttpServlet {

  /** Store class that gives access to Conversations. */
  private ConversationStore conversationStore;

  /** Store class that gives access to Messages. */
  private MessageStore messageStore;

  /** Store class that gives access to Users. */
  private UserStore userStore;

  private ActivityStore activityStore;

  /** Set up state for handling profile requests. */
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

  void setActivityStore(ActivityStore activityStore) {
    this.activityStore = activityStore;
  }

  /**
   * This function fires when a user navigates to a user's profile page. It gets the username from
   * the URL, finds the corresponding User, and fetches the messages posted by that user. It then
   * forwards to profile.jsp for rendering.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String requestUrl = request.getRequestURI();
    String name = requestUrl.substring("/profile/".length());
    User loggedInUser = userStore.getUser((String) request.getSession().getAttribute("user"));
    User user = userStore.getUser(name);
    List<Activity> activities = null;
    List<Activity> activitiesPermitted;
    if (user != null) {
      activitiesPermitted =
          loggedInUser == null
              ? activityStore.getAllPublicActivitiesWithUserIdSorted(user.getId())
              : activityStore.getAllPermittedActivitiesWithUserIdSorted(
                  user.getId(), loggedInUser.getId());
      activities = activityStore.getActivitiesPerPrivacy(user, activitiesPermitted);
    }
    request.setAttribute("activities", activities);
    request.setAttribute("user", user);
    request.setAttribute("loggedInUser", loggedInUser);
    request.getRequestDispatcher("/WEB-INF/view/profile.jsp").forward(request, response);
  }

  /** This function fires when a user submits the form on the profile page. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String requestUrl = request.getRequestURI();
    String name = requestUrl.substring("/profile/".length());
    User user = userStore.getUser(name);
    user.setBio(request.getParameter("newBio"));
    response.sendRedirect(requestUrl);
  }
}
