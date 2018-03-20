package codeu.controller;

import java.util.List;
import java.util.ArrayList;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.data.Conversation;
import codeu.model.store.basic.UserStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.ConversationStore;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet class responsible for activity feed. */
public class ActivityServlet extends HttpServlet {

  /** Store class that gives access to users. */
  private UserStore userStore;

  /** Store class that gives access to messages */
  private MessageStore messageStore;

  /** Store class that gives access to conversations */
  private ConversationStore conversationStore;

  /** Set up state for handling activity-related requests */
  @Override
  public void init() throws ServletException {
    super.init();
    setUserStore(UserStore.getInstance());
    setMessageStore(MessageStore.getInstance());
    setConversationStore(ConversationStore.getInstance());
  }

  /**
   * Sets the UserStore used by this servlet. This function provides a common setup method for use
   * by the test framework or the servlet's init() function.
   */
  void setUserStore(UserStore userStore) {
    this.userStore = userStore;
  }

  /**
   * Sets the MessageStore used by this servlet. This function provides a common setup method for
   * use by the test framework or the servlet's init() function.
   */
  void setMessageStore(MessageStore messageStore) {
    this.messageStore = messageStore;
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

    List<Message> messages = messageStore.getAllMessages();
    request.setAttribute("messages", messages);

    request.getRequestDispatcher("/WEB-INF/view/activityFeed.jsp").forward(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {}
}
