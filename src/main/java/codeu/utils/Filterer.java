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
    return new ArrayList<Conversation>(
        filterConversationsByTokens(conversations, conversations, tokensList));
  }

  private static HashSet<Conversation> filterConversationsByTokens(
      HashSet<Conversation> originalConvos, HashSet<Conversation> convos, List<String> tokens) {
    if (tokens.size() == 0) {
      return convos;
    }
    HashSet<Conversation> filteredConvos =
        filterConversationsByTokensHelper(originalConvos, convos, tokens);
    if (tokens.size() == 0) {
      return filteredConvos;
    }
    if (tokens.get(0).equals("AND")) {
      tokens.remove(0);
      return filterConversationsByTokens(originalConvos, filteredConvos, tokens);
    } else if (tokens.get(0).equals("OR")) {
      tokens.remove(0);
      filteredConvos.addAll(filterConversationsByTokens(originalConvos, originalConvos, tokens));
      return filteredConvos;
    } else {
      return filteredConvos;
    }
  }

  private static HashSet<Conversation> filterConversationsByTokensHelper(
      HashSet<Conversation> originalConvos, HashSet<Conversation> convos, List<String> tokens) {
    HashSet<Conversation> filteredConvos = convos;
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("MM-dd-yyyy").withZone(ZoneId.systemDefault());
    if (tokens.get(0).equals("(")) {
      tokens.remove(0);
      filteredConvos = filterConversationsByTokens(originalConvos, convos, tokens);
      if (tokens.size() == 0 || !tokens.get(0).equals(")")) {
        throw new UnsupportedOperationException(
            "Incorrect string format - mismatched parentheses.");
      } else {
        tokens.remove(0);
        return filteredConvos;
      }
    } else if (tokens.get(0).startsWith("before:")) {
      String dateString = tokens.get(0).substring("before:".length());
      tokens.remove(0);
      LocalDate date = LocalDate.parse(dateString, formatter);
      Instant instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
      filteredConvos = filterConversationsByCreationDate(convos, instant, -1);
      return filteredConvos;
    } else if (tokens.get(0).startsWith("after:")) {
      String dateString = tokens.get(0).substring("after:".length());
      tokens.remove(0);
      LocalDate date = LocalDate.parse(dateString, formatter);
      Instant instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
      filteredConvos = filterConversationsByCreationDate(convos, instant, 1);
      return filteredConvos;
    } else if (tokens.get(0).startsWith("on:")) {
      String dateString = tokens.get(0).substring("on:".length());
      tokens.remove(0);
      LocalDate date = LocalDate.parse(dateString, formatter);
      Instant instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
      filteredConvos = filterConversationsByCreationDate(convos, instant, 0);
      return filteredConvos;
    } else if (tokens.get(0).startsWith("with:")) {
      String username = tokens.get(0).substring("with:".length());
      System.out.println(username);
      tokens.remove(0);
      User user = UserStore.getInstance().getUser(username);
      if (user == null) {
        return new HashSet<Conversation>();
      }
      filteredConvos = filterConversationsByMember(convos, user.getId());
      return filteredConvos;
    } else {
      filteredConvos = filterConversationsByTitle(convos, tokens.get(0));
      tokens.remove(0);
      return filteredConvos;
    }
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

  private static HashSet<Message> filterMessagesByContent(List<Message> messages, String content) {
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
}
