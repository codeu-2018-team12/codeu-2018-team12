package codeu.utils;

import codeu.model.data.Conversation;
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
 * Class to filter a list of conversations based on a string formed using the following Context Free
 * Grammar(CFG): [A -> B and A | B or A | B]; [B -> (A) | filter]
 */
public class ConversationFilterer {

  private HashSet<Conversation> originalConvos;
  private List<User> users;

  private Comparator<Conversation> convoComparator =
      new Comparator<Conversation>() {
        public int compare(Conversation copvOne, Conversation copvTwo) {
          return copvTwo.getCreationTime().compareTo(copvOne.getCreationTime());
        }
      };

  private DateTimeFormatter formatter =
      new DateTimeFormatterBuilder()
          .appendPattern("MM-dd-yyyy")
          .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
          .toFormatter()
          .withZone(ZoneId.systemDefault());

  public ConversationFilterer(List<Conversation> originalConvos, List<User> users) {
    this.originalConvos = new HashSet<Conversation>(originalConvos);
    this.users = users;
  }

  /**
   * Splits the input on spaces and parentheses (instead of removing parentheses, they get their own
   * entry) to generate a list of tokens for parsing and initiates the parsing process
   *
   * @return the subset of conversations from the original list that match the input string
   */
  public List<Conversation> filterConversations(String input) {
    String[] tokens = input.split("((?<=\\())|((?=\\)))| ");
    List<String> tokensList = new ArrayList<String>(Arrays.asList(tokens));
    List<Conversation> result =
        new ArrayList<Conversation>(filterConversationsByTokens(originalConvos, tokensList));
    List<Conversation> direct = new ArrayList<Conversation>();
    for (Conversation convo : result) {
      if (convo.getTitle().startsWith("direct:")) {
        direct.add(convo);
      }
    }
    result.removeAll(direct);
    result.sort(convoComparator);
    return result;
  }

  /* Handles the [A -> B and A | B or A | B] branch of the CFG
   *
   * @return the subset of conversations from the original list that match the token list
   */
  private HashSet<Conversation> filterConversationsByTokens(
      HashSet<Conversation> convos, List<String> tokens) {
    if (tokens.size() == 0) {
      return convos;
    }
    HashSet<Conversation> filteredConvos = filterConversationsByTokensHelper(convos, tokens);
    if (tokens.size() < 2) {
      return filteredConvos;
    }
    String token = tokens.get(0);
    if (token.equals("AND")) {
      tokens.remove(0);
      filteredConvos = filterConversationsByTokens(filteredConvos, tokens);
    } else if (token.equals("OR")) {
      tokens.remove(0);
      filteredConvos.addAll(filterConversationsByTokens(originalConvos, tokens));
    }
    return filteredConvos;
  }

  /* Handles the [B -> (A) | filter] branch of the CFG
   *
   * @return the subset of conversations from the original list that match the token list
   */
  private HashSet<Conversation> filterConversationsByTokensHelper(
      HashSet<Conversation> convos, List<String> tokens) {
    HashSet<Conversation> filteredConvos = convos;
    String token = tokens.get(0);
    tokens.remove(0);
    if (token.equals("(")) {
      filteredConvos = filterConversationsByTokens(convos, tokens);
      if (tokens.size() == 0 || !tokens.get(0).equals(")")) {
        throw new UnsupportedOperationException(
            "Incorrect string format - mismatched parentheses.");
      } else {
        tokens.remove(0);
        return filteredConvos;
      }
    } else if (token.startsWith("before:")) {
      String dateString = token.substring("before:".length());
      filteredConvos = filterConversationsByCreationDate(convos, dateString, -1);
    } else if (token.startsWith("after:")) {
      String dateString = token.substring("after:".length());
      filteredConvos = filterConversationsByCreationDate(convos, dateString, 1);
    } else if (token.startsWith("on:")) {
      String dateString = token.substring("on:".length());
      filteredConvos = filterConversationsByCreationDate(convos, dateString, 0);
    } else if (token.startsWith("with:")) {
      String username = token.substring("with:".length());
      User user = null;
      for (User u : users) {
        if (u.getName().equals(username)) {
          user = u;
          break;
        }
      }
      if (user == null) {
        return new HashSet<Conversation>();
      }
      filteredConvos = filterConversationsByMember(convos, user.getId());
    } else {
      filteredConvos = filterConversationsByTitle(convos, token);
    }
    return filteredConvos;
  }

  private HashSet<Conversation> filterConversationsByTitle(
      HashSet<Conversation> convos, String title) {
    HashSet<Conversation> filteredConvos = new HashSet<Conversation>();
    for (Conversation convo : convos) {
      if (convo.getTitle().contains(title)) {
        filteredConvos.add(convo);
      }
    }
    return filteredConvos;
  }

  private HashSet<Conversation> filterConversationsByCreationDate(
      HashSet<Conversation> convos, String dateString, int comp) {
    ZonedDateTime date = ZonedDateTime.parse(dateString, formatter);
    HashSet<Conversation> filteredConvos = new HashSet<Conversation>();
    for (Conversation convo : convos) {
      ZonedDateTime convoDate =
          ZonedDateTime.ofInstant(convo.getCreationTime(), ZoneId.systemDefault());
      convoDate = convoDate.truncatedTo(ChronoUnit.DAYS);
      int dateComp = convoDate.compareTo(date);
      if (dateComp > 0 && comp > 0) {
        filteredConvos.add(convo);
      } else if (dateComp == 0 && comp == 0) {
        filteredConvos.add(convo);
      } else if (dateComp < 0 && comp < 0) {
        filteredConvos.add(convo);
      }
    }
    return filteredConvos;
  }

  private HashSet<Conversation> filterConversationsByMember(
      HashSet<Conversation> convos, UUID user) {
    HashSet<Conversation> filteredConvos = new HashSet<Conversation>();
    for (Conversation convo : convos) {
      if (convo.getConversationUsers().contains(user)) {
        filteredConvos.add(convo);
      }
    }
    return filteredConvos;
  }
}
