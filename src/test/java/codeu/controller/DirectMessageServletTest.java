// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.controller;

import codeu.model.data.Activity;
import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.ActivityStore;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class DirectMessageServletTest {

  private DirectMessageServlet directMessageServlet;
  private HttpServletRequest mockRequest;
  private HttpSession mockSession;
  private HttpServletResponse mockResponse;
  private RequestDispatcher mockRequestDispatcher;
  private ConversationStore mockConversationStore;
  private MessageStore mockMessageStore;
  private UserStore mockUserStore;
  private ActivityStore mockActivityStore;
  private final LocalServiceTestHelper appEngineTestHelper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setup() {
    directMessageServlet = new DirectMessageServlet();

    mockRequest = Mockito.mock(HttpServletRequest.class);
    mockSession = Mockito.mock(HttpSession.class);
    Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

    mockResponse = Mockito.mock(HttpServletResponse.class);
    mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
    Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/directMessage.jsp"))
        .thenReturn(mockRequestDispatcher);

    mockConversationStore = Mockito.mock(ConversationStore.class);
    directMessageServlet.setConversationStore(mockConversationStore);

    mockMessageStore = Mockito.mock(MessageStore.class);
    directMessageServlet.setMessageStore(mockMessageStore);

    mockUserStore = Mockito.mock(UserStore.class);
    directMessageServlet.setUserStore(mockUserStore);

    mockActivityStore = Mockito.mock(ActivityStore.class);
    directMessageServlet.setActivityStore(mockActivityStore);
    appEngineTestHelper.setUp();
  }

  @Test
  public void testDoGet() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/direct/test_user");
    Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("test_user2");
    User testloggedInUser =
        new User(UUID.randomUUID(), "test_user2", null, null, Instant.now(), null);
    Mockito.when(mockUserStore.getUser("test_user2")).thenReturn(testloggedInUser);
    User testUser =
        new User(
            UUID.randomUUID(),
            "test_user",
            "password",
            null,
            Instant.now(),
            "codeUChatTestEmail@gmail.com");
    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(testUser);
    UUID fakeConversationId = UUID.randomUUID();
    Conversation fakeConversation =
        new Conversation(fakeConversationId, UUID.randomUUID(), "test_conversation", Instant.now());
    Mockito.when(mockConversationStore.getConversationWithTitle("direct:test_user-test_user2"))
        .thenReturn(fakeConversation);

    List<Message> fakeMessageList = new ArrayList<>();
    fakeMessageList.add(
        new Message(
            UUID.randomUUID(),
            fakeConversationId,
            UUID.randomUUID(),
            "test message",
            Instant.now()));
    Mockito.when(mockMessageStore.getMessagesInConversation(fakeConversationId))
        .thenReturn(fakeMessageList);

    directMessageServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("loggedInUser", testloggedInUser);
    Mockito.verify(mockRequest).setAttribute("otherUser", testUser);
    Mockito.verify(mockRequest).setAttribute("conversation", fakeConversation);
    Mockito.verify(mockRequest).setAttribute("messages", fakeMessageList);
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoGet_BadUser() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/direct/bad_user");
    Mockito.when(mockUserStore.getUser("bad_user")).thenReturn(null);
    Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("test_user2");
    User testloggedInUser =
        new User(UUID.randomUUID(), "test_user2", null, null, Instant.now(), null);
    Mockito.when(mockUserStore.getUser("test_user2")).thenReturn(testloggedInUser);

    directMessageServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockResponse).sendRedirect("/conversations");
  }

  @Test
  public void testDoGet_SameUser() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/direct/test_user");
    User testUser = new User(UUID.randomUUID(), "test_user", null, null, Instant.now(), null);
    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(testUser);
    Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("test_user");

    directMessageServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockResponse).sendRedirect("/conversations");
  }

  @Test
  public void testDoPost_SameUser() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/direct/test_user");
    User testUser = new User(UUID.randomUUID(), "test_user", null, null, Instant.now(), null);
    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(testUser);
    Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("test_user");

    directMessageServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockResponse).sendRedirect("/conversations");
  }

  @Test
  public void testDoGet_UserNotLoggedIn() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/direct/bad_user");
    Mockito.when(mockUserStore.getUser("bad_user")).thenReturn(null);
    Mockito.when(mockSession.getAttribute("user")).thenReturn(null);

    directMessageServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockMessageStore, Mockito.never()).addMessage(Mockito.any(Message.class));
    Mockito.verify(mockResponse).sendRedirect("/login");
  }

  @Test
  public void testDoPost_UserNotLoggedIn() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/direct/bad_user");
    Mockito.when(mockUserStore.getUser("bad_user")).thenReturn(null);
    Mockito.when(mockSession.getAttribute("user")).thenReturn(null);

    directMessageServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockMessageStore, Mockito.never()).addMessage(Mockito.any(Message.class));
    Mockito.verify(mockResponse).sendRedirect("/login");
  }

  @Test
  public void testDoGet_ConversationNotFound() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/direct/test_user");
    Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("test_user2");
    User testloggedInUser =
        new User(UUID.randomUUID(), "test_user2", null, null, Instant.now(), null);
    Mockito.when(mockUserStore.getUser("test_user2")).thenReturn(testloggedInUser);
    User testUser =
        new User(
            UUID.randomUUID(),
            "test_user",
            "password",
            null,
            Instant.now(),
            "codeUChatTestEmail@gmail.com");
    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(testUser);
    UUID fakeConversationId = UUID.randomUUID();
    Mockito.when(mockConversationStore.getConversationWithTitle("direct:test_user-test_user2"))
        .thenReturn(null);

    directMessageServlet.doGet(mockRequest, mockResponse);

    ArgumentCaptor<Conversation> conversationArgumentCaptor =
        ArgumentCaptor.forClass(Conversation.class);
    Mockito.verify(mockConversationStore).addConversation(conversationArgumentCaptor.capture());
    Assert.assertEquals(
        conversationArgumentCaptor.getValue().getTitle(), "direct:test_user-test_user2");

    Mockito.verify(mockRequest).setAttribute("loggedInUser", testloggedInUser);
    Mockito.verify(mockRequest).setAttribute("otherUser", testUser);
    Mockito.verify(mockRequest).setAttribute("conversation", conversationArgumentCaptor.getValue());
    Mockito.verify(mockRequest).setAttribute("messages", new ArrayList<Message>());
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoPost() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/direct/test_user");
    Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("test_user2");
    User testloggedInUser =
        new User(UUID.randomUUID(), "test_user2", null, null, Instant.now(), null);
    Mockito.when(mockUserStore.getUser("test_user2")).thenReturn(testloggedInUser);
    User testUser =
        new User(
            UUID.randomUUID(),
            "test_user",
            "password",
            null,
            Instant.now(),
            "codeUChatTestEmail@gmail.com");
    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(testUser);

    Conversation fakeConversation =
        new Conversation(
            UUID.randomUUID(), UUID.randomUUID(), "direct:test_user-test_user2", Instant.now());
    Mockito.when(mockConversationStore.getConversationWithTitle("direct:test_user-test_user2"))
        .thenReturn(fakeConversation);

    fakeConversation.getConversationUsers().add(testloggedInUser.getId());

    Mockito.when(mockRequest.getParameter("message")).thenReturn("Test message.");

    directMessageServlet.doPost(mockRequest, mockResponse);

    ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
    Mockito.verify(mockMessageStore).addMessage(messageArgumentCaptor.capture());
    Assert.assertEquals("Test message.", messageArgumentCaptor.getValue().getContent());

    Mockito.verify(mockResponse).sendRedirect("/direct/test_user");
  }

  @Test
  public void testDoPost_StoresActivity() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/direct/test_user");
    Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("test_user2");
    User testloggedInUser =
        new User(UUID.randomUUID(), "test_user2", null, null, Instant.now(), null);
    Mockito.when(mockUserStore.getUser("test_user2")).thenReturn(testloggedInUser);
    User testUser =
        new User(
            UUID.randomUUID(),
            "test_user",
            "password",
            null,
            Instant.now(),
            "codeUChatTestEmail@gmail.com");
    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(testUser);

    Conversation fakeConversation =
        new Conversation(
            UUID.randomUUID(), UUID.randomUUID(), "direct:test_user-test_user2", Instant.now());
    Mockito.when(mockConversationStore.getConversationWithTitle("direct:test_user-test_user2"))
        .thenReturn(fakeConversation);

    fakeConversation.getConversationUsers().add(testloggedInUser.getId());

    Mockito.when(mockRequest.getParameter("message")).thenReturn("Test message.");

    Activity activity =
        new Activity(
            UUID.randomUUID(),
            testloggedInUser.getId(),
            fakeConversation.getId(),
            Instant.now(),
            "sentMessage",
            "test_activity_message" + "Test message.",
            fakeConversation.getConversationUsers(),
            fakeConversation.getIsPublic());
    mockActivityStore.addActivity(activity);
    Mockito.when(mockActivityStore.getActivityWithId(activity.getId())).thenReturn(activity);

    directMessageServlet.doPost(mockRequest, mockResponse);

    ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
    Mockito.verify(mockMessageStore).addMessage(messageArgumentCaptor.capture());
    Assert.assertEquals("Test message.", messageArgumentCaptor.getValue().getContent());

    Mockito.verify(mockActivityStore).addActivity(activity);
    Assert.assertTrue(
        mockActivityStore
            .getActivityWithId(activity.getId())
            .getActivityMessage()
            .contains("Test message."));

    Mockito.verify(mockResponse).sendRedirect("/direct/test_user");
  }
}
