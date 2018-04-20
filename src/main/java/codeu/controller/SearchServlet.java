package codeu.controller;

import codeu.model.data.Conversation;
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.UserStore;
import codeu.utils.Filterer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet class responsible for the logout page. */
public class SearchServlet extends HttpServlet {

  /** Store class that gives access to Users. */
  private UserStore userStore;

  private ConversationStore conversationStore;

  /** Set up state for handling profile requests. */
  @Override
  public void init() throws ServletException {
    super.init();
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

  void setConversationStore(ConversationStore conversationStore) {
    this.conversationStore = conversationStore;
  }

  /**
   * This function fires when a user requests the /search URL. It finds the set of users that match
   * the given string and forwards that information to search.jsp
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    User loggedInUser = userStore.getUser((String) request.getSession().getAttribute("user"));
    String search = null;
    if (request.getParameter("searchuser") != null) {
      search = request.getParameter("searchuser");
      List<User> result = new ArrayList<User>();
      if (search != null) {
        result = userStore.searchUsersSorted(search);
      }
      request.setAttribute("users", result);
    } else if (request.getParameter("searchconvo") != null) {
      search = request.getParameter("searchconvo");
      List<Conversation> conversations =
          loggedInUser == null
              ? conversationStore.getAllPublicConversationsSorted()
              : conversationStore.getAllPermittedConversationsSorted(loggedInUser.getId());
      try {
        List<Conversation> result = Filterer.filterConversations(conversations, search);
        request.setAttribute("conversations", result);
      } catch (Exception e) {
        System.out.println("Exception: " + e);
        request.setAttribute("conversations", new ArrayList<Conversation>());
      }
    }
    request.getRequestDispatcher("/WEB-INF/view/search.jsp").forward(request, response);
  }
}
