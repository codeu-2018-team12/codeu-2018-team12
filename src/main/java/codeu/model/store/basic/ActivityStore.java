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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Store class that uses in-memory data structures to hold values and automatically loads from and
 * saves to PersistentStorageAgent. It's a singleton so all servlet classes can access the same
 * instance.
 */
public class ActivityStore {

  /** Singleton instance of ActivityStore. */
  private static ActivityStore instance;

  private Comparator<Activity> activityComparator =
      new Comparator<Activity>() {
        public int compare(Activity copvOne, Activity copvTwo) {
          return copvTwo.getCreationTime().compareTo(copvOne.getCreationTime());
        }
      };

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

  /** Access the current set of activities known to the application. */
  public List<Activity> getAllActivities() {
    return activities;
  }

  /** Access the current set of activities known to the application sorted with newest first. */
  public List<Activity> getAllActivitiesSorted() {
    activities.sort(activityComparator);
    return activities;
  }

  public List<Activity> getAllPermittedActivities(UUID user) {
    ArrayList<Activity> permittedActivities = new ArrayList();
    for (Activity act : activities) {
      if (act.hasPermission(user)) {
        permittedActivities.add(act);
      }
    }
    return permittedActivities;
  }

  public List<Activity> getAllPermittedActivitiesSorted(UUID user) {
    ArrayList<Activity> permittedActivities = new ArrayList();
    for (Activity act : activities) {
      if (act.hasPermission(user)) {
        permittedActivities.add(act);
      }
    }
    permittedActivities.sort(activityComparator);
    return permittedActivities;
  }

  public List<Activity> getAllPublicActivities() {
    ArrayList<Activity> publicActivities = new ArrayList();
    for (Activity act : activities) {
      if (act.getIsPublic()) {
        publicActivities.add(act);
      }
    }
    return publicActivities;
  }

  public List<Activity> getAllPublicActivitiesSorted() {
    ArrayList<Activity> publicActivities = new ArrayList();
    for (Activity act : activities) {
      if (act.getIsPublic()) {
        publicActivities.add(act);
      }
    }
    publicActivities.sort(activityComparator);
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
    for (Activity activity : activities1) {
      for (UUID u : activity.getUsers()) {
        User user = userstore.getUser(u);
        if (currentUser != null
            && currentUser.getConversationFriends().contains(u)
            && (user.getActivityFeedPrivacy().equals("someContent"))) {
          activitiesPerPrivacy.add(activity);
        } else if (user.getActivityFeedPrivacy().equals("allContent")) {
          activitiesPerPrivacy.add(activity);
        }
        if (currentUser != null && currentUser.getActivityFeedPrivacy().equals("noContent")) {
          if (activity.getUserId().equals(currentUser.getId())) {
            activitiesPerPrivacy.add(activity);
          }
        }
      }
    }
    // remove any duplicates
    Set<Activity> hashSet = new HashSet<>(activitiesPerPrivacy);
    activitiesPerPrivacy.clear();
    activitiesPerPrivacy.addAll(hashSet);

    activitiesPerPrivacy.sort(activityComparator);
    return activitiesPerPrivacy;
  }

  /** Access a current subset of activities known to the application sorted with newest first. */
  public List<Activity> getActivityListSorted(List<Activity> activityList) {
    activityList.sort(activityComparator);
    return activityList;
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

  public List<Activity> getAllPublicActivitiesWithUserIdSorted(UUID user) {
    ArrayList<Activity> result = new ArrayList<Activity>();
    for (Activity activity : activities) {
      if (activity.getUserId().equals(user) && activity.getIsPublic()) {
        result.add(activity);
      }
    }
    result.sort(activityComparator);
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

  public List<Activity> getAllPermittedActivitiesWithUserIdSorted(UUID user, UUID loggedInUser) {
    ArrayList<Activity> result = new ArrayList<Activity>();
    for (Activity activity : activities) {
      if (activity.getUserId().equals(user) && activity.hasPermission(loggedInUser)) {
        result.add(activity);
      }
    }
    result.sort(activityComparator);
    return result;
  }

  /** Sets the list of activities stored by this ActivityStore. */
  public void setActivities(List<Activity> activities) {
    this.activities = activities;
  }
}
