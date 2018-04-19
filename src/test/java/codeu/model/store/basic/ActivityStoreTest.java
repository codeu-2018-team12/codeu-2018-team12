package codeu.model.store.basic;

import codeu.model.data.Activity;
import codeu.model.data.User;
import codeu.model.store.persistence.PersistentStorageAgent;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static codeu.model.store.basic.ActivityStore.sort;

public class ActivityStoreTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private ActivityStore activityStore;
  private PersistentStorageAgent mockPersistentStorageAgent;

  private final UUID USER_ONE = UUID.randomUUID();
  private final UUID USER_TWO = UUID.randomUUID();

  private List<UUID> users = Arrays.asList(USER_ONE);

  private final Activity ACTIVITY_ONE =
      new Activity(
          UUID.randomUUID(),
          UUID.randomUUID(),
          UUID.randomUUID(),
          Instant.ofEpochMilli(2000),
          "leftConvo",
          "test_message",
          users,
          false);
  private final Activity ACTIVITY_TWO =
      new Activity(
          UUID.randomUUID(),
          UUID.randomUUID(),
          UUID.randomUUID(),
          Instant.ofEpochMilli(1000),
          "leftConvo",
          "test_message",
          new ArrayList<UUID>(),
          true);

  @Before
  public void setup() {
    helper.setUp();
    mockPersistentStorageAgent = Mockito.mock(PersistentStorageAgent.class);
    activityStore = ActivityStore.getTestInstance(mockPersistentStorageAgent);

    final List<Activity> activityList = new ArrayList<>();
    activityList.add(ACTIVITY_ONE);
    activityList.add(ACTIVITY_TWO);
    activityStore.setActivities(activityList);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testGetActivityWithId_found() {
    Activity resultActivity = activityStore.getActivityWithId(ACTIVITY_ONE.getId());

    assertEquals(ACTIVITY_ONE, resultActivity);
  }

  @Test
  public void testGetAllPermittedActivitiesSorted_Permitted() {
    List<Activity> permittedActivities = sort(activityStore.getAllPermittedActivities(USER_ONE));
    Assert.assertEquals(permittedActivities.size(), 2);
    assertEquals(permittedActivities.get(0), ACTIVITY_ONE);
    assertEquals(permittedActivities.get(1), ACTIVITY_TWO);
  }

  @Test
  public void testGetAllPermittedActivitiesSorted_NotPermitted() {
    List<Activity> permittedActivities = sort(activityStore.getAllPermittedActivities(USER_TWO));
    Assert.assertEquals(permittedActivities.size(), 1);
    assertEquals(permittedActivities.get(0), ACTIVITY_TWO);
  }

  @Test
  public void testGetActivityWithId_notFound() {
    // Generates an empty/nil UUID object
    UUID uuid = new UUID(0L, 0L);
    Activity resultActivity = activityStore.getActivityWithId(uuid);

    Assert.assertNull(resultActivity);
  }

  @Test
  public void testAddActivity() {
    UUID activtiyId = UUID.randomUUID();
    Activity inputActivity =
        new Activity(
            activtiyId,
            UUID.randomUUID(),
            UUID.randomUUID(),
            Instant.now(),
            "joinedConvo",
            "testMessage",
            new ArrayList<UUID>(),
            true);

    activityStore.addActivity(inputActivity);
    Activity resultActivity = activityStore.getActivityWithId(activtiyId);

    assertEquals(inputActivity, resultActivity);
    Mockito.verify(mockPersistentStorageAgent).writeThrough(inputActivity);
  }

  private void assertEquals(Activity expectedActivity, Activity actualActivity) {
    Assert.assertEquals(expectedActivity.getId(), actualActivity.getId());
    Assert.assertEquals(expectedActivity.getUserId(), actualActivity.getUserId());
    Assert.assertEquals(expectedActivity.getConversationId(), actualActivity.getConversationId());
    Assert.assertEquals(expectedActivity.getCreationTime(), actualActivity.getCreationTime());
    Assert.assertEquals(expectedActivity.getActivityType(), actualActivity.getActivityType());
    Assert.assertEquals(expectedActivity.getActivityMessage(), actualActivity.getActivityMessage());
  }

  @Test
  public void testGetActivitiesPerPrivacy_General() {
    UUID allContentId = UUID.randomUUID();
    UUID noContentID = UUID.randomUUID();

    User allContentUser =
        new User(
            allContentId,
            "allContentUser",
            "password",
            null,
            Instant.now(),
            "codeUChatTestEmail@gmail.com");

    User noContentUser =
        new User(
            noContentID,
            "noContentUser",
            "password",
            null,
            Instant.now(),
            "codeUChatTestEmail@gmail.com");

    allContentUser.setActivityFeedPrivacy("allContent");
    allContentUser.setProfilePrivacy("allContent");

    noContentUser.setActivityFeedPrivacy("noContent");
    noContentUser.setProfilePrivacy("noContent");

    Activity activity1 =
        new Activity(
            allContentId,
            allContentId,
            allContentId,
            Instant.ofEpochMilli(2000),
            "leftConvo",
            "test_message",
            users,
            true);

    Activity activity2 =
        new Activity(
            allContentId,
            allContentId,
            allContentId,
            Instant.ofEpochMilli(2000),
            "leftConvo",
            "test_message",
            users,
            true);

    Activity activity3 =
        new Activity(
            noContentID,
            noContentID,
            noContentID,
            Instant.ofEpochMilli(2000),
            "leftConvo",
            "test_message",
            users,
            true);

    Activity activity4 =
        new Activity(
            noContentID,
            noContentID,
            noContentID,
            Instant.ofEpochMilli(2000),
            "leftConvo",
            "test_message",
            users,
            true);

    List<Activity> activityList = new ArrayList<>();
    activityList.add(activity1);
    activityList.add(activity2);
    activityList.add(activity3);
    activityList.add(activity4);

    List<Activity> activitiesPerPrivacy =
        activityStore.getActivitiesPerPrivacy(allContentUser, activityList);

    Assert.assertEquals(activity1, activitiesPerPrivacy.get(0));
    Assert.assertEquals(activity2, activitiesPerPrivacy.get(1));
    Assert.assertEquals(2, activitiesPerPrivacy.size());
  }
}
