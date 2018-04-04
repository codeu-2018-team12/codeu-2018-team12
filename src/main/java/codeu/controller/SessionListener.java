package codeu.controller;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Set;
import java.util.HashSet;

/**
 * Listener class that fires when a new session is created or destroyed
 */
public class SessionListener implements HttpSessionListener {

  private Set<String> loggedInUsers = new HashSet<>();

  public Set getOnlineUsers(){
    return loggedInUsers;
  }

  /** Called when a new session is created */
  @Override
  public void sessionCreated(HttpSessionEvent event) {
    HttpSession session = event.getSession();
    loggedInUsers.add(session.getAttribute("user").toString());
  }

  /** Called when a session is destroyed, i.e. During
   * a session timeout or a call to session.invalidate()
   */
  @Override
  public void sessionDestroyed(HttpSessionEvent event) {
    HttpSession session = event.getSession();
    loggedInUsers.remove(session.getAttribute("user").toString());
  }
}
