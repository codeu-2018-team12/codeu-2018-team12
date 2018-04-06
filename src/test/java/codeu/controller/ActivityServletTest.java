package codeu.controller;

import codeu.model.data.Activity;
import codeu.model.data.User;
import codeu.model.store.basic.ActivityStore;
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

public class ActivityServletTest {

  private ActivityServlet activityServlet;
  private HttpSession mockSession;
  private HttpServletRequest mockRequest;
  private HttpServletResponse mockResponse;
  private RequestDispatcher mockRequestDispatcher;
  private ActivityStore mockActivityStore;
  private UserStore mockUserStore;

  @Before
  public void setup() {
    activityServlet = new ActivityServlet();

    mockRequest = Mockito.mock(HttpServletRequest.class);
    mockSession = Mockito.mock(HttpSession.class);
    Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

    mockResponse = Mockito.mock(HttpServletResponse.class);
    mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
    Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/activityFeed.jsp"))
        .thenReturn(mockRequestDispatcher);

    mockActivityStore = Mockito.mock(ActivityStore.class);
    mockUserStore = Mockito.mock(UserStore.class);
    activityServlet.setUserStore(mockUserStore);
    activityServlet.setActivityStore(mockActivityStore);
  }

  @Test
  public void testDoGet() throws IOException, ServletException {

    List<Activity> sampleActivities = new ArrayList<>();
    sampleActivities.add(
        new Activity(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            Instant.ofEpochMilli(2000),
            "joinedApp",
            "testMessage",
            new ArrayList<UUID>(),
            true));

    sampleActivities.add(
        new Activity(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            Instant.ofEpochMilli(1000),
            "createdConvo",
            "testMessage",
            new ArrayList<UUID>(),
            true));
    Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("testuser");
    User testUser = new User(UUID.randomUUID(), "testuser", null, null, Instant.now(), null);
    Mockito.when(mockUserStore.getUser("testuser")).thenReturn(testUser);
    Mockito.when(mockActivityStore.getAllPermittedActivitiesSorted(testUser.getId()))
        .thenReturn(sampleActivities);

    activityServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("activities", sampleActivities);
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }
}
