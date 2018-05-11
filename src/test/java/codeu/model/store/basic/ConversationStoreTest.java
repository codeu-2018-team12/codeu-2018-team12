package codeu.model.store.basic;

import static codeu.model.store.basic.ConversationStore.sort;

import codeu.model.data.Conversation;
import codeu.model.data.User;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ConversationStoreTest {

  private ConversationStore conversationStore;
  private PersistentStorageAgent mockPersistentStorageAgent;

  private final User USER_ONE =
      new User(UUID.randomUUID(), "user_one", null, null, Instant.ofEpochMilli(1000), null);
  private final User USER_TWO =
      new User(UUID.randomUUID(), "user_two", null, null, Instant.ofEpochMilli(2000), null);

  private Conversation CONVERSATION_ONE =
      new Conversation(
          UUID.randomUUID(),
          UUID.randomUUID(),
          "conversation_one",
          Instant.ofEpochMilli(1000),
          true);
  private final Conversation CONVERSATION_TWO =
      new Conversation(
          UUID.randomUUID(),
          USER_TWO.getId(),
          "conversation_two",
          Instant.ofEpochMilli(2000),
          false);

  @Before
  public void setup() {
    mockPersistentStorageAgent = Mockito.mock(PersistentStorageAgent.class);
    conversationStore = ConversationStore.getTestInstance(mockPersistentStorageAgent);

    final List<Conversation> conversationList = new ArrayList<>();
    conversationList.add(CONVERSATION_ONE);
    conversationList.add(CONVERSATION_TWO);
    conversationStore.setConversations(conversationList);
  }

  @Test
  public void testGetConversationWithTitle_found() {
    Conversation resultConversation =
        conversationStore.getConversationWithTitle(CONVERSATION_ONE.getTitle());

    assertEquals(CONVERSATION_ONE, resultConversation);
  }

  @Test
  public void testGetConversationWithTitle_notFound() {
    Conversation resultConversation = conversationStore.getConversationWithTitle("unfound_title");

    Assert.assertNull(resultConversation);
  }

  @Test
  public void testGetConversationWithId_found() {
    Conversation resultConversation =
        conversationStore.getConversationWithId(CONVERSATION_ONE.getId());

    assertEquals(CONVERSATION_ONE, resultConversation);
  }

  @Test
  public void testGetConversationWithId_notFound() {
    Conversation resultConversation = conversationStore.getConversationWithId(UUID.randomUUID());

    Assert.assertNull(resultConversation);
  }

  @Test
  public void testGetAllPermittedConversationsSorted_Permitted() {
    List<Conversation> permittedConversations =
        sort(conversationStore.getAllPermittedConversations(USER_TWO.getId()));
    Assert.assertEquals(2, permittedConversations.size());
    assertEquals(CONVERSATION_TWO, permittedConversations.get(0));
    assertEquals(CONVERSATION_ONE, permittedConversations.get(1));
  }

  @Test
  public void testGetAllPermittedConversationsSorted_NotPermitted() {
    List<Conversation> permittedConversations =
        sort(conversationStore.getAllPermittedConversations(USER_ONE.getId()));
    Assert.assertEquals(1, permittedConversations.size());
    assertEquals(CONVERSATION_ONE, permittedConversations.get(0));
  }

  @Test
  public void testIsTitleTaken_true() {
    boolean isTitleTaken = conversationStore.isTitleTaken(CONVERSATION_ONE.getTitle());

    Assert.assertTrue(isTitleTaken);
  }

  @Test
  public void testIsTitleTaken_false() {
    boolean isTitleTaken = conversationStore.isTitleTaken("unfound_title");

    Assert.assertFalse(isTitleTaken);
  }

  @Test
  public void testAddConversation() {
    Conversation inputConversation =
        new Conversation(UUID.randomUUID(), UUID.randomUUID(), "test_conversation", Instant.now());

    conversationStore.addConversation(inputConversation);
    Conversation resultConversation =
        conversationStore.getConversationWithTitle("test_conversation");

    assertEquals(inputConversation, resultConversation);
    Mockito.verify(mockPersistentStorageAgent).writeThrough(inputConversation);
  }

  private void assertEquals(Conversation expectedConversation, Conversation actualConversation) {
    Assert.assertEquals(expectedConversation.getId(), actualConversation.getId());
    Assert.assertEquals(expectedConversation.getOwnerId(), actualConversation.getOwnerId());
    Assert.assertEquals(expectedConversation.getTitle(), actualConversation.getTitle());
    Assert.assertEquals(
        expectedConversation.getCreationTime(), actualConversation.getCreationTime());
  }
}
