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

package codeu.model.store.persistence;

import codeu.model.data.Activity;
import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.DatastoreServiceConfig;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

/**
 * This class handles all interactions with Google App Engine's Datastore service. On startup it
 * sets the state of the applications's data objects from the current contents of its Datastore. It
 * also performs writes of new or modified objects back to the Datastore.
 */
public class PersistentDataStore {

  // Handle to Google AppEngine's Datastore service.
  private DatastoreService datastore;

  /**
   * Constructs a new PersistentDataStore and sets up its state to begin loading objects from the
   * Datastore service.
   */
  public PersistentDataStore() {
    System.setProperty(
        DatastoreServiceConfig.DATASTORE_EMPTY_LIST_SUPPORT, Boolean.TRUE.toString());
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  /**
   * Loads all User objects from the Datastore service and returns them in a List.
   *
   * @throws codeu.model.store.persistence.PersistentDataStoreException if an error was detected
   *     during the load from the Datastore service
   */
  public List<User> loadUsers() throws PersistentDataStoreException {

    List<User> users = new ArrayList<>();

    // Retrieve all users from the datastore.
    Query query = new Query("chat-users");
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        UUID uuid = UUID.fromString((String) entity.getProperty("uuid"));
        String userName = (String) entity.getProperty("username");
        String password = (String) entity.getProperty("password");
        String biography = (String) entity.getProperty("biography");
        Instant creationTime = Instant.parse((String) entity.getProperty("creation_time"));
        Boolean notifications =
            entity.getProperty("notificationStatus") == null
                ? true
                : (boolean) entity.getProperty("notificationStatus");
        String notificationFrequency =
            entity.getProperty("notificationFrequency") == null
                ? "everyMessage"
                : (String) entity.getProperty("notificationFrequency");
        String profilePrivacy =
            entity.getProperty("profilePrivacy") == null
                ? "allContent"
                : (String) entity.getProperty("profilePrivacy");
        String activityFeedPrivacy =
            entity.getProperty("activityFeedPrivacy") == null
                ? "allContent"
                : (String) entity.getProperty("activityFeedPrivacy");
        List<String> conversationFriends =
            entity.getProperty("conversationFriends") == null
                ? new ArrayList<>()
                : (List<String>) entity.getProperty("conversationFriends");
        String email =
            entity.getProperty("email") == null
                ? "codeUChatTest@gmail.com"
                : (String) entity.getProperty("email");
        if (password != null && !password.startsWith("$2a$")) {
          password = BCrypt.hashpw(password, BCrypt.gensalt());
        }
        Queue<String> notificationQueue =
            entity.getProperty("notification") == null
                ? new LinkedList<String>()
                : (Queue<String>) entity.getProperty("notifications");
        User user = new User(uuid, userName, password, biography, creationTime, email);
        user.setNotifications(notificationQueue);
        user.setConversationFriends(conversationFriends);
        if (!(activityFeedPrivacy.equals("allContent"))) {
          user.setActivityFeedPrivacy((activityFeedPrivacy));
        }
        if (!(profilePrivacy.equals("allContent"))) {
          user.setProfilePrivacy((profilePrivacy));
        }
        if (!notifications) {
          user.setNotificationStatus((notifications));
        }
        if (!(notificationFrequency.equals("everyMessage"))) {
          user.setNotificationFrequency((notificationFrequency));
        }
        users.add(user);
      } catch (Exception e) {
        // In a production environment, errors should be very rare. Errors which may
        // occur include network errors, Datastore service errors, authorization errors,
        // database entity definition mismatches, or service mismatches.
        throw new PersistentDataStoreException(e);
      }
    }

    return users;
  }

  /**
   * Loads all Conversation objects from the Datastore service and returns them in a List.
   *
   * @throws codeu.model.store.persistence.PersistentDataStoreException if an error was detected
   *     during the load from the Datastore service
   */
  public List<Conversation> loadConversations() throws PersistentDataStoreException {

    List<Conversation> conversations = new ArrayList<>();

    // Retrieve all conversations from the datastore.
    Query query = new Query("chat-conversations");
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        UUID uuid = UUID.fromString((String) entity.getProperty("uuid"));
        UUID ownerUuid = UUID.fromString((String) entity.getProperty("owner_uuid"));
        String title = (String) entity.getProperty("title");
        List<String> users =
            entity.getProperty("users") == null
                ? new ArrayList<String>()
                : (List<String>) entity.getProperty("users");
        Instant creationTime = Instant.parse((String) entity.getProperty("creation_time"));
        boolean isPublic =
            entity.getProperty("isPublic") == null
                ? true
                : ((String) entity.getProperty("isPublic")).equals("true");
        Conversation conversation =
            new Conversation(uuid, ownerUuid, title, creationTime, isPublic);
        conversation.setUsers(users);
        conversations.add(conversation);
      } catch (Exception e) {
        // In a production environment, errors should be very rare. Errors which may
        // occur include network errors, Datastore service errors, authorization errors,
        // database entity definition mismatches, or service mismatches.
        throw new PersistentDataStoreException(e);
      }
    }

    return conversations;
  }

  /**
   * Loads all Message objects from the Datastore service and returns them in a List.
   *
   * @throws codeu.model.store.persistence.PersistentDataStoreException if an error was detected
   *     during the load from the Datastore service
   */
  public List<Message> loadMessages() throws PersistentDataStoreException {

    List<Message> messages = new ArrayList<>();

    // Retrieve all messages from the datastore.
    Query query = new Query("chat-messages");
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        UUID uuid = UUID.fromString((String) entity.getProperty("uuid"));
        UUID conversationUuid = UUID.fromString((String) entity.getProperty("conv_uuid"));
        UUID authorUuid = UUID.fromString((String) entity.getProperty("author_uuid"));
        Instant creationTime = Instant.parse((String) entity.getProperty("creation_time"));
        String content = (String) entity.getProperty("content");
        Message message = new Message(uuid, conversationUuid, authorUuid, content, creationTime);
        messages.add(message);
      } catch (Exception e) {
        // In a production environment, errors should be very rare. Errors which may
        // occur include network errors, Datastore service errors, authorization errors,
        // database entity definition mismatches, or service mismatches.
        throw new PersistentDataStoreException(e);
      }
    }

    return messages;
  }

  /**
   * Loads all Activity objects from the Datastore service and returns them in a List.
   *
   * @throws codeu.model.store.persistence.PersistentDataStoreException if an error was detected
   *     during the load from the Datastore service
   */
  public List<Activity> loadActivities() throws PersistentDataStoreException {

    List<Activity> activities = new ArrayList<>();

    // Retrieve all activities from the datastore.
    Query query = new Query("chat-activities");
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        UUID uuid = UUID.fromString((String) entity.getProperty("uuid"));
        UUID memberId = UUID.fromString((String) entity.getProperty("member_id"));
        UUID conversationId = UUID.fromString((String) entity.getProperty("conversation_id"));
        Instant creationTime = Instant.parse((String) entity.getProperty("creation_time"));
        String activityType = (String) entity.getProperty("activity_type");
        String activityMessage = (String) entity.getProperty("activity_message");
        List<String> strings =
            entity.getProperty("users") == null
                ? new ArrayList<String>()
                : (List<String>) entity.getProperty("users");
        List<UUID> users = new ArrayList<UUID>();
        for (String str : strings) {
          users.add(UUID.fromString(str));
        }
        boolean isPublic =
            entity.getProperty("isPublic") == null
                ? true
                : ((String) entity.getProperty("isPublic")).equals("true");
        Activity activity =
            new Activity(
                uuid,
                memberId,
                conversationId,
                creationTime,
                activityType,
                activityMessage,
                users,
                isPublic);
        activities.add(activity);
      } catch (Exception e) {
        // In a production environment, errors should be very rare. Errors which may
        // occur include network errors, Datastore service errors, authorization errors,
        // database entity definition mismatches, or service mismatches.
        throw new PersistentDataStoreException(e);
      }
    }

    return activities;
  }

  /** Write a User object to the Datastore service. */
  public void writeThrough(User user) {
    Entity userEntity = new Entity("chat-users");
    userEntity.setProperty("uuid", user.getId().toString());
    userEntity.setProperty("username", user.getName());
    userEntity.setProperty("password", user.getPassword());
    userEntity.setProperty("biography", user.getBio());
    userEntity.setProperty("creation_time", user.getCreationTime().toString());
    userEntity.setProperty("email", user.getEmail());
    userEntity.setProperty("notificationStatus", user.getNotificationStatus());
    userEntity.setProperty("notificationFrequency", user.getNotificationFrequency());
    userEntity.setProperty("notifications", user.getStoredNotifications());
    userEntity.setProperty("profilePrivacy", user.getProfilePrivacy());
    userEntity.setProperty("activityFeedPrivacy", user.getActivityFeedPrivacy());
    userEntity.setProperty("conversationFriends", user.getUserIdsAsStrings());
    datastore.put(userEntity);
  }

  /** Write a Message object to the Datastore service. */
  public void writeThrough(Message message) {
    Entity messageEntity = new Entity("chat-messages");
    messageEntity.setProperty("uuid", message.getId().toString());
    messageEntity.setProperty("conv_uuid", message.getConversationId().toString());
    messageEntity.setProperty("author_uuid", message.getAuthorId().toString());
    messageEntity.setProperty("content", message.getContent());
    messageEntity.setProperty("creation_time", message.getCreationTime().toString());
    datastore.put(messageEntity);
  }

  /** Write a Conversation object to the Datastore service. */
  public void writeThrough(Conversation conversation) {
    Entity conversationEntity = new Entity("chat-conversations");
    conversationEntity.setProperty("uuid", conversation.getId().toString());
    conversationEntity.setProperty("owner_uuid", conversation.getOwnerId().toString());
    conversationEntity.setProperty("title", conversation.getTitle());
    conversationEntity.setProperty("creation_time", conversation.getCreationTime().toString());
    conversationEntity.setProperty("users", conversation.getUserIdsAsStrings());
    conversationEntity.setProperty("isPublic", Boolean.toString(conversation.getIsPublic()));
    datastore.put(conversationEntity);
  }

  /** Write an Activity object to the Datastore service. */
  public void writeThrough(Activity activity) {
    Entity activityEntity = new Entity("chat-activities");
    activityEntity.setProperty("uuid", activity.getId().toString());
    activityEntity.setProperty("member_id", activity.getUserId().toString());
    activityEntity.setProperty("conversation_id", activity.getConversationId().toString());
    activityEntity.setProperty("creation_time", activity.getCreationTime().toString());
    activityEntity.setProperty("activity_type", activity.getActivityType());
    activityEntity.setProperty("activity_message", activity.getActivityMessage());
    activityEntity.setProperty("users", activity.getUserIdsAsStrings());
    activityEntity.setProperty("isPublic", Boolean.toString(activity.getIsPublic()));
    datastore.put(activityEntity);
  }

  /** Updates the users property of a Conversation entity in the Datastore service */
  public void updateConversationEntityUsers(Conversation conversation) {
    Entity resultEntity = setUpConversationEntity(conversation);
    if (resultEntity != null) {
      resultEntity.setProperty("users", conversation.getUserIdsAsStrings());
      datastore.put(resultEntity);
    }
  }

  /** Updates the biography property of a User entity in the Datastore service */
  public void updateUserEntityBiography(User user) {
    Entity resultEntity = setUpUserEntity(user);
    resultEntity.setProperty("biography", user.getBio());
    datastore.put(resultEntity);
  }

  /** Updates the email property of a User entity in the Datastore service */
  public void updateUserEntityEmail(User user) {
    Entity resultEntity = setUpUserEntity(user);
    if (resultEntity != null) {
      resultEntity.setProperty("email", user.getEmail());
      datastore.put(resultEntity);
    }
  }

  /** Updates the password property of a User entity in the Datastore service */
  public void updateUserEntityPassword(User user) {
    Entity resultEntity = setUpUserEntity(user);
    if (resultEntity != null) {
      resultEntity.setProperty("password", user.getPassword());
      datastore.put(resultEntity);
    }
  }

  /** Updates the notificationsFrequency property of a user entity in the Datastore service */
  public void updateUserEntityNotificationFrequency(User user) {
    Entity resultEntity = setUpUserEntity(user);
    if (resultEntity != null) {
      resultEntity.setProperty("notificationFrequency", user.getNotificationFrequency());
      datastore.put(resultEntity);
    }
  }

  /** Updates the notifications property of a user entity in the Datastore service */
  public void updateUserEntityNotificationStatus(User user) {
    Entity resultEntity = setUpUserEntity(user);
    if (resultEntity != null) {
      resultEntity.setProperty("notificationStatus", user.getNotificationStatus());
      datastore.put(resultEntity);
    }
  }

  /** Updates the notifications property of a user entity in the Datastore service */
  public void updateUserEntityStoredNotifications(User user) {
    Entity resultEntity = setUpUserEntity(user);
    if (resultEntity != null) {
      resultEntity.setProperty("notifications", user.getStoredNotifications());
      datastore.put(resultEntity);
    }
  }

  /** Updates the profile privacy property of a user entity in the Datastore service */
  public void updateUserEntityProfilePrivacy(User user) {
    Entity resultEntity = setUpUserEntity(user);
    if (resultEntity != null) {
      resultEntity.setProperty("profilePrivacy", user.getProfilePrivacy());
      datastore.put(resultEntity);
    }
  }

  /** Updates the activity feed privacy property of a user entity in the Datastore service */
  public void updateUserEntityActivityFeedPrivacy(User user) {
    Entity resultEntity = setUpUserEntity(user);
    if (resultEntity != null) {
      resultEntity.setProperty("activityFeedPrivacy", user.getActivityFeedPrivacy());
      datastore.put(resultEntity);
    }
  }

  /** Updates the activity feed privacy property of a user entity in the Datastore service */
  public void updateUserEntityConversationFriends(User user) {
    Entity resultEntity = setUpUserEntity(user);
    if (resultEntity != null) {
      resultEntity.setProperty("conversationFriends", user.getUserIdsAsStrings());
      datastore.put(resultEntity);
    }
  }

  /** Retrieves a User Entity object
   *
   * @param user user in application
   * @return User Entity
   **/
  private Entity setUpUserEntity(User user) {
    Query query =
        new Query("chat-users")
            .setFilter(new FilterPredicate("uuid",
                FilterOperator.EQUAL, user.getId().toString()));
    PreparedQuery preparedQuery = datastore.prepare(query);
    return preparedQuery.asSingleEntity();
  }

  /** Retrieves a Conversation Entity object
   *
   * @param conversation user in application
   * @return Conversation Entity
   **/
  private Entity setUpConversationEntity(Conversation conversation) {
    Query query =
        new Query("chat-conversations")
            .setFilter(
                new FilterPredicate("uuid",
                    FilterOperator.EQUAL, conversation.getId().toString()));
    PreparedQuery preparedQuery = datastore.prepare(query);
    return preparedQuery.asSingleEntity();
  }
}
