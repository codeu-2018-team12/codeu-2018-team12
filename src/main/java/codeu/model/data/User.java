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

package codeu.model.data;

import codeu.model.store.persistence.PersistentStorageAgent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

/** Class representing a registered user. */
public class User {
  private final UUID id;
  private final String name;
  private String hashedPassword;
  private String biography;
  private final Instant creation;
  private String email;
  private boolean notifications;
  private String notificationFrequency;
  private Queue<String> storedNotifications;
  private String profilePrivacy;
  private String activityFeedPrivacy;
  private List<UUID> conversationFriends;
  private String profilePicture;

  /**
   * Constructs a new User.
   *
   * @param id the ID of this User
   * @param name the username of this User
   * @param hashedPassword the hashed password of this User
   * @param creation the creation time of this User
   * @param email the email of this user
   */
  public User(
      UUID id,
      String name,
      String hashedPassword,
      String biography,
      Instant creation,
      String email) {
    this.id = id;
    this.name = name;
    this.hashedPassword = hashedPassword;
    this.biography = biography;
    this.creation = creation;
    this.email = email;
    this.notifications = true;
    this.notificationFrequency = "everyMessage";
    this.storedNotifications = new LinkedList<>();
    this.profilePrivacy = "allContent";
    this.activityFeedPrivacy = "allContent";
    this.conversationFriends = new ArrayList<>();
    this.profilePicture = null;
  }

  /** Returns the ID of this User. */
  public UUID getId() {
    return id;
  }

  /** Returns the username of this User. */
  public String getName() {
    return name;
  }

  /** Returns the hashedPassword of this User. */
  public String getPassword() {
    return hashedPassword;
  }

  /** Returns the bio of this User */
  public String getBio() {
    return biography;
  }

  /** Returns the email of this User */
  public String getEmail() {
    return email;
  }

  /** Returns the creation time of this User. */
  public Instant getCreationTime() {
    return creation;
  }

  /**
   * Returns frequency of user notifications Either everyMessage, everyHour, everyFourHours,
   * everyDay
   */
  public String getNotificationFrequency() {
    return notificationFrequency;
  }

  /** Returns true if users want notifications, false if not */
  public boolean getNotificationStatus() {
    return notifications;
  }

  /** Returns a queue of notifications that have not yet been sent to a user */
  public Queue<String> getStoredNotifications() {
    return storedNotifications;
  }

  /** Adds a notification to the queue of notifications associated with a user */
  public void addNotification(String notification) {
    storedNotifications.add(notification);
    PersistentStorageAgent.getInstance().updateUserEntityStoredNotifications(this);
  }

  /** Clears the notification queue for a user */
  public void clearNotifications() {
    while (!storedNotifications.isEmpty()) {
      storedNotifications.remove();
    }
    PersistentStorageAgent.getInstance().updateUserEntityStoredNotifications(this);
  }

  public void setNotifications(Queue<String> newNotifications) {
    storedNotifications = newNotifications;
  }

  /** Returns profile privacy of a user; Either allContent, someContent, or noContent. */
  public String getProfilePrivacy() {
    return profilePrivacy;
  }

  /*
   * Returns activity feed privacy of a user; Either allContent, someContent, or noContent.
   */
  public String getActivityFeedPrivacy() {
    return activityFeedPrivacy;
  }

  /** Returns the name of the profile picture file of a user */
  public String getProfilePicture() {
    return profilePicture;
  }
  /** Returns the list of users that are in the same conversations as User */
  public List<UUID> getConversationFriends() {
    return conversationFriends;
  }

  public void setConversationFriends(List<String> users) {
    List<UUID> newUsers = new ArrayList<>();
    for (String userId : users) {
      UUID id = UUID.fromString(userId);
      newUsers.add(id);
    }
    conversationFriends = newUsers;
  }

  public List<String> getUserIdsAsStrings() {
    List<String> ids = new ArrayList<>();
    for (UUID user : this.conversationFriends) {
      ids.add(user.toString());
    }
    return ids;
  }

  /** Sets the email of this user with a provided email */
  public void setEmail(String newEmail) {
    email = newEmail;
    PersistentStorageAgent.getInstance().updateUserEntityEmail(this);
  }

  /** Sets the password of this user with a provided hashed password */
  public void setPassword(String newHashedPassword) {
    hashedPassword = newHashedPassword;
    PersistentStorageAgent.getInstance().updateUserEntityPassword(this);
  }

  /** Sets the bio of this user with a provided bio */
  public void setBio(String newBio) {
    biography = newBio;
    PersistentStorageAgent.getInstance().updateUserEntityBiography(this);
  }

  /** Sets the name of the profile image file in Cloud Datastore */
  public void setProfilePicture(String newImageName) {
    profilePicture = newImageName;
    PersistentStorageAgent.getInstance().updateUserEntityProfilePicture(this);
  }

  /**
   * Sets the notification frequency of a user. String inputs must be in one of the following four
   * formats: "everyMessage", "everyHour", "everyFourHours", "everyDay"
   */
  public void setNotificationFrequency(String newNotificationFrequency) {
    notificationFrequency = newNotificationFrequency;
    PersistentStorageAgent.getInstance().updateUserEntityNotificationFrequency(this);
  }

  /** Sets the notification status of a user */
  public void setNotificationStatus(Boolean newNotifications) {
    notifications = newNotifications;
    PersistentStorageAgent.getInstance().updateUserEntityNotificationStatus(this);
  }

  /** Sets the profile privacy of a user */
  public void setProfilePrivacy(String privacy) {
    profilePrivacy = privacy;
    PersistentStorageAgent.getInstance().updateUserEntityProfilePrivacy(this);
  }

  /** Sets the activity feed privacy of a user */
  public void setActivityFeedPrivacy(String privacy) {
    activityFeedPrivacy = privacy;
    PersistentStorageAgent.getInstance().updateUserEntityActivityFeedPrivacy(this);
  }

  /** Adds the conversationFriends of a user */
  public void addConversationFriend(User newConversationFriend) {
    conversationFriends.add(newConversationFriend.getId());
    PersistentStorageAgent.getInstance().updateUserEntityConversationFriends(this);
  }

  /** Removes the conversationFriends of a user */
  public void removeConversationFriend(User oldConversationFriend) {
    conversationFriends.remove(oldConversationFriend.getId());
    PersistentStorageAgent.getInstance().updateUserEntityConversationFriends(this);
  }
}
