package codeu.controller;

import codeu.model.data.Activity;
import codeu.model.data.Conversation;
import codeu.model.data.User;
import codeu.model.store.basic.ActivityStore;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.UserStore;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet class responsible for activity feed. */
public class PersonalActivityServlet extends HttpServlet {

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
   * Sets the ActivityStore used by this servlet. This function provides a common setup method for
   * use by the test framework or the servlet's init() function.
   */
  void setActivityStore(ActivityStore activityStore) {
    this.activityStore = activityStore;
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
   * This function fires when a user navigates to the activity feed page. It gets a list of all
   * current messages and forwards them to activityFeed.jsp.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String username = (String) request.getSession().getAttribute("user");
    if (username == null) {
      response.sendRedirect("/activityFeed");
      return;
    }
    User user = userStore.getUser(username);
    UUID userID = user.getId();
    List<Conversation> conversations = conversationStore.getAllConversationsSorted();
    List<Activity> conversationActivities = new ArrayList<>();
    List<Activity> tailoredActivities = new ArrayList<>();

    // retrieve activities for the conversations a user has joined
    for (Conversation conversation : conversations) {
      List<UUID> conversationUsers = conversation.getConversationUsers();
      if (conversationUsers.contains(user.getId())) {
        conversationActivities.add(
            activityStore.getActivityWithConversationID(conversation.getId()));
      }
    }
    tailoredActivities.addAll(conversationActivities);

    // retrieve user activities
    List<Activity> userActivities = activityStore.getActivitiesWithUserID(userID);
    tailoredActivities.addAll(userActivities);

    // remove any duplicates
    Set<Activity> hashSet = new HashSet<>();
    hashSet.addAll(tailoredActivities);
    tailoredActivities.clear();
    tailoredActivities.addAll(hashSet);

    // sort the activities
    List<Activity> personalizedActivities = activityStore.getActivityListSorted(tailoredActivities);
    List<Activity> privacyActivities =
        activityStore.getActivitiesPerPrivacy(user, personalizedActivities);

    request.setAttribute("activities", privacyActivities);
    request
        .getRequestDispatcher("/WEB-INF/view/personalActivityFeed.jsp")
        .forward(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {}
}
