package codeu.model.store.basic;

import codeu.model.data.Activity;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ActivityStoreTest {

  private ActivityStore activityStore;
  private PersistentStorageAgent mockPersistentStorageAgent;

  private final Activity ACTIVITY_ONE =
      new Activity(
          UUID.randomUUID(),
          UUID.randomUUID(),
          UUID.randomUUID(),
          Instant.ofEpochMilli(1000),
          "leftConvo",
          "test_message");

  @Before
  public void setup() {
    mockPersistentStorageAgent = Mockito.mock(PersistentStorageAgent.class);
    activityStore = ActivityStore.getTestInstance(mockPersistentStorageAgent);

    final List<Activity> activityList = new ArrayList<>();
    activityList.add(ACTIVITY_ONE);
    activityStore.setActivities(activityList);
  }

  @Test
  public void testGetActivityWithId_found() {
    Activity resultActivity = activityStore.getActivityWithId(ACTIVITY_ONE.getId());

    assertEquals(ACTIVITY_ONE, resultActivity);
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
            "testMessage");

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
}
