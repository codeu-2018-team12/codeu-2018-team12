package codeu.utils;

import codeu.model.data.Message;
import codeu.model.data.User;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MessageFiltererTest {

  private MessageFilterer filterer;

  private final DateTimeFormatter FORMATTER =
      new DateTimeFormatterBuilder()
          .appendPattern("MM-dd-yyyy")
          .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
          .toFormatter()
          .withZone(ZoneId.systemDefault());

  private final User USER_ONE =
      new User(UUID.randomUUID(), "one", null, null, Instant.ofEpochMilli(1000), null);

  private final User USER_TWO =
      new User(UUID.randomUUID(), "two", null, null, Instant.ofEpochMilli(1000), null);

  private final Message MESSAGE_ONE =
      new Message(
          UUID.randomUUID(),
          UUID.randomUUID(),
          USER_ONE.getId(),
          "message one",
          ZonedDateTime.parse("04-24-2018", FORMATTER).toInstant());

  private final Message MESSAGE_TWO =
      new Message(
          UUID.randomUUID(),
          UUID.randomUUID(),
          USER_TWO.getId(),
          "message two",
          ZonedDateTime.parse("04-26-2018", FORMATTER).toInstant());

  private final Message MESSAGE_THREE =
      new Message(
          UUID.randomUUID(),
          UUID.randomUUID(),
          USER_ONE.getId(),
          "message three",
          ZonedDateTime.parse("04-25-2018", FORMATTER).toInstant());

  @Before
  public void setup() {
    ArrayList<Message> messages = new ArrayList<Message>();
    messages.add(MESSAGE_ONE);
    messages.add(MESSAGE_TWO);
    messages.add(MESSAGE_THREE);
    ArrayList<User> users = new ArrayList<User>();
    users.add(USER_ONE);
    users.add(USER_TWO);
    filterer = new MessageFilterer(messages, users);
  }

  @Test
  public void testFilterMessagesDate() {
    List<Message> res1 = filterer.filterMessages("before:04-25-2018");
    Assert.assertEquals(1, res1.size());
    assertEquals(res1.get(0), MESSAGE_ONE);

    List<Message> res2 = filterer.filterMessages("after:04-25-2018");
    Assert.assertEquals(1, res2.size());
    assertEquals(res2.get(0), MESSAGE_TWO);

    List<Message> res3 = filterer.filterMessages("on:04-25-2018");
    Assert.assertEquals(1, res3.size());
    assertEquals(res3.get(0), MESSAGE_THREE);
  }

  @Test
  public void testFilterMessagesContent() {
    List<Message> res1 = filterer.filterMessages("one");
    Assert.assertEquals(1, res1.size());
    assertEquals(res1.get(0), MESSAGE_ONE);

    List<Message> res2 = filterer.filterMessages("two");
    Assert.assertEquals(1, res2.size());
    assertEquals(res2.get(0), MESSAGE_TWO);

    List<Message> res3 = filterer.filterMessages("three");
    Assert.assertEquals(1, res3.size());
    assertEquals(res3.get(0), MESSAGE_THREE);

    List<Message> res4 = filterer.filterMessages("message");
    Assert.assertEquals(3, res4.size());
    assertEquals(res4.get(0), MESSAGE_TWO);
    assertEquals(res4.get(1), MESSAGE_THREE);
    assertEquals(res4.get(2), MESSAGE_ONE);
  }

  @Test
  public void testFilterMessagesAuthor() {
    List<Message> res1 = filterer.filterMessages("by:one");
    Assert.assertEquals(2, res1.size());
    assertEquals(res1.get(0), MESSAGE_THREE);
    assertEquals(res1.get(1), MESSAGE_ONE);

    List<Message> res2 = filterer.filterMessages("by:two");
    Assert.assertEquals(1, res2.size());
    assertEquals(res2.get(0), MESSAGE_TWO);
  }

  private void assertEquals(Message expectedMessage, Message actualMessage) {
    Assert.assertEquals(expectedMessage.getId(), actualMessage.getId());
    Assert.assertEquals(expectedMessage.getConversationId(), actualMessage.getConversationId());
    Assert.assertEquals(expectedMessage.getAuthorId(), actualMessage.getAuthorId());
    Assert.assertEquals(expectedMessage.getContent(), actualMessage.getContent());
    Assert.assertEquals(expectedMessage.getCreationTime(), actualMessage.getCreationTime());
  }
}
