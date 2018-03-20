package codeu.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet class responsible for the logout page. */
public class LogoutServlet extends HttpServlet {

  /**
   * This function fires when a user requests the /logout URL. It simply logs out the user and
   * redirects the user to the login page.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    request.getSession().setAttribute("user", null);
    response.sendRedirect("/login");
  }
}
