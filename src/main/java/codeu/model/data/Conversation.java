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

import codeu.model.store.basic.UserStore;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class representing a conversation, which can be thought of as a chat room. Conversations are
 * created by a User and contain Messages.
 */
public class Conversation {
  private final UUID id;
  private final UUID owner;
  private final Instant creation;
  private final String title;
  private UserStore userStore = UserStore.getInstance();
  private List<User> conversationUsers = new ArrayList<>();
  private boolean isPublic = true;
  /**
   * Constructs a new Conversation.
   *
   * @param id the ID of this Conversation
   * @param owner the ID of the User who created this Conversation
   * @param title the title of this Conversation
   * @param creation the creation time of this Conversation
   */
  public Conversation(UUID id, UUID owner, String title, Instant creation) {
    this.id = id;
    this.owner = owner;
    this.creation = creation;
    this.title = title;
    this.conversationUsers.add(userStore.getUser(owner));
  }

  /** Returns the ID of this Conversation. */
  public UUID getId() {
    return id;
  }

  /** Returns the ID of the User who created this Conversation. */
  public UUID getOwnerId() {
    return owner;
  }

  /** Returns the title of this Conversation. */
  public String getTitle() {
    return title;
  }

  /** Returns the creation time of this Conversation. */
  public Instant getCreationTime() {
    return creation;
  }

  /** Returns the set of users in this Conversation. */
  public List<User> getConversationUsers() {
    return conversationUsers;
  }

  /**
   * Returns the set of users in this Conversation as a list of strings indicating the UUID of each
   * user. For use in persistentDataStore
   */
  public List<String> getUserIdsAsStrings() {
    List<String> ids = new ArrayList<>();
    for (User user : conversationUsers) {
      ids.add(user.getId().toString());
    }
    return ids;
  }
  /** Adds a user to a conversation */
  public void addUser(User user) {
    conversationUsers.add(user);
    PersistentStorageAgent.getInstance().updateEntity(this);
  }

  /** Removes a user from a conversation */
  public void removeUser(User user) {
    conversationUsers.remove(user);
    PersistentStorageAgent.getInstance().updateEntity(this);
  }

  /** Updates the list of users from a list of user Ids */
  public void setUsers(List<String> users) {
    List<User> newUsers = new ArrayList<>();
    for (String userId : users) {
      UUID id = UUID.fromString(userId);
      newUsers.add(userStore.getUser(id));
    }
    conversationUsers = newUsers;
  }

  public boolean getIsPublic() {
    return isPublic;
  }

  public void setIsPublic(boolean isPublic) {
    this.isPublic = isPublic;
  }

  public boolean hasPermission(User user) {
    if (isPublic) {
      return true;
    }
    return conversationUsers.contains(user);
  }
}
