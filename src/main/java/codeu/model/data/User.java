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
import java.util.UUID;

/** Class representing a registered user. */
public class User {
  private final UUID id;
  private final String name;
  private final String hashedPassword;
  private String biography;
  private final Instant creation;
  private String email;

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

  /** Sets the email of this user with a provided email */
  public void setEmail(String newEmail) {
    email = newEmail;
    PersistentStorageAgent.getInstance().updateUserEntityEmail(this);
  }

  /** Sets the bio of this user with a provided bio */
  public void setBio(String newBio) {
    biography = newBio;
    PersistentStorageAgent.getInstance().updateUserEntityBiopgraphy(this);
  }
}
