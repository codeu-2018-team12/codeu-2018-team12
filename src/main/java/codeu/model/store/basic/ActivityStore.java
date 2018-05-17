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

package codeu.model.store.basic;

import codeu.model.data.Activity;
import codeu.model.data.User;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Store class that uses in-memory data structures to hold values and automatically loads from and
 * saves to PersistentStorageAgent. It's a singleton so all servlet classes can access the same
 * instance.
 */
public class ActivityStore {

  /** Singleton instance of ActivityStore. */
  private static ActivityStore instance;

  private static Comparator<Activity> activityComparator =
      (copvOne, copvTwo) -> copvTwo.getCreationTime().compareTo(copvOne.getCreationTime());

  /**
   * Returns the singleton instance of ActivityStore that should be shared between all servlet
   * classes. Do not call this function from a test; use getTestInstance() instead.
   */
  public static ActivityStore getInstance() {
    if (instance == null) {
      instance = new ActivityStore(PersistentStorageAgent.getInstance());
    }
    return instance;
  }

  /**
   * Retrieves a static sorter that takes in a list of activities and sort them
   *
   * @param activities the list of activities to be sorted
   * @return the sorted list of activities
   */
  public static List<Activity> sort(List<Activity> activities) {
    activities.sort(activityComparator);
    return activities;
  }

  /**
   * Instance getter function used for testing. Supply a mock for PersistentStorageAgent.
   *
   * @param persistentStorageAgent a mock used for testing
   */
  public static ActivityStore getTestInstance(PersistentStorageAgent persistentStorageAgent) {
    return new ActivityStore(persistentStorageAgent);
  }

  /**
   * The PersistentStorageAgent responsible for loading Activities from and saving Activities to
   * Datastore.
   */
  private PersistentStorageAgent persistentStorageAgent;

  /** The in-memory list of Activities. */
  private List<Activity> activities;

  /** This class is a singleton, so its constructor is private. Call getInstance() instead. */
  private ActivityStore(PersistentStorageAgent persistentStorageAgent) {
    this.persistentStorageAgent = persistentStorageAgent;
    activities = new ArrayList<>();
  }

  /**
   * Load a set of randomly-generated Activity objects.
   *
   * @return false if a error occurs.
   */
  public boolean loadTestData() {
    boolean loaded = false;
    try {
      activities.addAll(DefaultDataStore.getInstance().getAllActivities());
      loaded = true;
    } catch (Exception e) {
      loaded = false;
      System.err.println("ERROR: Unable to establish initial store (conversations).");
    }
    return loaded;
  }

  public List<Activity> getAllPermittedActivities(UUID user) {
    ArrayList<Activity> permittedActivities = new ArrayList<>();
    for (Activity act : activities) {
      if (act.hasPermission(user)) {
        permittedActivities.add(act);
      }
    }
    return permittedActivities;
  }

  public List<Activity> getAllPublicActivities() {
    ArrayList<Activity> publicActivities = new ArrayList<>();
    for (Activity act : activities) {
      if (act.getIsPublic()) {
        publicActivities.add(act);
      }
    }
    return publicActivities;
  }

  /**
   * Access list of activities with respect to user privacy settings
   *
   * @param currentUser the current logged in user
   * @param activities1 the list of from which to activities to pull
   * @return list of activities
   */
  public List<Activity> getActivitiesPerPrivacy(User currentUser, List<Activity> activities1) {
    UserStore userstore = UserStore.getInstance();
    List<Activity> activitiesPerPrivacy = new ArrayList<>();
    if (activities1 == null) {
      return activitiesPerPrivacy;
    } else {
      for (Activity activity : activities1) {
        if (activity != null) {
          UUID activityUserID = activity.getUserId();
          User user = userstore.getUser(activityUserID);
          if (user != null && user.equals(currentUser)) {
            activitiesPerPrivacy.add(activity);
          } else if (currentUser != null
              && user != null
              && currentUser.getConversationFriends().contains(activityUserID)
              && (user.getActivityFeedPrivacy().equals("someContent"))) {
            activitiesPerPrivacy.add(activity);
          } else if (user != null && user.getActivityFeedPrivacy().equals("allContent")) {
            activitiesPerPrivacy.add(activity);
          } else if (currentUser != null && activityUserID.equals(currentUser.getId())) {
            activitiesPerPrivacy.add(activity);
          }
        }
      }
      return activitiesPerPrivacy;
    }
  }

  /** Add a new activity to the current set of activities known to the application. */
  public void addActivity(Activity activity) {
    activities.add(activity);
    persistentStorageAgent.writeThrough(activity);
  }

  /** Find and return the activity with the given Id. */
  public Activity getActivityWithId(UUID Id) {
    for (Activity activity : activities) {
      if (activity.getId().equals(Id)) {
        return activity;
      }
    }
    return null;
  }

  public Activity getActivityWithConversationID(UUID conversationID) {
    for (Activity activity : activities) {
      if (activity.getConversationId().equals(conversationID)) {
        return activity;
      }
    }
    return null;
  }

  public List<Activity> getActivitiesWithUserID(UUID userID) {
    ArrayList<Activity> userActivities = new ArrayList<>();
    for (Activity activity : activities) {
      if (activity.getUserId().equals(userID)) {
        userActivities.add(activity);
      }
    }
    return userActivities;
  }

  public List<Activity> getAllPublicActivitiesWithUserId(UUID user) {
    ArrayList<Activity> result = new ArrayList<Activity>();
    for (Activity activity : activities) {
      if (activity.getUserId().equals(user) && activity.getIsPublic()) {
        result.add(activity);
      }
    }
    return result;
  }

  public List<Activity> getAllPermittedActivitiesWithUserId(UUID user, UUID loggedInUser) {
    ArrayList<Activity> result = new ArrayList<Activity>();
    for (Activity activity : activities) {
      if (activity.getUserId().equals(user) && activity.hasPermission(loggedInUser)) {
        result.add(activity);
      }
    }
    return result;
  }

  /** Sets the list of activities stored by this ActivityStore. */
  public void setActivities(List<Activity> activities) {
    this.activities = activities;
  }
}
