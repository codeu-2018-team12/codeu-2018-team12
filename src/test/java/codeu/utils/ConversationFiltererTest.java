package codeu.utils;

import codeu.model.data.Conversation;
import codeu.model.data.User;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConversationFiltererTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private ConversationFilterer filterer;

  private final DateTimeFormatter FORMATTER =
      new DateTimeFormatterBuilder()
          .appendPattern("MM-dd-yyyy")
          .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
          .toFormatter()
          .withZone(ZoneId.systemDefault());

  private final Conversation CONVERSATION_ONE =
      new Conversation(
          UUID.randomUUID(),
          UUID.randomUUID(),
          "conversation_one",
          ZonedDateTime.parse("04-24-2018", FORMATTER).toInstant(),
          true);

  private final Conversation CONVERSATION_TWO =
      new Conversation(
          UUID.randomUUID(),
          UUID.randomUUID(),
          "conversation_two",
          ZonedDateTime.parse("04-26-2018", FORMATTER).toInstant(),
          true);

  private final Conversation CONVERSATION_THREE =
      new Conversation(
          UUID.randomUUID(),
          UUID.randomUUID(),
          "conversation_three",
          ZonedDateTime.parse("04-25-2018", FORMATTER).toInstant(),
          true);

  private final User USER_ONE =
      new User(UUID.randomUUID(), "one", null, null, Instant.ofEpochMilli(1000), null);

  private final User USER_TWO =
      new User(UUID.randomUUID(), "two", null, null, Instant.ofEpochMilli(1000), null);

  @Before
  public void setup() {
    helper.setUp();
    ArrayList<Conversation> convos = new ArrayList<Conversation>();
    CONVERSATION_ONE.addUser(USER_ONE.getId());
    CONVERSATION_THREE.addUser(USER_ONE.getId());
    CONVERSATION_ONE.addUser(USER_TWO.getId());
    CONVERSATION_TWO.addUser(USER_TWO.getId());
    convos.add(CONVERSATION_ONE);
    convos.add(CONVERSATION_TWO);
    convos.add(CONVERSATION_THREE);
    ArrayList<User> users = new ArrayList<User>();
    users.add(USER_ONE);
    users.add(USER_TWO);
    filterer = new ConversationFilterer(convos, users);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testFilterConvosDate() {
    List<Conversation> res1 = filterer.filterConversations("before:04-25-2018");
    Assert.assertEquals(1, res1.size());
    assertEquals(res1.get(0), CONVERSATION_ONE);

    List<Conversation> res2 = filterer.filterConversations("after:04-25-2018");
    Assert.assertEquals(1, res2.size());
    assertEquals(res2.get(0), CONVERSATION_TWO);

    List<Conversation> res3 = filterer.filterConversations("on:04-25-2018");
    Assert.assertEquals(1, res3.size());
    assertEquals(res3.get(0), CONVERSATION_THREE);
  }

  @Test
  public void testFilterConvosTitle() {
    List<Conversation> res1 = filterer.filterConversations("one");
    Assert.assertEquals(1, res1.size());
    assertEquals(res1.get(0), CONVERSATION_ONE);

    List<Conversation> res2 = filterer.filterConversations("two");
    Assert.assertEquals(1, res2.size());
    assertEquals(res2.get(0), CONVERSATION_TWO);

    List<Conversation> res3 = filterer.filterConversations("three");
    Assert.assertEquals(1, res3.size());
    assertEquals(res3.get(0), CONVERSATION_THREE);

    List<Conversation> res4 = filterer.filterConversations("conversation");
    Assert.assertEquals(3, res4.size());
    assertEquals(res4.get(0), CONVERSATION_TWO);
    assertEquals(res4.get(1), CONVERSATION_THREE);
    assertEquals(res4.get(2), CONVERSATION_ONE);
  }

  @Test
  public void testFilterConvosMember() {
    List<Conversation> res1 = filterer.filterConversations("with:one");
    Assert.assertEquals(2, res1.size());
    assertEquals(CONVERSATION_THREE, res1.get(0));
    assertEquals(CONVERSATION_ONE, res1.get(1));

    List<Conversation> res2 = filterer.filterConversations("with:two");
    Assert.assertEquals(2, res2.size());
    assertEquals(CONVERSATION_TWO, res2.get(0));
    assertEquals(CONVERSATION_ONE, res2.get(1));
  }

  @Test
  public void testFilterConvosAnd() {
    List<Conversation> res1 = filterer.filterConversations("with:one AND with:two");
    Assert.assertEquals(1, res1.size());
    assertEquals(CONVERSATION_ONE, res1.get(0));

    List<Conversation> res2 = filterer.filterConversations("with:two AND two");
    Assert.assertEquals(1, res2.size());
    assertEquals(CONVERSATION_TWO, res2.get(0));

    List<Conversation> res3 = filterer.filterConversations("two AND with:two");
    Assert.assertEquals(1, res3.size());
    assertEquals(CONVERSATION_TWO, res3.get(0));
  }

  @Test
  public void testFilterConvosOr() {
    List<Conversation> res1 = filterer.filterConversations("with:one OR with:two");
    Assert.assertEquals(3, res1.size());
    assertEquals(CONVERSATION_TWO, res1.get(0));
    assertEquals(CONVERSATION_THREE, res1.get(1));
    assertEquals(CONVERSATION_ONE, res1.get(2));

    List<Conversation> res2 = filterer.filterConversations("with:two OR with:one");
    Assert.assertEquals(3, res2.size());
    assertEquals(CONVERSATION_TWO, res2.get(0));
    assertEquals(CONVERSATION_THREE, res2.get(1));
    assertEquals(CONVERSATION_ONE, res2.get(2));

    List<Conversation> res3 = filterer.filterConversations("not_a_name OR before:04-28-2018");
    Assert.assertEquals(3, res2.size());
    assertEquals(CONVERSATION_TWO, res2.get(0));
    assertEquals(CONVERSATION_THREE, res2.get(1));
    assertEquals(CONVERSATION_ONE, res2.get(2));
  }

  @Test
  public void testFilterConvosChain() {
    List<Conversation> res1 =
        filterer.filterConversations("(with:one OR with:two) AND before:04-26-2018");
    Assert.assertEquals(2, res1.size());
    assertEquals(CONVERSATION_THREE, res1.get(0));
    assertEquals(CONVERSATION_ONE, res1.get(1));

    List<Conversation> res2 =
        filterer.filterConversations("(conversation AND with:one) OR before:04-27-2018");
    Assert.assertEquals(3, res2.size());
    assertEquals(CONVERSATION_TWO, res2.get(0));
    assertEquals(CONVERSATION_THREE, res2.get(1));
    assertEquals(CONVERSATION_ONE, res2.get(2));
  }

  private void assertEquals(Conversation expectedConversation, Conversation actualConversation) {
    Assert.assertEquals(expectedConversation.getId(), actualConversation.getId());
    Assert.assertEquals(expectedConversation.getOwnerId(), actualConversation.getOwnerId());
    Assert.assertEquals(expectedConversation.getTitle(), actualConversation.getTitle());
    Assert.assertEquals(
        expectedConversation.getCreationTime(), actualConversation.getCreationTime());
  }
}
