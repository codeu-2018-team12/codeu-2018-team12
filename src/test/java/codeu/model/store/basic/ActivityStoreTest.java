package codeu.model.store.basic;

import codeu.model.data.Activity;
import codeu.model.data.User;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ActivityStoreTest {

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
    mockPersistentStorageAgent = Mockito.mock(PersistentStorageAgent.class);
    activityStore = ActivityStore.getTestInstance(mockPersistentStorageAgent);

    final List<Activity> activityList = new ArrayList<>();
    activityList.add(ACTIVITY_ONE);
    activityList.add(ACTIVITY_TWO);
    activityStore.setActivities(activityList);
  }

  @Test
  public void testGetActivityWithId_found() {
    Activity resultActivity = activityStore.getActivityWithId(ACTIVITY_ONE.getId());

    assertEquals(ACTIVITY_ONE, resultActivity);
  }

  @Test
  public void testGetAllPermittedActivitesSorted_Permited() {
    List<Activity> permittedActivities = activityStore.getAllPermittedActivitiesSorted(USER_ONE);
    Assert.assertEquals(permittedActivities.size(), 2);
    assertEquals(permittedActivities.get(0), ACTIVITY_ONE);
    assertEquals(permittedActivities.get(1), ACTIVITY_TWO);
  }

  @Test
  public void testGetAllPermittedActivitesSorted_NotPermited() {
    List<Activity> permittedActivities = activityStore.getAllPermittedActivitiesSorted(USER_TWO);
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
  public void testGetActivitiesPerPrivacy() {

  }
}
