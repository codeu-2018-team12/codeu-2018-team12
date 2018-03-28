package codeu.model.store.persistence;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;

import codeu.model.data.Activity;
import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import com.google.appengine.api.datastore.*;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mindrot.jbcrypt.*;

/**
 * Test class for PersistentDataStore. The PersistentDataStore class relies on DatastoreService,
 * which in turn relies on being deployed in an AppEngine context. Since this test doesn't run in
 * AppEngine, we use LocalServiceTestHelper to do all of the AppEngine setup so we can test. More
 * info: https://cloud.google.com/appengine/docs/standard/java/tools/localunittesting
 */
public class PersistentDataStoreTest {

  private PersistentDataStore persistentDataStore;
  private DatastoreService datastore;
  private final LocalServiceTestHelper appEngineTestHelper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setup() {
    appEngineTestHelper.setUp();
    persistentDataStore = new PersistentDataStore();
  }

  @After
  public void tearDown() {
    appEngineTestHelper.tearDown();
  }

  @Test
  public void testSaveAndLoadUsersNoPassHash() throws PersistentDataStoreException {
    UUID idOne = UUID.randomUUID();
    String nameOne = "test_username_one";
    String passwordOne = "test_password_one";
    Instant creationOne = Instant.ofEpochMilli(1000);
    User inputUserOne = new User(idOne, nameOne, passwordOne, null, creationOne);

    UUID idTwo = UUID.randomUUID();
    String nameTwo = "test_username_two";
    String passwordTwo = "test_password_two";
    Instant creationTwo = Instant.ofEpochMilli(2000);
    User inputUserTwo = new User(idTwo, nameTwo, passwordTwo, null, creationTwo);

    // save
    persistentDataStore.writeThrough(inputUserOne);
    persistentDataStore.writeThrough(inputUserTwo);

    // load
    List<User> resultUsers = persistentDataStore.loadUsers();

    // confirm that what we saved matches what we loaded
    User resultUserOne = resultUsers.get(0);
    Assert.assertEquals(idOne, resultUserOne.getId());
    Assert.assertEquals(nameOne, resultUserOne.getName());
    Assert.assertTrue(BCrypt.checkpw(passwordOne, resultUserOne.getPassword()));
    Assert.assertEquals(creationOne, resultUserOne.getCreationTime());

    User resultUserTwo = resultUsers.get(1);
    Assert.assertEquals(idTwo, resultUserTwo.getId());
    Assert.assertEquals(nameTwo, resultUserTwo.getName());
    Assert.assertTrue(BCrypt.checkpw(passwordTwo, resultUserTwo.getPassword()));
    Assert.assertEquals(creationTwo, resultUserTwo.getCreationTime());
  }

  @Test
  public void testSaveAndLoadUsersPassHash() throws PersistentDataStoreException {
    UUID idOne = UUID.randomUUID();
    String nameOne = "test_username_one";
    String passwordOne = "test_password_one";
    String hashedPasswordOne = BCrypt.hashpw(passwordOne, BCrypt.gensalt());
    Instant creationOne = Instant.ofEpochMilli(1000);
    User inputUserOne = new User(idOne, nameOne, hashedPasswordOne, null, creationOne);

    UUID idTwo = UUID.randomUUID();
    String nameTwo = "test_username_two";
    String passwordTwo = "test_password_two";
    String hashedPasswordTwo = BCrypt.hashpw(passwordTwo, BCrypt.gensalt());
    Instant creationTwo = Instant.ofEpochMilli(2000);
    User inputUserTwo = new User(idTwo, nameTwo, hashedPasswordTwo, null, creationTwo);

    // save
    persistentDataStore.writeThrough(inputUserOne);
    persistentDataStore.writeThrough(inputUserTwo);

    // load
    List<User> resultUsers = persistentDataStore.loadUsers();

    // confirm that what we saved matches what we loaded
    User resultUserOne = resultUsers.get(0);
    Assert.assertEquals(idOne, resultUserOne.getId());
    Assert.assertEquals(nameOne, resultUserOne.getName());
    Assert.assertTrue(BCrypt.checkpw(passwordOne, resultUserOne.getPassword()));
    Assert.assertEquals(creationOne, resultUserOne.getCreationTime());

    User resultUserTwo = resultUsers.get(1);
    Assert.assertEquals(idTwo, resultUserTwo.getId());
    Assert.assertEquals(nameTwo, resultUserTwo.getName());
    Assert.assertTrue(BCrypt.checkpw(passwordTwo, resultUserTwo.getPassword()));
    Assert.assertEquals(creationTwo, resultUserTwo.getCreationTime());
  }

  @Test
  public void testSaveAndLoadConversations() throws PersistentDataStoreException {
    UUID idOne = UUID.randomUUID();
    UUID ownerOne = UUID.randomUUID();
    String titleOne = "Test_Title";
    Instant creationOne = Instant.ofEpochMilli(1000);
    Conversation inputConversationOne = new Conversation(idOne, ownerOne, titleOne, creationOne);

    UUID idTwo = UUID.randomUUID();
    UUID ownerTwo = UUID.randomUUID();
    String titleTwo = "Test_Title_Two";
    Instant creationTwo = Instant.ofEpochMilli(2000);
    Conversation inputConversationTwo = new Conversation(idTwo, ownerTwo, titleTwo, creationTwo);

    // save
    persistentDataStore.writeThrough(inputConversationOne);
    persistentDataStore.writeThrough(inputConversationTwo);

    // load
    List<Conversation> resultConversations = persistentDataStore.loadConversations();

    // confirm that what we saved matches what we loaded
    Conversation resultConversationOne = resultConversations.get(0);
    Assert.assertEquals(idOne, resultConversationOne.getId());
    Assert.assertEquals(ownerOne, resultConversationOne.getOwnerId());
    Assert.assertEquals(titleOne, resultConversationOne.getTitle());
    Assert.assertEquals(creationOne, resultConversationOne.getCreationTime());

    Conversation resultConversationTwo = resultConversations.get(1);
    Assert.assertEquals(idTwo, resultConversationTwo.getId());
    Assert.assertEquals(ownerTwo, resultConversationTwo.getOwnerId());
    Assert.assertEquals(titleTwo, resultConversationTwo.getTitle());
    Assert.assertEquals(creationTwo, resultConversationTwo.getCreationTime());
  }

  @Test
  public void testSaveAndLoadMessages() throws PersistentDataStoreException {
    UUID idOne = UUID.randomUUID();
    UUID conversationOne = UUID.randomUUID();
    UUID authorOne = UUID.randomUUID();
    String contentOne = "test content one";
    Instant creationOne = Instant.ofEpochMilli(1000);
    Message inputMessageOne =
        new Message(idOne, conversationOne, authorOne, contentOne, creationOne);

    UUID idTwo = UUID.randomUUID();
    UUID conversationTwo = UUID.randomUUID();
    UUID authorTwo = UUID.randomUUID();
    String contentTwo = "test content one";
    Instant creationTwo = Instant.ofEpochMilli(2000);
    Message inputMessageTwo =
        new Message(idTwo, conversationTwo, authorTwo, contentTwo, creationTwo);

    // save
    persistentDataStore.writeThrough(inputMessageOne);
    persistentDataStore.writeThrough(inputMessageTwo);

    // load
    List<Message> resultMessages = persistentDataStore.loadMessages();

    // confirm that what we saved matches what we loaded
    Message resultMessageOne = resultMessages.get(0);
    Assert.assertEquals(idOne, resultMessageOne.getId());
    Assert.assertEquals(conversationOne, resultMessageOne.getConversationId());
    Assert.assertEquals(authorOne, resultMessageOne.getAuthorId());
    Assert.assertEquals(contentOne, resultMessageOne.getContent());
    Assert.assertEquals(creationOne, resultMessageOne.getCreationTime());

    Message resultMessageTwo = resultMessages.get(1);
    Assert.assertEquals(idTwo, resultMessageTwo.getId());
    Assert.assertEquals(conversationTwo, resultMessageTwo.getConversationId());
    Assert.assertEquals(authorTwo, resultMessageTwo.getAuthorId());
    Assert.assertEquals(contentTwo, resultMessageTwo.getContent());
    Assert.assertEquals(creationTwo, resultMessageTwo.getCreationTime());
  }

  @Test
  public void testSaveAndLoadActivities() throws PersistentDataStoreException {
    UUID idOne = UUID.randomUUID();
    UUID userIdOne = UUID.randomUUID();
    UUID conversationIdOne = UUID.randomUUID();
    Instant creationOne = Instant.ofEpochMilli(1000);
    String messageTypeOne = "joinedApp";
    String messageOne = "Ada joined!";
    Activity inputActivityOne =
        new Activity(idOne, userIdOne, conversationIdOne, creationOne, messageTypeOne, messageOne);

    UUID idTwo = UUID.randomUUID();
    UUID userIdTwo = UUID.randomUUID();
    UUID conversationIdTwo = UUID.randomUUID();
    Instant creationTwo = Instant.ofEpochMilli(1000);
    String messageTypeTwo = "messageSent";
    String messageTwo =
        "Grace sent a message in Programming Chat: \"I've always been more interested "
            + "in the future than in the past.\"";
    Activity inputActivityTwo =
        new Activity(idTwo, userIdTwo, conversationIdTwo, creationTwo, messageTypeTwo, messageTwo);
    // save
    persistentDataStore.writeThrough(inputActivityOne);
    persistentDataStore.writeThrough(inputActivityTwo);

    // load
    List<Activity> resultActivities = persistentDataStore.loadActivities();

    // confirm that what we saved matches what we loaded
    Activity resultActivityOne = resultActivities.get(0);
    Assert.assertEquals(idOne, resultActivityOne.getId());
    Assert.assertEquals(userIdOne, resultActivityOne.getUserId());
    Assert.assertEquals(conversationIdOne, resultActivityOne.getConversationId());
    Assert.assertEquals(creationOne, resultActivityOne.getCreationTime());
    Assert.assertEquals(messageTypeOne, resultActivityOne.getActivityType());
    Assert.assertEquals(messageOne, resultActivityOne.getActivityMessage());

    Activity resultActivityTwo = resultActivities.get(1);
    Assert.assertEquals(idTwo, resultActivityTwo.getId());
    Assert.assertEquals(userIdTwo, resultActivityTwo.getUserId());
    Assert.assertEquals(conversationIdTwo, resultActivityTwo.getConversationId());
    Assert.assertEquals(creationTwo, resultActivityTwo.getCreationTime());
    Assert.assertEquals(messageTypeTwo, resultActivityTwo.getActivityType());
    Assert.assertEquals(messageTwo, resultActivityTwo.getActivityMessage());
  }

  @Test
  public void testWriteThroughUser() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    assertEquals(0, ds.prepare(new Query("chat-users")).countEntities(withLimit(10)));
    Entity testEntity = new Entity("chat-users");
    testEntity.setProperty("uuid", UUID.randomUUID().toString());
    testEntity.setProperty("username", "test username");
    testEntity.setProperty("password", "test password");
    testEntity.setProperty("biography", "test bio");
    testEntity.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    ds.put(testEntity);

    Entity testEntity1 = new Entity("chat-users");
    testEntity1.setProperty("uuid", UUID.randomUUID().toString());
    testEntity1.setProperty("username", "test username1");
    testEntity1.setProperty("password", "test password1");
    testEntity1.setProperty("biography", "test bio1");
    testEntity1.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    ds.put(testEntity1);
    assertEquals(2, ds.prepare(new Query("chat-users")).countEntities(withLimit(10)));
  }

  @Test
  public void testWriteThroughMessage() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    assertEquals(0, ds.prepare(new Query("chat-messages")).countEntities(withLimit(10)));
    Entity testEntity = new Entity("chat-messages");
    testEntity.setProperty("uuid", UUID.randomUUID().toString());
    testEntity.setProperty("conv_uuid", UUID.randomUUID().toString());
    testEntity.setProperty("author_uuid", UUID.randomUUID().toString());
    testEntity.setProperty("content", "test message");
    testEntity.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    ds.put(testEntity);

    Entity testEntity1 = new Entity("chat-messages");
    testEntity1.setProperty("uuid", UUID.randomUUID().toString());
    testEntity1.setProperty("conv_uuid", UUID.randomUUID().toString());
    testEntity1.setProperty("author_uuid", UUID.randomUUID().toString());
    testEntity1.setProperty("content", "test message1");
    testEntity1.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    ds.put(testEntity1);
    assertEquals(2, ds.prepare(new Query("chat-messages")).countEntities(withLimit(10)));
  }

  @Test
  public void testWriteThroughConversation() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    assertEquals(0, ds.prepare(new Query("chat-conversations")).countEntities(withLimit(10)));
    Entity testEntity = new Entity("chat-conversations");
    testEntity.setProperty("uuid", UUID.randomUUID().toString());
    testEntity.setProperty("owner_uuid", UUID.randomUUID().toString());
    testEntity.setProperty("title", "test title");
    testEntity.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    ds.put(testEntity);

    Entity testEntity1 = new Entity("chat-conversations");
    testEntity1.setProperty("uuid", UUID.randomUUID().toString());
    testEntity1.setProperty("owner_uuid", UUID.randomUUID().toString());
    testEntity1.setProperty("title", "test title");
    testEntity1.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    ds.put(testEntity1);
    assertEquals(2, ds.prepare(new Query("chat-conversations")).countEntities(withLimit(10)));
  }

  @Test
  public void testWriteThroughActivity() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    assertEquals(0, ds.prepare(new Query("chat-activities")).countEntities(withLimit(10)));
    Entity testEntity = new Entity("chat-activities");
    testEntity.setProperty("uuid", UUID.randomUUID().toString());
    testEntity.setProperty("member_id", UUID.randomUUID().toString());
    testEntity.setProperty("conversation_id", UUID.randomUUID().toString());
    testEntity.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    testEntity.setProperty("activity_type", "test type");
    testEntity.setProperty("activity_message", "test message");
    ds.put(testEntity);

    Entity testEntity1 = new Entity("chat-activities");
    testEntity1.setProperty("uuid", UUID.randomUUID().toString());
    testEntity1.setProperty("member_id", UUID.randomUUID().toString());
    testEntity1.setProperty("conversation_id", UUID.randomUUID().toString());
    testEntity1.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    testEntity1.setProperty("activity_type", "test type1");
    testEntity1.setProperty("activity_message", "test message1");
    ds.put(testEntity1);
    assertEquals(2, ds.prepare(new Query("chat-activities")).countEntities(withLimit(10)));
  }

  @Test
  public void testUpdateEntitiesUser()
      throws PersistentDataStoreException, EntityNotFoundException {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    Entity testEntity = new Entity("chat-users");
    String testUUID = UUID.randomUUID().toString();
    testEntity.setProperty("uuid", testUUID);
    testEntity.setProperty("username", "test username");
    testEntity.setProperty("password", "test password");
    testEntity.setProperty("biography", "test bio");
    testEntity.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    ds.put(testEntity);

    Query testQuery =
        new Query("chat-users")
            .setFilter(new Query.FilterPredicate("uuid", Query.FilterOperator.EQUAL, testUUID));
    PreparedQuery preparedTestQuery = ds.prepare(testQuery);
    Entity testResultEntity = preparedTestQuery.asSingleEntity();
    String updatedBio = "updated bio";
    testResultEntity.setProperty("biography", updatedBio);
    ds.put(testResultEntity);

    Key entityKey = testResultEntity.getKey();
    Entity retrievedEntity = ds.get(entityKey);
    String biography = (String) retrievedEntity.getProperty("biography");
    assertEquals(updatedBio, biography);
  }
}
