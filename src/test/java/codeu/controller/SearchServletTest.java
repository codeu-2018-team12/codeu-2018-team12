package codeu.controller;

import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SearchServletTest {

  private SearchServlet searchServlet;
  private HttpServletRequest mockRequest;
  private HttpSession mockSession;
  private HttpServletResponse mockResponse;
  private RequestDispatcher mockRequestDispatcher;
  private UserStore mockUserStore;
  private ConversationStore mockConversationStore;
  private MessageStore mockMessageStore;

  @Before
  public void setup() {
    searchServlet = new SearchServlet();

    mockRequest = Mockito.mock(HttpServletRequest.class);
    mockSession = Mockito.mock(HttpSession.class);
    Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

    mockResponse = Mockito.mock(HttpServletResponse.class);
    mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
    Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/search.jsp"))
        .thenReturn(mockRequestDispatcher);

    mockUserStore = Mockito.mock(UserStore.class);
    searchServlet.setUserStore(mockUserStore);
    mockConversationStore = Mockito.mock(ConversationStore.class);
    searchServlet.setConversationStore(mockConversationStore);
    mockMessageStore = Mockito.mock(MessageStore.class);
    searchServlet.setMessageStore(mockMessageStore);
  }

  @Test
  public void testDoGetUser() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("searchuser")).thenReturn("te");
    User testUser =
        new User(
            UUID.randomUUID(),
            "test_user",
            "password",
            "test biography",
            Instant.now(),
            "codeUChatTestEmail@gmail.com");

    ArrayList<User> users = new ArrayList<User>();
    users.add(testUser);

    Mockito.when(mockUserStore.searchUsersSorted("te")).thenReturn(users);

    searchServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("users", users);
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoGetConversation() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("searchconvo")).thenReturn("test");
    List<Conversation> fakeConversationList = new ArrayList<>();
    fakeConversationList.add(
        new Conversation(UUID.randomUUID(), UUID.randomUUID(), "test_conversation", Instant.now()));
    Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("testuser");
    User testUser = new User(UUID.randomUUID(), "testuser", null, null, Instant.now(), null);
    Mockito.when(mockUserStore.getUser("testuser")).thenReturn(testUser);
    Mockito.when(mockConversationStore.getAllPermittedConversations(testUser.getId()))
        .thenReturn(fakeConversationList);

    searchServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("conversations", fakeConversationList);
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoGetMessage() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("searchmessage")).thenReturn("test");
    Conversation fakeConversation =
        new Conversation(UUID.randomUUID(), UUID.randomUUID(), "test_conversation", Instant.now());
    Mockito.when(mockRequest.getParameter("searchbutton")).thenReturn("test_conversation");
    Mockito.when(mockConversationStore.getConversationWithTitle("test_conversation"))
        .thenReturn(fakeConversation);
    List<Message> fakeMessageList = new ArrayList<Message>();
    fakeMessageList.add(
        new Message(
            UUID.randomUUID(), fakeConversation.getId(), UUID.randomUUID(), "test", Instant.now(),true));

    Mockito.when(mockMessageStore.getMessagesInConversation(fakeConversation.getId()))
        .thenReturn(fakeMessageList);

    searchServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("messages", fakeMessageList);
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }
}
