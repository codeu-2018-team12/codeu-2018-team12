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
import java.util.List;
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

public class PersonalActivityServletTest {
  private PersonalActivityServlet personalActivityServlet;
  private HttpSession mockSession;
  private HttpServletRequest mockRequest;
  private HttpServletResponse mockResponse;
  private RequestDispatcher mockRequestDispatcher;
  private ActivityStore mockActivityStore;
  private UserStore mockUserStore;
  private ConversationStore mockConversationStore;
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setup() {
    personalActivityServlet = new PersonalActivityServlet();

    mockRequest = Mockito.mock(HttpServletRequest.class);
    mockSession = Mockito.mock(HttpSession.class);
    Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

    mockResponse = Mockito.mock(HttpServletResponse.class);
    mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
    Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/personalActivityFeed.jsp"))
        .thenReturn(mockRequestDispatcher);

    mockActivityStore = Mockito.mock(ActivityStore.class);
    personalActivityServlet.setActivityStore(mockActivityStore);

    mockUserStore = Mockito.mock(UserStore.class);
    personalActivityServlet.setUserStore(mockUserStore);

    mockConversationStore = Mockito.mock(ConversationStore.class);
    personalActivityServlet.setConversationStore(mockConversationStore);

    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");

    helper.setUp();
  }

  @Test
  public void testDoGet() throws IOException, ServletException {

    UUID fakeUserID = UUID.randomUUID();
    UUID fakeConversationID = UUID.randomUUID();

    User fakeUser =
        new User(
            fakeUserID,
            "test_username",
            "password",
            "test biography",
            Instant.now(),
            "test@gmail.com");
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);

    List<Activity> sampleActivities = new ArrayList<>();
    sampleActivities.add(
        new Activity(
            UUID.randomUUID(),
            fakeUserID,
            fakeConversationID,
            Instant.ofEpochMilli(2000),
            "joinedApp",
            "testMessage",
            new ArrayList<UUID>(),
            true));

    sampleActivities.add(
        new Activity(
            UUID.randomUUID(),
            fakeUserID,
            fakeConversationID,
            Instant.ofEpochMilli(1000),
            "createdConvo",
            "testMessage",
            new ArrayList<UUID>(),
            true));

    List<Activity> privateSampleActivities = mockActivityStore.getActivitiesPerPrivacy(fakeUser, sampleActivities);
    List<Activity> sortedSampleActivities =
        mockActivityStore.getActivityListSorted(privateSampleActivities);
    personalActivityServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("activities", sortedSampleActivities);
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}
