package codeu.controller;

import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

/** Listener class that fires when there is a change to an attribute of a session */
public class SessionListener implements HttpSessionAttributeListener {

  private static SessionListener currentSession;
  private Set<String> loggedInUsers = new HashSet<>();

  public static SessionListener getInstance() {
    if (currentSession == null) {
      currentSession = new SessionListener();
    }
    return currentSession;
  }

  public boolean isLoggedIn(String username) {
    return loggedInUsers.contains(username);
  }

  /** Called when an attribute is added to a session */
  @Override
  public void attributeAdded(HttpSessionBindingEvent event) {
    loggedInUsers.add(event.getValue().toString());
  }

  /** Called when an attribute is removed from a session */
  @Override
  public void attributeRemoved(HttpSessionBindingEvent event) {
    loggedInUsers.remove(event.getValue().toString());
  }

  /** Called when an attribute is replaced in a session */
  @Override
  public void attributeReplaced(HttpSessionBindingEvent event) {}
}
