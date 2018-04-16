package codeu.controller;

import com.google.appengine.api.mail.BounceNotification;
import com.google.appengine.api.mail.BounceNotificationParser;

import java.io.IOException;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BounceHandlerServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(BounceHandlerServlet.class.getName());

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      BounceNotification bounce = BounceNotificationParser.parse(req);
      if (bounce.getOriginal() != null) {
        log.warning(
            "The email from "
                + bounce.getOriginal().getFrom()
                + " to "
                + bounce.getOriginal().getTo()
                + " has been bounced.");
      }
    } catch (MessagingException e) {
      System.out.println("A messaging exception has occurred.");
    }
  }
}
