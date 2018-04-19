import codeu.model.data.Conversation;
import codeu.model.data.Message;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Filterer {

  public static List<Conversation> filterConversationsByTitle(
      List<Conversation> convos, String title) {
    ArrayList<Conversation> filteredConvos = new ArrayList<Conversation>();
    for (Conversation convo : convos) {
      if (convo.getTitle().contains(title)) {
        filteredConvos.add(convo);
      }
    }
    return filteredConvos;
  }

  public static List<Conversation> filterConversationsByCreationDate(
      List<Conversation> convos, Instant date, int comp) {
    ArrayList<Conversation> filteredConvos = new ArrayList<Conversation>();
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

  public static List<Conversation> filterConversationsByMember(
      List<Conversation> convos, UUID user) {
    ArrayList<Conversation> filteredConvos = new ArrayList<Conversation>();
    for (Conversation convo : convos) {
      if (convo.getConversationUsers().contains(user)) {
        filteredConvos.add(convo);
      }
    }
    return filteredConvos;
  }

  public static List<Message> filterMessagesByContent(List<Message> messages, String content) {
    ArrayList<Message> filteredMessages = new ArrayList<Message>();
    for (Message message : messages) {
      if (message.getContent().contains(content)) {
        filteredMessages.add(message);
      }
    }
    return filteredMessages;
  }

  public static List<Message> filterMessagesByAuthor(List<Message> messages, UUID user) {
    ArrayList<Message> filteredMessages = new ArrayList<Message>();
    for (Message message : messages) {
      if (message.getAuthorId().equals(user)) {
        filteredMessages.add(message);
      }
    }
    return filteredMessages;
  }

  public static List<Message> filterMessagesByCreationDate(
      List<Message> messages, Instant date, int comp) {
    ArrayList<Message> filteredMessages = new ArrayList<Message>();
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
