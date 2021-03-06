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
import java.util.List;

/**
 * This class is the interface between the application and PersistentDataStore, which handles
 * interactions with Google App Engine's Datastore service. Currently this class simply passes
 * function calls through to PersistentDataStore, but this could be modified to make asynchronous
 * calls or to point to a different backend storage system.
 *
 * <p>This is a singleton; the single instance is accessed through getInstance().
 */
public class PersistentStorageAgent {

  private static PersistentStorageAgent instance;

  private final PersistentDataStore persistentDataStore;

  /**
   * Access the persistent storage agent, in order to perform object-level loads and/or stores. Do
   * not call this function from a test; use getTestInstance() instead.
   */
  public static PersistentStorageAgent getInstance() {
    if (instance == null) {
      instance = new PersistentStorageAgent(new PersistentDataStore());
    }
    return instance;
  }

  /**
   * Instance getter function used for testing. Supply a mock for PersistentDataStore.
   *
   * @param mockPersistentDataStore a mock used for testing
   */
  static PersistentStorageAgent getTestInstance(PersistentDataStore mockPersistentDataStore) {
    return new PersistentStorageAgent(mockPersistentDataStore);
  }

  // Private constructor, accessible only through singleton interface
  private PersistentStorageAgent(PersistentDataStore persistentDataStore) {
    this.persistentDataStore = persistentDataStore;
  }

  /**
   * Retrieve all User objects from the Datastore service. The returned list may be empty.
   *
   * @throws PersistentDataStoreException if an error was detected during the load from the
   *     Datastore service
   */
  public List<User> loadUsers() throws PersistentDataStoreException {
    return persistentDataStore.loadUsers();
  }

  /**
   * Retrieve all Conversation objects from the Datastore service. The returned list may be empty.
   *
   * @throws PersistentDataStoreException if an error was detected during the load from the
   *     Datastore service
   */
  public List<Conversation> loadConversations() throws PersistentDataStoreException {
    return persistentDataStore.loadConversations();
  }

  /**
   * Retrieve all Message objects from the Datastore service. The returned list may be empty.
   *
   * @throws PersistentDataStoreException if an error was detected during the load from the
   *     Datastore service
   */
  public List<Message> loadMessages() throws PersistentDataStoreException {
    return persistentDataStore.loadMessages();
  }

  /**
   * Retrieve all Activity objects from the Datastore service. The returned list may be empty.
   *
   * @throws PersistentDataStoreException if an error was detected during the load from the
   *     Datastore service
   */
  public List<Activity> loadActivities() throws PersistentDataStoreException {
    return persistentDataStore.loadActivities();
  }

  /** Write a User object to the Datastore service. */
  public void writeThrough(User user) {
    persistentDataStore.writeThrough(user);
  }

  /** Write a Message object to the Datastore service. */
  public void writeThrough(Conversation conversation) {
    persistentDataStore.writeThrough(conversation);
  }

  /** Write a Conversation object to the Datastore service. */
  public void writeThrough(Message message) {
    persistentDataStore.writeThrough(message);
  }

  /** Write an Activity object to the Datastore service */
  public void writeThrough(Activity activity) {
    persistentDataStore.writeThrough(activity);
  }

  /** Updates a Conversation object in the Datastore service */
  public void updateConversationEntityUsers(Conversation conversation) {
    persistentDataStore.updateConversationEntityUsers(conversation);
  }
  /** Updates the biography of a User object in the Datastore service */
  public void updateUserEntityBiography(User user) {
    persistentDataStore.updateUserEntityBiography(user);
  }

  /** Updates the email of a User object in the Datastore service */
  public void updateUserEntityEmail(User user) {
    persistentDataStore.updateUserEntityEmail(user);
  }

  /** Updates the password of a User object in the Datastore service */
  public void updateUserEntityPassword(User user) {
    persistentDataStore.updateUserEntityPassword(user);
  }

  /** Updates the notification status of a User in the Datastore service */
  public void updateUserEntityNotificationStatus(User user) {
    persistentDataStore.updateUserEntityNotificationStatus(user);
  }

  /** Updates the notification frequency of a User in the Datastore service */
  public void updateUserEntityNotificationFrequency(User user) {
    persistentDataStore.updateUserEntityNotificationFrequency(user);
  }

  /** Updates the stored notifications of a User in the Datastore service */
  public void updateUserEntityStoredNotifications(User user) {
    persistentDataStore.updateUserEntityStoredNotifications(user);
  }

  /** Updates the name of the profile picture of a User in the Datastore service */
  public void updateUserEntityProfilePicture(User user) {
    persistentDataStore.updateUserEntityProfilePicture(user);
  }

  /** Updates the profile privacy of a User in the Datastore service */
  public void updateUserEntityProfilePrivacy(User user) {
    persistentDataStore.updateUserEntityProfilePrivacy(user);
  }

  /** Updates the activity feed privacy of a User in the Datastore service */
  public void updateUserEntityActivityFeedPrivacy(User user) {
    persistentDataStore.updateUserEntityActivityFeedPrivacy(user);
  }

  /** Updates the activity feed privacy of a User in the Datastore service */
  public void updateUserEntityConversationFriends(User user) {
    persistentDataStore.updateUserEntityConversationFriends(user);
  }
}
