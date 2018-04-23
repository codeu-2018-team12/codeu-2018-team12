package codeu.utils;

import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class Filterer {

  public static List<Conversation> filterConversations(List<Conversation> convos, String input) {
    String[] tokens = input.split("((?<=\\())|((?=\\)))| ");
    List<String> tokensList = new ArrayList<String>(Arrays.asList(tokens));
    HashSet<Conversation> conversations = new HashSet<Conversation>(convos);
    List<Conversation> result =
        new ArrayList<Conversation>(
            filterConversationsByTokens(conversations, conversations, tokensList));
    List<Conversation> direct = new ArrayList<Conversation>();
    for (Conversation convo : result) {
      if (convo.getTitle().startsWith("direct:")) {
        direct.add(convo);
      }
    }
    result.removeAll(direct);
    return result;
  }

  private static HashSet<Conversation> filterConversationsByTokens(
      HashSet<Conversation> originalConvos, HashSet<Conversation> convos, List<String> tokens) {
    if (tokens.size() == 0) {
      return convos;
    }
    HashSet<Conversation> filteredConvos =
        filterConversationsByTokensHelper(originalConvos, convos, tokens);
    if (tokens.size() < 2) {
      return filteredConvos;
    }
    String token = tokens.get(0);
    if (token.equals("AND")) {
      tokens.remove(0);
      filteredConvos = filterConversationsByTokens(originalConvos, filteredConvos, tokens);
    } else if (token.equals("OR")) {
      tokens.remove(0);
      filteredConvos.addAll(filterConversationsByTokens(originalConvos, originalConvos, tokens));
    }
    return filteredConvos;
  }

  private static HashSet<Conversation> filterConversationsByTokensHelper(
      HashSet<Conversation> originalConvos, HashSet<Conversation> convos, List<String> tokens) {
    HashSet<Conversation> filteredConvos = convos;
    String token = tokens.get(0);
    tokens.remove(0);
    if (token.equals("(")) {
      filteredConvos = filterConversationsByTokens(originalConvos, convos, tokens);
      if (tokens.size() == 0 || !tokens.get(0).equals(")")) {
        throw new UnsupportedOperationException(
            "Incorrect string format - mismatched parentheses.");
      } else {
        tokens.remove(0);
        return filteredConvos;
      }
    } else if (token.startsWith("before:")) {
      String dateString = token.substring("before:".length());
      filteredConvos =
          filterConversationsByCreationDate(convos, getInstantFromString(dateString), -1);
    } else if (token.startsWith("after:")) {
      String dateString = token.substring("after:".length());
      filteredConvos =
          filterConversationsByCreationDate(convos, getInstantFromString(dateString), 1);
    } else if (token.startsWith("on:")) {
      String dateString = token.substring("on:".length());
      filteredConvos =
          filterConversationsByCreationDate(convos, getInstantFromString(dateString), 0);
    } else if (token.startsWith("with:")) {
      String username = token.substring("with:".length());
      User user = UserStore.getInstance().getUser(username);
      if (user == null) {
        return new HashSet<Conversation>();
      }
      filteredConvos = filterConversationsByMember(convos, user.getId());
    } else {
      filteredConvos = filterConversationsByTitle(convos, token);
    }
    return filteredConvos;
  }

  private static HashSet<Conversation> filterConversationsByTitle(
      HashSet<Conversation> convos, String title) {
    HashSet<Conversation> filteredConvos = new HashSet<Conversation>();
    for (Conversation convo : convos) {
      if (convo.getTitle().contains(title)) {
        filteredConvos.add(convo);
      }
    }
    return filteredConvos;
  }

  private static HashSet<Conversation> filterConversationsByCreationDate(
      HashSet<Conversation> convos, Instant date, int comp) {
    HashSet<Conversation> filteredConvos = new HashSet<Conversation>();
    for (Conversation convo : convos) {
      if (convo.getCreationTime().compareTo(date) > 0 && comp > 0) {
        filteredConvos.add(convo);
      } else if (convo.getCreationTime().compareTo(date) == 0 && comp == 0) {
        filteredConvos.add(convo);
      } else if (convo.getCreationTime().compareTo(date) < 0 && comp < 0) {
        filteredConvos.add(convo);
      }
    }
    return filteredConvos;
  }

  private static HashSet<Conversation> filterConversationsByMember(
      HashSet<Conversation> convos, UUID user) {
    HashSet<Conversation> filteredConvos = new HashSet<Conversation>();
    for (Conversation convo : convos) {
      if (convo.getConversationUsers().contains(user)) {
        filteredConvos.add(convo);
      }
    }
    return filteredConvos;
  }

  public static List<Message> filterMessages(List<Message> messages, String input) {
    String[] tokens = input.split("((?<=\\())|((?=\\)))| ");
    List<String> tokensList = new ArrayList<String>(Arrays.asList(tokens));
    HashSet<Message> messagesHash = new HashSet<Message>(messages);
    List<Message> result =
        new ArrayList<Message>(filterMessagesByTokens(messagesHash, messagesHash, tokensList));
    return result;
  }

  private static HashSet<Message> filterMessagesByTokens(
      HashSet<Message> originalMessages, HashSet<Message> messages, List<String> tokens) {
    if (tokens.size() == 0) {
      return messages;
    }
    HashSet<Message> filteredMessages =
        filterMessagesByTokensHelper(originalMessages, messages, tokens);
    if (tokens.size() < 2) {
      return filteredMessages;
    }
    String token = tokens.get(0);
    if (token.equals("AND")) {
      tokens.remove(0);
      filteredMessages = filterMessagesByTokens(originalMessages, filteredMessages, tokens);
    } else if (token.equals("OR")) {
      tokens.remove(0);
      filteredMessages.addAll(filterMessagesByTokens(originalMessages, originalMessages, tokens));
    }
    return filteredMessages;
  }

  private static HashSet<Message> filterMessagesByTokensHelper(
      HashSet<Message> originalMessages, HashSet<Message> messages, List<String> tokens) {
    HashSet<Message> filteredMessages = messages;
    String token = tokens.get(0);
    tokens.remove(0);
    if (token.equals("(")) {
      filteredMessages = filterMessagesByTokens(originalMessages, messages, tokens);
      if (tokens.size() == 0 || !tokens.get(0).equals(")")) {
        throw new UnsupportedOperationException(
            "Incorrect string format - mismatched parentheses.");
      } else {
        tokens.remove(0);
      }
    } else if (token.startsWith("before:")) {
      String dateString = token.substring("before:".length());
      filteredMessages =
          filterMessagesByCreationDate(messages, getInstantFromString(dateString), -1);
    } else if (token.startsWith("after:")) {
      String dateString = token.substring("after:".length());
      filteredMessages =
          filterMessagesByCreationDate(messages, getInstantFromString(dateString), 1);
    } else if (token.startsWith("on:")) {
      String dateString = token.substring("on:".length());
      filteredMessages =
          filterMessagesByCreationDate(messages, getInstantFromString(dateString), 0);
    } else if (token.startsWith("by:")) {
      String username = token.substring("by:".length());
      User user = UserStore.getInstance().getUser(username);
      if (user == null) {
        return new HashSet<Message>();
      }
      filteredMessages = filterMessagesByAuthor(messages, user.getId());
    } else {
      filteredMessages = filterMessagesByContent(messages, token);
    }
    return filteredMessages;
  }

  private static HashSet<Message> filterMessagesByContent(
      HashSet<Message> messages, String content) {
    HashSet<Message> filteredMessages = new HashSet<Message>();
    for (Message message : messages) {
      if (message.getContent().contains(content)) {
        filteredMessages.add(message);
      }
    }
    return filteredMessages;
  }

  private static HashSet<Message> filterMessagesByAuthor(HashSet<Message> messages, UUID user) {
    HashSet<Message> filteredMessages = new HashSet<Message>();
    for (Message message : messages) {
      if (message.getAuthorId().equals(user)) {
        filteredMessages.add(message);
      }
    }
    return filteredMessages;
  }

  private static HashSet<Message> filterMessagesByCreationDate(
      HashSet<Message> messages, Instant date, int comp) {
    HashSet<Message> filteredMessages = new HashSet<Message>();
    for (Message message : messages) {
      if (message.getCreationTime().compareTo(date) > 0 && comp > 0) {
        filteredMessages.add(message);
      } else if (message.getCreationTime().compareTo(date) == 0 && comp == 0) {
        filteredMessages.add(message);
      } else if (message.getCreationTime().compareTo(date) < 0 && comp < 0) {
        filteredMessages.add(message);
      }
    }
    return filteredMessages;
  }

  private static Instant getInstantFromString(String str) {
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("MM-dd-yyyy").withZone(ZoneId.systemDefault());
    LocalDate date = LocalDate.parse(str, formatter);
    return date.atStartOfDay(ZoneId.systemDefault()).toInstant();
  }
}
