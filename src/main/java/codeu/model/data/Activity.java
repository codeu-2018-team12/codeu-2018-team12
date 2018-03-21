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

import java.time.Instant;
import java.util.UUID;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;

/**
 * Class representing a conversation, which can be thought of as a chat room. Conversations are
 * created by a User and contain Messages.
 */
public class Activity {
  public final UUID id;
  public final UUID user;
  public final Instant creation;
  public final String activityType;
  public final String activityMessage;

  /**
   * Constructs a new activity. Invoked when a conversation id is specified
   *
   * @param id the ID of this Activity
   * @param userId the ID of the user who is attached to this activity
   * @param creation the creation time of this activity
   * @param activityType the type of activity represented by this message
   * @param conversationId the ID of the conversation associated with this activity
   */
  public Activity(UUID id, UUID userId, Instant creation, String activityType, UUID conversationId) {
    this.id = id;
    this.user = userId;
    this.creation = creation;
    this.activityType = activityType;

    String message = "";
    User chatUser = UserStore.getInstance().getUser(userId);
    String userName = chatUser.getName();

    Conversation conversation = ConversationStore.getInstance().getConversationWithId(conversationId);
    String conversationName = conversation.getTitle();

    if (activityType.equals("joinedApp")) {

      message = userName + "created an account!";

    } else if (activityType.equals("joinedConvo")) {

      message = userName + "joined the conversation " + conversationName;

    } else if (activityType.equals("leftConvo")) {

      message = userName + "left the conversation " + conversationName;

    } else if (activityType.equals("createdConvo")) {

      message = userName + "created a new conversation: " + conversationName;

    } else {

      String messageBody = MessageStore.getInstance().getMostRecentMessageFromConvo(conversationId);
      message = userName + " sent a message in " + conversationName + ": \" " + messageBody + "\"";

    }
    this.activityMessage = message;
  }

  /**
   * Constructs a new activity. Invoked when a conversation id is not specified
   *
   * @param id the ID of this Activity
   * @param userId the ID of the user who is attached to this activity
   * @param creation the creation time of this activity
   * @param activityType the type of activity represented by this message
   */
  public Activity(UUID id, UUID userId, Instant creation, String activityType) {
    this.id = id;
    this.user = userId;
    this.creation = creation;
    this.activityType = activityType;

    User chatUser = UserStore.getInstance().getUser(userId);
    String userName = chatUser.getName();

    this.activityMessage = userName + "created an account!";
  }

  /** Returns the ID of this activity */
  public UUID getId() {
    return id;
  }

  /** Returns the ID of the user associated with this activity */
  public UUID getUserId() {
    return user;
  }

  /** Returns the creation time of this activity */
  public Instant getCreationTime() {
    return creation;
  }

  /** Returns the type of this activity
   * This method returns activity types of the following four formats:
   * joinedApp - user newly created an account on the chat app
   * joinedConvo - user joined an existing conversation
   * leftConvo - user left a conversation
   * createdConvo - user created a new conversation
   * messageSent - user sent a message in a conversation
   */
  public String getActivityType() { return activityType; }

  /** Returns the message of this activity. */
  public String getActivityMessage() {
    return activityMessage;
  }

}

