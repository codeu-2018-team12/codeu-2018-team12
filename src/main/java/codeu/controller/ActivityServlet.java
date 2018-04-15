package codeu.controller;

import codeu.model.data.Activity;
import codeu.model.data.Conversation;
import codeu.model.data.User;
import codeu.model.store.basic.ActivityStore;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.UserStore;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet class responsible for activity feed. */
public class ActivityServlet extends HttpServlet {

  /** Store class that gives access to activities. */
  private ActivityStore activityStore;

  private UserStore userStore;

  /** Set up state for handling activity-related requests */
  @Override
  public void init() throws ServletException {
    super.init();
    setActivityStore(ActivityStore.getInstance());
    setUserStore(UserStore.getInstance());
  }

  /**
   * Sets the ActivityStore used by this servlet. This function provides a common setup method for
   * use by the test framework or the servlet's init() function.
   */
  void setActivityStore(ActivityStore activityStore) {
    this.activityStore = activityStore;
  }

  void setUserStore(UserStore userStore) {
    this.userStore = userStore;
  }

  /**
   * This function fires when a user navigates to the activity feed page. It gets a list of all
   * current messages and forwards them to activityFeed.jsp.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    User loggedInUser = userStore.getUser((String) request.getSession().getAttribute("user"));
    List<Activity> activitiesPermitted =
        loggedInUser == null
            ? activityStore.getAllPublicActivities()
            : activityStore.getAllPermittedActivitiesSorted(loggedInUser.getId());
    List<Activity> activities = activityStore.getActivitiesPerPrivacy(loggedInUser, activitiesPermitted);
    request.setAttribute("activities", activities);
    request.getRequestDispatcher("/WEB-INF/view/activityFeed.jsp").forward(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String button = request.getParameter("button");

    if ("personalizeActivities".equals(button)) {
      response.sendRedirect("/personalActivityFeed");
    }
  }
}
