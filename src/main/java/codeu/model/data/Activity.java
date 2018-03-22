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

/**
 * Class representing a conversation, which can be thought of as a chat room. Conversations are
 * created by a User and contain Messages.
 */
public class Activity {
  public final UUID id;
  public final UUID user;
  public final UUID conversationId;
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
  public Activity(
      UUID id,
      UUID userId,
      UUID conversationId,
      Instant creation,
      String activityType,
      String activityMessage) {
    this.id = id;
    this.user = userId;
    this.conversationId = conversationId;
    this.creation = creation;
    this.activityType = activityType;
    this.activityMessage = activityMessage;
  }

  /** Returns the ID of this activity */
  public UUID getId() {
    return id;
  }

  /** Returns the ID of the user associated with this activity */
  public UUID getUserId() {
    return user;
  }

  /**
   * Returns the ID of the conversation associated with this activity (Will return a nil/empty UUID
   * if no conversation associated)
   */
  public UUID getConversationId() {
    return conversationId;
  }

  /** Returns the creation time of this activity */
  public Instant getCreationTime() {
    return creation;
  }

  /**
   * This method returns a string describing an activity type of the following five formats: 1.
   * joinedApp (user newly created an account on the chat app), 2. joinedConvo (user joined an
   * existing conversation), 3. leftConvo (user left a conversation), 4. createdConvo (user created
   * a new conversation), 5. messageSent (user sent a message in a conversation)
   */
  public String getActivityType() {
    return activityType;
  }

  /** Returns the message of this activity. */
  public String getActivityMessage() {
    return activityMessage;
  }
}
