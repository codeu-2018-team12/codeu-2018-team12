package codeu.controller;

import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
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
  }

  @Test
  public void testDoGet() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("search")).thenReturn("te");
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
}
