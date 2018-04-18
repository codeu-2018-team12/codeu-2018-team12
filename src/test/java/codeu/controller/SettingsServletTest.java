package codeu.controller;

import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.io.IOException;
import java.time.Instant;
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

public class SettingsServletTest {

  private SettingsServlet settingsServlet;
  private HttpServletRequest mockRequest;
  private HttpSession mockSession;
  private HttpServletResponse mockResponse;
  private RequestDispatcher mockRequestDispatcher;
  private UserStore mockUserStore;
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setup() {
    helper.setUp();
    settingsServlet = new SettingsServlet();

    mockRequest = Mockito.mock(HttpServletRequest.class);
    mockSession = Mockito.mock(HttpSession.class);
    Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

    mockResponse = Mockito.mock(HttpServletResponse.class);
    mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
    Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/settings.jsp"))
        .thenReturn(mockRequestDispatcher);

    mockUserStore = Mockito.mock(UserStore.class);
    settingsServlet.setUserStore(mockUserStore);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testDoGet() throws IOException, ServletException {
    User user =
        new User(
            UUID.randomUUID(),
            "test_user",
            "password",
            "test biography",
            Instant.now(),
            "codeUChatTestEmail@gmail.com");

    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_user");
    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(user);
    settingsServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("user", user);
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoPost_updateEmail() throws IOException, ServletException {
    User user =
        new User(
            UUID.randomUUID(),
            "test_user",
            "password",
            "test biography",
            Instant.now(),
            "codeUChatTestEmail@gmail.com");

    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_user");
    mockUserStore.addUser(user);
    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(user);

    Mockito.when(mockRequest.getParameter("password")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("confirmPassword")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("email")).thenReturn("codeUChatTestEmail@gmail.com");
    Mockito.when(mockRequest.getParameter("notificationStatus")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("notificationFrequency")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("profilePrivacy")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("activityFeedPrivacy")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("submit")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("submitPassword")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("submitEmail")).thenReturn("submitEmail");
    Mockito.when(mockRequest.getParameter("submitNotification")).thenReturn(null);

    settingsServlet.doPost(mockRequest, mockResponse);
    Mockito.verify(mockRequest).setAttribute("user", user);
    Mockito.verify(mockRequest)
        .setAttribute("successInfo", "You have successfully updated your information.");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoPost_updatePassword() throws IOException, ServletException {
    User user =
        new User(
            UUID.randomUUID(),
            "test_user",
            "password",
            "test biography",
            Instant.now(),
            "codeUChatTestEmail@gmail.com");

    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_user");
    mockUserStore.addUser(user);
    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(user);

    Mockito.when(mockRequest.getParameter("password")).thenReturn("testPassword123");
    Mockito.when(mockRequest.getParameter("confirmPassword")).thenReturn("testPassword123");
    Mockito.when(mockRequest.getParameter("email")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("notificationStatus")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("notificationFrequency")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("profilePrivacy")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("activityFeedPrivacy")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("submit")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("submitPassword")).thenReturn("submitPassword");
    Mockito.when(mockRequest.getParameter("submitEmail")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("submitNotification")).thenReturn(null);

    settingsServlet.doPost(mockRequest, mockResponse);
    Mockito.verify(mockRequest).setAttribute("user", user);
    Mockito.verify(mockRequest)
        .setAttribute("successInfo", "You have successfully updated your information.");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoPost_updateNotificationStatus() throws IOException, ServletException {
    User user =
        new User(
            UUID.randomUUID(),
            "test_user",
            "password",
            "test biography",
            Instant.now(),
            "codeUChatTestEmail@gmail.com");

    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_user");
    mockUserStore.addUser(user);
    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(user);

    Mockito.when(mockRequest.getParameter("password")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("confirmPassword")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("email")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("notificationStatus")).thenReturn("optIn");
    Mockito.when(mockRequest.getParameter("notificationFrequency")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("profilePrivacy")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("activityFeedPrivacy")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("submit")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("submitPassword")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("submitEmail")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("submitNotification")).thenReturn("submitNotification");

    settingsServlet.doPost(mockRequest, mockResponse);
    Mockito.verify(mockRequest).setAttribute("user", user);
    Mockito.verify(mockRequest)
        .setAttribute(
            "successNotifications", "You have successfully updated your notification status.");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoPost_updateProfilePrivacySettings() throws IOException, ServletException {
    User user =
        new User(
            UUID.randomUUID(),
            "test_user",
            "password",
            "test biography",
            Instant.now(),
            "codeUChatTestEmail@gmail.com");

    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_user");
    mockUserStore.addUser(user);
    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(user);

    Mockito.when(mockRequest.getParameter("password")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("confirmPassword")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("email")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("notificationStatus")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("notificationFrequency")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("profilePrivacy")).thenReturn("private");
    Mockito.when(mockRequest.getParameter("activityFeedPrivacy")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("submit")).thenReturn("submitProfilePrivacy");
    Mockito.when(mockRequest.getParameter("submitPassword")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("submitEmail")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("submitNotification")).thenReturn(null);

    settingsServlet.doPost(mockRequest, mockResponse);
    Mockito.verify(mockRequest).setAttribute("user", user);
    Mockito.verify(mockRequest)
        .setAttribute(
            "successPrivacy", "You have successfully updated your profile privacy settings.");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoPost_updateActivityPrivacySettings() throws IOException, ServletException {
    User user =
        new User(
            UUID.randomUUID(),
            "test_user",
            "password",
            "test biography",
            Instant.now(),
            "codeUChatTestEmail@gmail.com");

    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_user");
    mockUserStore.addUser(user);
    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(user);

    Mockito.when(mockRequest.getParameter("password")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("confirmPassword")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("email")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("notificationStatus")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("notificationFrequency")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("profilePrivacy")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("activityFeedPrivacy")).thenReturn("private");
    Mockito.when(mockRequest.getParameter("submit")).thenReturn("submitActivityFeedPrivacy");
    Mockito.when(mockRequest.getParameter("submitPassword")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("submitEmail")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("submitNotification")).thenReturn(null);

    settingsServlet.doPost(mockRequest, mockResponse);
    Mockito.verify(mockRequest).setAttribute("user", user);
    Mockito.verify(mockRequest)
        .setAttribute(
            "successPrivacy", "You have successfully updated your activity feed privacy settings.");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoPost_newPasswordUnderMinLength() throws IOException, ServletException {
    User user =
        new User(
            UUID.randomUUID(),
            "test_user",
            "password",
            "test biography",
            Instant.now(),
            "codeUChatTestEmail@gmail.com");
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_user");
    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(user);
    Mockito.when(mockRequest.getParameter("password")).thenReturn("pass");

    settingsServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("user", user);
    Mockito.verify(mockRequest)
        .setAttribute("error", "Please enter a password that is at least 8 characters.");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoPost_emptyEmailAndPassword() throws IOException, ServletException {
    User user =
        new User(
            UUID.randomUUID(),
            "test_user",
            "password",
            "test biography",
            Instant.now(),
            "codeUChatTestEmail@gmail.com");
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_user");
    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(user);
    Mockito.when(mockRequest.getParameter("confirmPassword")).thenReturn("");
    Mockito.when(mockRequest.getParameter("email")).thenReturn("");

    settingsServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("user", user);
    Mockito.verify(mockRequest)
        .setAttribute(
            "error", "Please enter information for at least one field before submitting.");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoPost_emptyPassword() throws IOException, ServletException {
    User user =
        new User(
            UUID.randomUUID(),
            "test_user",
            "password",
            "test biography",
            Instant.now(),
            "codeUChatTestEmail@gmail.com");
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_user");
    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(user);

    Mockito.when(mockRequest.getParameter("confirmPassword")).thenReturn("");
    Mockito.when(mockRequest.getParameter("email")).thenReturn(null);

    settingsServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("user", user);
    Mockito.verify(mockRequest)
        .setAttribute("error", "Please enter a password that is at least 8 characters.");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoPost_passwordAndConfPasswordMismatch() throws IOException, ServletException {
    User user =
        new User(
            UUID.randomUUID(),
            "test_user",
            "password",
            "test biography",
            Instant.now(),
            "codeUChatTestEmail@gmail.com");
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_user");
    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(user);

    Mockito.when(mockRequest.getParameter("password")).thenReturn("testPassword");
    Mockito.when(mockRequest.getParameter("confirmPassword")).thenReturn("testPassword123");
    Mockito.when(mockRequest.getParameter("email")).thenReturn(null);

    settingsServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("user", user);
    Mockito.verify(mockRequest)
        .setAttribute("error", "Your password and confirmation password do not match.");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }
}
