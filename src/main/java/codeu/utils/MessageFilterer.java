package codeu.utils;

import codeu.model.data.Message;
import codeu.model.data.User;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Class to filter a list of messages based on a string formed using the following Context Free
 * Grammar(CFG): [A -> B and A | B or A | B]; [B -> (A) | filter]
 */
public class MessageFilterer {

  private HashSet<Message> originalMessages;
  private List<User> users;

  private Comparator<Message> msgComparator =
      new Comparator<Message>() {
        public int compare(Message m1, Message m2) {
          return m2.getCreationTime().compareTo(m1.getCreationTime());
        }
      };

  private DateTimeFormatter formatter =
      new DateTimeFormatterBuilder()
          .appendPattern("MM-dd-yyyy")
          .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
          .toFormatter()
          .withZone(ZoneId.systemDefault());

  public MessageFilterer(List<Message> originalMessages, List<User> users) {
    this.originalMessages = new HashSet<Message>(originalMessages);
    this.users = users;
  }

  /**
   * Splits the input on spaces and parentheses (instead of removing parentheses, they get their own
   * entry) to generate a list of tokens for parsing and initiates the parsing process
   *
   * @return the subset of messages from the original list that match the input string
   */
  public List<Message> filterMessages(String input) {
    String[] tokens = input.split("((?<=\\())|((?=\\)))| ");
    List<String> tokensList = new ArrayList<String>(Arrays.asList(tokens));
    List<Message> result =
        new ArrayList<Message>(filterMessagesByTokens(originalMessages, tokensList));
    result.sort(msgComparator);
    return result;
  }

  /* Handles the [A -> B and A | B or A | B] branch of the CFG
   *
   * @return the subset of messages from the original list that match the token list
   */
  private HashSet<Message> filterMessagesByTokens(HashSet<Message> messages, List<String> tokens) {
    if (tokens.size() == 0) {
      return messages;
    }
    HashSet<Message> filteredMessages = filterMessagesByTokensHelper(messages, tokens);
    if (tokens.size() < 2) {
      return filteredMessages;
    }
    String token = tokens.get(0);
    if (token.equals("AND")) {
      tokens.remove(0);
      filteredMessages = filterMessagesByTokens(filteredMessages, tokens);
    } else if (token.equals("OR")) {
      tokens.remove(0);
      filteredMessages.addAll(filterMessagesByTokens(originalMessages, tokens));
    }
    return filteredMessages;
  }

  /* Handles the [B -> (A) | filter] branch of the CFG
   *
   * @return the subset of messages from the original list that match the token list
   */
  private HashSet<Message> filterMessagesByTokensHelper(
      HashSet<Message> messages, List<String> tokens) {
    HashSet<Message> filteredMessages = messages;
    String token = tokens.get(0);
    tokens.remove(0);
    if (token.equals("(")) {
      filteredMessages = filterMessagesByTokens(messages, tokens);
      if (tokens.size() == 0 || !tokens.get(0).equals(")")) {
        throw new UnsupportedOperationException(
            "Incorrect string format - mismatched parentheses.");
      } else {
        tokens.remove(0);
      }
    } else if (token.startsWith("before:")) {
      String dateString = token.substring("before:".length());
      filteredMessages = filterMessagesByCreationDate(messages, dateString, -1);
    } else if (token.startsWith("after:")) {
      String dateString = token.substring("after:".length());
      filteredMessages = filterMessagesByCreationDate(messages, dateString, 1);
    } else if (token.startsWith("on:")) {
      String dateString = token.substring("on:".length());
      filteredMessages = filterMessagesByCreationDate(messages, dateString, 0);
    } else if (token.startsWith("by:")) {
      String username = token.substring("by:".length());
      User user = null;
      for (User u : users) {
        if (u.getName().equals(username)) {
          user = u;
          break;
        }
      }
      if (user == null) {
        return new HashSet<Message>();
      }
      filteredMessages = filterMessagesByAuthor(messages, user.getId());
    } else {
      filteredMessages = filterMessagesByContent(messages, token);
    }
    return filteredMessages;
  }

  private HashSet<Message> filterMessagesByContent(HashSet<Message> messages, String content) {
    HashSet<Message> filteredMessages = new HashSet<Message>();
    for (Message message : messages) {
      if (message.getContent().contains(content)) {
        filteredMessages.add(message);
      }
    }
    return filteredMessages;
  }

  private HashSet<Message> filterMessagesByAuthor(HashSet<Message> messages, UUID user) {
    HashSet<Message> filteredMessages = new HashSet<Message>();
    for (Message message : messages) {
      if (message.getAuthorId().equals(user)) {
        filteredMessages.add(message);
      }
    }
    return filteredMessages;
  }

  private HashSet<Message> filterMessagesByCreationDate(
      HashSet<Message> messages, String dateString, int comp) {
    ZonedDateTime date = ZonedDateTime.parse(dateString, formatter);
    HashSet<Message> filteredMessages = new HashSet<Message>();
    for (Message message : messages) {
      ZonedDateTime msgDate =
          ZonedDateTime.ofInstant(message.getCreationTime(), ZoneId.systemDefault());
      msgDate = msgDate.truncatedTo(ChronoUnit.DAYS);
      int dateComp = msgDate.compareTo(date);
      if (dateComp > 0 && comp > 0) {
        filteredMessages.add(message);
      } else if (dateComp == 0 && comp == 0) {
        filteredMessages.add(message);
      } else if (dateComp < 0 && comp < 0) {
        filteredMessages.add(message);
      }
    }
    return filteredMessages;
  }
}
