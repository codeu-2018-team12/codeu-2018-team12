package codeu.controller;

import codeu.model.data.Activity;
import codeu.model.data.User;
import codeu.model.store.basic.ActivityStore;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.UserStore;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static codeu.model.store.basic.ActivityStore.sort;

public class ProfileServletTest {

  private ProfileServlet profileServlet;
  private HttpServletRequest mockRequest;
  private HttpSession mockSession;
  private HttpServletResponse mockResponse;
  private RequestDispatcher mockRequestDispatcher;
  private ConversationStore mockConversationStore;
  private ActivityStore mockActivityStore;
  private UserStore mockUserStore;
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setup() {
    helper.setUp();
    profileServlet = new ProfileServlet();

    mockRequest = Mockito.mock(HttpServletRequest.class);
    mockSession = Mockito.mock(HttpSession.class);
    Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

    mockResponse = Mockito.mock(HttpServletResponse.class);
    mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
    Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/profile.jsp"))
        .thenReturn(mockRequestDispatcher);

    mockConversationStore = Mockito.mock(ConversationStore.class);
    profileServlet.setConversationStore(mockConversationStore);

    mockActivityStore = Mockito.mock(ActivityStore.class);
    profileServlet.setActivityStore(mockActivityStore);

    mockUserStore = Mockito.mock(UserStore.class);
    profileServlet.setUserStore(mockUserStore);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testDoGet() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/profile/test_user");
    Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("testuser");
    User testloggedInUser =
        new User(UUID.randomUUID(), "testuser", null, null, Instant.now(), null);
    Mockito.when(mockUserStore.getUser("testuser")).thenReturn(testloggedInUser);
    User testUser =
        new User(
            UUID.randomUUID(),
            "test_user",
            "password",
            null,
            Instant.now(),
            "codeUChatTestEmail@gmail.com");
    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(testUser);
    ArrayList<Activity> activities = new ArrayList<Activity>();
    Activity activityOne =
        new Activity(
            UUID.randomUUID(),
            testUser.getId(),
            UUID.randomUUID(),
            Instant.ofEpochMilli(2000),
            "leftConvo",
            "test_message",
            new ArrayList<UUID>(),
            false);
    Activity activityTwo =
        new Activity(
            UUID.randomUUID(),
            testUser.getId(),
            UUID.randomUUID(),
            Instant.ofEpochMilli(1000),
            "leftConvo",
            "test_message",
            new ArrayList<UUID>(),
            true);
    activities.add(activityOne);
    activities.add(activityTwo);
    Mockito.when(
            sort(mockActivityStore.getAllPermittedActivitiesWithUserId(
                testUser.getId(), testloggedInUser.getId())))
        .thenReturn(activities);
    Mockito.when(mockActivityStore.getActivitiesPerPrivacy(testUser, activities))
        .thenReturn(activities);

    profileServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("user", testUser);
    Mockito.verify(mockRequest).setAttribute("activities", activities);
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoGet_BadUser() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/profile/test_user");

    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(null);

    profileServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("user", null);
    Mockito.verify(mockRequest).setAttribute("activities", null);
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }
}
