package codeu.controller;

import static codeu.model.store.basic.ActivityStore.sort;

import codeu.model.data.Activity;
import codeu.model.data.User;
import codeu.model.store.basic.ActivityStore;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import codeu.utils.ImageStorage;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig(
        maxFileSize = 10 * 1024 * 1024, // max size for uploaded files
        maxRequestSize = 20 * 1024 * 1024, // max size for multipart/form-data
        fileSizeThreshold = 5 * 1024 * 1024 // start writing to Cloud Storage after 5MB
)

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

  /**
   * Sets the ActivityStore used by this servlet. This function provides a common setup method for
   * use by the test framework or the servlet's init() function.
   */
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
              ? sort(activityStore.getAllPublicActivitiesWithUserId(user.getId()))
              : sort(
                  activityStore.getAllPermittedActivitiesWithUserId(
                      user.getId(), loggedInUser.getId()));
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
    Part image = request.getPart("image");
    String biography = request.getParameter("submitBiography");
    String name = requestUrl.substring("/profile/".length());
    User user = userStore.getUser(name);

    if (biography != null) {
      user.setBio(request.getParameter("newBio"));
    }

    if (image != null) {
      ImageStorage imageStorage = new ImageStorage();
      String imageName = imageStorage.storeImage(image);
      user.setProfilePicture(imageName);
    }
    response.sendRedirect(requestUrl);
  }
}
