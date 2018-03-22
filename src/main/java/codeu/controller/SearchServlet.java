package codeu.controller;

import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
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

  /** Set up state for handling profile requests. */
  @Override
  public void init() throws ServletException {
    super.init();
    setUserStore(UserStore.getInstance());
  }

  /**
   * Sets the UserStore used by this servlet. This function provides a common setup method for use
   * by the test framework or the servlet's init() function.
   */
  void setUserStore(UserStore userStore) {
    this.userStore = userStore;
  }

  /**
   * This function fires when a user requests the /search URL. It finds the set of users that match
   * the given string and forwards that information to search.jsp
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String search = request.getParameter("search");
    List<User> result = new ArrayList<User>();
    if (search != null) {
      result = userStore.searchUsers(search);
    }
    request.setAttribute("users", result);
    request.getRequestDispatcher("/WEB-INF/view/search.jsp").forward(request, response);
  }
}
