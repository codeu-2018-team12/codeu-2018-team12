package codeu.model.store.persistence;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;

import codeu.model.data.Activity;
import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.UserStore;
import com.google.appengine.api.datastore.*;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.time.Instant;
import java.util.ArrayList;
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
    User inputUserOne =
        new User(idOne, nameOne, passwordOne, null, creationOne, "codeUChatTestEmail@gmail.com");

    UUID idTwo = UUID.randomUUID();
    String nameTwo = "test_username_two";
    String passwordTwo = "test_password_two";
    Instant creationTwo = Instant.ofEpochMilli(2000);
    User inputUserTwo =
        new User(idTwo, nameTwo, passwordTwo, null, creationTwo, "codeUChatTestEmail@gmail.com");

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
    User inputUserOne =
        new User(
            idOne, nameOne, hashedPasswordOne, null, creationOne, "codeUChatTestEmail@gmail.com");

    UUID idTwo = UUID.randomUUID();
    String nameTwo = "test_username_two";
    String passwordTwo = "test_password_two";
    String hashedPasswordTwo = BCrypt.hashpw(passwordTwo, BCrypt.gensalt());
    Instant creationTwo = Instant.ofEpochMilli(2000);
    User inputUserTwo =
        new User(
            idTwo, nameTwo, hashedPasswordTwo, null, creationTwo, "codeUChatTestEmail@gmail.com");

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
    User ownerOne =
        new User(
            UUID.randomUUID(),
            "test_username_one",
            "password one",
            "test biography",
            Instant.ofEpochMilli(1000),
            "codeUChatTestEmail@gmail.com");
    UUID ownerOneUUID = ownerOne.getId();
    UserStore.getInstance().addUser(ownerOne);
    String titleOne = "Test_Title";
    Instant creationOne = Instant.ofEpochMilli(1000);
    Conversation inputConversationOne =
        new Conversation(idOne, ownerOneUUID, titleOne, creationOne, true);

    // save
    ConversationStore.getInstance().addConversation(inputConversationOne);

    UUID idTwo = UUID.randomUUID();
    User ownerTwo =
        new User(
            UUID.randomUUID(),
            "test_username_one",
            "password one",
            "test biography",
            Instant.ofEpochMilli(1000),
            "codeUChatTestEmail@gmail.com");
    UUID ownerTwoUUID = ownerOne.getId();
    UserStore.getInstance().addUser(ownerTwo);
    String titleTwo = "Test_Title_Two";
    Instant creationTwo = Instant.ofEpochMilli(2000);
    Conversation inputConversationTwo =
        new Conversation(idTwo, ownerTwoUUID, titleTwo, creationTwo, false);

    // save
    ConversationStore.getInstance().addConversation(inputConversationTwo);

    // load
    List<Conversation> resultConversations = persistentDataStore.loadConversations();

    // confirm that what we saved matches what we loaded
    Conversation resultConversationOne = resultConversations.get(0);
    Assert.assertEquals(idOne, resultConversationOne.getId());
    Assert.assertEquals(ownerOneUUID, resultConversationOne.getOwnerId());
    Assert.assertEquals(titleOne, resultConversationOne.getTitle());
    Assert.assertEquals(creationOne, resultConversationOne.getCreationTime());

    Conversation resultConversationTwo = resultConversations.get(1);
    Assert.assertEquals(idTwo, resultConversationTwo.getId());
    Assert.assertEquals(ownerTwoUUID, resultConversationTwo.getOwnerId());
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
        new Message(idOne, conversationOne, authorOne, contentOne, creationOne, false);

    UUID idTwo = UUID.randomUUID();
    UUID conversationTwo = UUID.randomUUID();
    UUID authorTwo = UUID.randomUUID();
    String contentTwo = "test content one";
    Instant creationTwo = Instant.ofEpochMilli(2000);
    Message inputMessageTwo =
        new Message(idTwo, conversationTwo, authorTwo, contentTwo, creationTwo, false);

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
    ArrayList<UUID> usersOne = new ArrayList<UUID>();
    usersOne.add(userIdOne);
    Activity inputActivityOne =
        new Activity(
            idOne,
            userIdOne,
            conversationIdOne,
            creationOne,
            messageTypeOne,
            messageOne,
            usersOne,
            true);
    UUID idTwo = UUID.randomUUID();
    UUID userIdTwo = UUID.randomUUID();
    UUID conversationIdTwo = UUID.randomUUID();
    Instant creationTwo = Instant.ofEpochMilli(1000);
    String messageTypeTwo = "messageSent";
    String messageTwo =
        "Grace sent a message in Programming Chat: \"I've always been more interested "
            + "in the future than in the past.\"";
    ArrayList<UUID> usersTwo = new ArrayList<UUID>();
    usersTwo.add(userIdTwo);
    Activity inputActivityTwo =
        new Activity(
            idTwo,
            userIdTwo,
            conversationIdTwo,
            creationTwo,
            messageTypeTwo,
            messageTwo,
            usersTwo,
            false);
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
    Assert.assertEquals(usersOne, resultActivityOne.getUsers());
    Assert.assertEquals(true, resultActivityOne.getIsPublic());

    Activity resultActivityTwo = resultActivities.get(1);
    Assert.assertEquals(idTwo, resultActivityTwo.getId());
    Assert.assertEquals(userIdTwo, resultActivityTwo.getUserId());
    Assert.assertEquals(conversationIdTwo, resultActivityTwo.getConversationId());
    Assert.assertEquals(creationTwo, resultActivityTwo.getCreationTime());
    Assert.assertEquals(messageTypeTwo, resultActivityTwo.getActivityType());
    Assert.assertEquals(messageTwo, resultActivityTwo.getActivityMessage());
    Assert.assertEquals(usersTwo, resultActivityTwo.getUsers());
    Assert.assertEquals(false, resultActivityTwo.getIsPublic());
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
    testEntity.setProperty("email", "testEmail@gmail.com");
    ds.put(testEntity);

    Entity testEntity1 = new Entity("chat-users");
    testEntity1.setProperty("uuid", UUID.randomUUID().toString());
    testEntity1.setProperty("username", "test username1");
    testEntity1.setProperty("password", "test password1");
    testEntity1.setProperty("biography", "test bio1");
    testEntity1.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    testEntity1.setProperty("email", "testEmail1@gmail.com");
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
    testEntity.setProperty("isPublic", Boolean.toString(true));
    ds.put(testEntity);

    Entity testEntity1 = new Entity("chat-conversations");
    testEntity1.setProperty("uuid", UUID.randomUUID().toString());
    testEntity1.setProperty("owner_uuid", UUID.randomUUID().toString());
    testEntity1.setProperty("title", "test title");
    testEntity1.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    testEntity.setProperty("isPublic", Boolean.toString(false));
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
  public void testUpdateEntityConversationAdd()
      throws PersistentDataStoreException, EntityNotFoundException {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    Entity testEntity = new Entity("chat-conversations");
    String testUUID = UUID.randomUUID().toString();
    testEntity.setProperty("uuid", testUUID);
    String ownerId = UUID.randomUUID().toString();
    testEntity.setProperty("owner_uuid", ownerId);
    testEntity.setProperty("title", "test title");
    testEntity.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    testEntity.setProperty("isPublic", Boolean.toString(false));
    List<String> ids = new ArrayList<>();
    ids.add(ownerId);
    testEntity.setProperty("users", ids);
    ds.put(testEntity);

    Query testQuery =
        new Query("chat-conversations")
            .setFilter(new Query.FilterPredicate("uuid", Query.FilterOperator.EQUAL, testUUID));
    PreparedQuery preparedTestQuery = ds.prepare(testQuery);
    Entity retrievedEntity = preparedTestQuery.asSingleEntity();

    @SuppressWarnings("unchecked")
    List<String> users = (List<String>) retrievedEntity.getProperty("users");
    users.add(UUID.randomUUID().toString());
    retrievedEntity.setProperty("users", users);
    ds.put(retrievedEntity);

    Key entityKey = retrievedEntity.getKey();
    Entity retrievedEntityAfter = ds.get(entityKey);
    @SuppressWarnings("unchecked")
    List<String> updatedUsers = (List<String>) retrievedEntityAfter.getProperty("users");
    assertEquals(updatedUsers, users);
  }

  @Test
  public void testUpdateEntityConversationRemove()
      throws PersistentDataStoreException, EntityNotFoundException {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    Entity testEntity = new Entity("chat-conversations");
    String testUUID = UUID.randomUUID().toString();
    testEntity.setProperty("uuid", testUUID);
    String ownerId = UUID.randomUUID().toString();
    testEntity.setProperty("owner_uuid", ownerId);
    testEntity.setProperty("title", "test title");
    testEntity.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    testEntity.setProperty("isPublic", Boolean.toString(true));
    List<String> ids = new ArrayList<>();
    ids.add(ownerId);
    testEntity.setProperty("users", ids);
    ds.put(testEntity);

    Query testQuery =
        new Query("chat-conversations")
            .setFilter(new Query.FilterPredicate("uuid", Query.FilterOperator.EQUAL, testUUID));
    PreparedQuery preparedTestQuery = ds.prepare(testQuery);
    Entity retrievedEntity = preparedTestQuery.asSingleEntity();

    List<String> users = (List<String>) retrievedEntity.getProperty("users");
    users.remove(users.get(0));
    retrievedEntity.setProperty("users", users);
    ds.put(retrievedEntity);

    Key entityKey = retrievedEntity.getKey();
    Entity retrievedEntityAfter = ds.get(entityKey);
    List<String> updatedUsers = (List<String>) retrievedEntityAfter.getProperty("users");
    assertEquals(updatedUsers, users);
  }

  @Test
  public void testUpdateEntityUser() throws PersistentDataStoreException, EntityNotFoundException {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    Entity testEntity = new Entity("chat-users");
    String testUUID = UUID.randomUUID().toString();
    testEntity.setProperty("uuid", testUUID);
    testEntity.setProperty("username", "test username");
    testEntity.setProperty("password", "test password");
    testEntity.setProperty("biography", "test bio");
    testEntity.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    testEntity.setProperty("email", "testEmail@gmail.com");
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

  @Test
  public void testUpdateUserEntityProfilePrivacy()
      throws PersistentDataStoreException, EntityNotFoundException {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    Entity testEntity = new Entity("chat-users");
    String testUUID = UUID.randomUUID().toString();
    testEntity.setProperty("uuid", testUUID);
    testEntity.setProperty("username", "test username");
    testEntity.setProperty("password", "test password");
    testEntity.setProperty("biography", "test bio");
    testEntity.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    testEntity.setProperty("email", "testEmail@gmail.com");
    testEntity.setProperty("profilePrivacy", "allContent");
    ds.put(testEntity);

    Query testQuery =
        new Query("chat-users")
            .setFilter(new Query.FilterPredicate("uuid", Query.FilterOperator.EQUAL, testUUID));
    PreparedQuery preparedTestQuery = ds.prepare(testQuery);
    Entity testResultEntity = preparedTestQuery.asSingleEntity();
    String updatedProfilePrivacy = "someContent";
    testResultEntity.setProperty("profilePrivacy", updatedProfilePrivacy);
    ds.put(testResultEntity);

    Key entityKey = testResultEntity.getKey();
    Entity retrievedEntity = ds.get(entityKey);
    String profilePrivacy = (String) retrievedEntity.getProperty("profilePrivacy");
    assertEquals(updatedProfilePrivacy, profilePrivacy);
  }

  @Test
  public void testUpdateUserEntityActivityFeedPrivacy()
      throws PersistentDataStoreException, EntityNotFoundException {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    Entity testEntity = new Entity("chat-users");
    String testUUID = UUID.randomUUID().toString();
    testEntity.setProperty("uuid", testUUID);
    testEntity.setProperty("username", "test username");
    testEntity.setProperty("password", "test password");
    testEntity.setProperty("biography", "test bio");
    testEntity.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    testEntity.setProperty("email", "testEmail@gmail.com");
    testEntity.setProperty("activityFeedPrivacy", "allContent");
    ds.put(testEntity);

    Query testQuery =
        new Query("chat-users")
            .setFilter(new Query.FilterPredicate("uuid", Query.FilterOperator.EQUAL, testUUID));
    PreparedQuery preparedTestQuery = ds.prepare(testQuery);
    Entity testResultEntity = preparedTestQuery.asSingleEntity();
    String updatedActivityFeedPrivacy = "someContent";
    testResultEntity.setProperty("activityFeedPrivacy", updatedActivityFeedPrivacy);
    ds.put(testResultEntity);

    Key entityKey = testResultEntity.getKey();
    Entity retrievedEntity = ds.get(entityKey);
    String activityFeedPrivacy = (String) retrievedEntity.getProperty("activityFeedPrivacy");
    assertEquals(updatedActivityFeedPrivacy, activityFeedPrivacy);
  }

  @Test
  public void testUpdateUserEntityConversationFriends()
      throws PersistentDataStoreException, EntityNotFoundException {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    Entity testEntity = new Entity("chat-users");
    String testUUID = UUID.randomUUID().toString();
    List<String> testConversationFriends = new ArrayList<>();
    testConversationFriends.add(UUID.randomUUID().toString());
    testConversationFriends.add(UUID.randomUUID().toString());
    testEntity.setProperty("uuid", testUUID);
    testEntity.setProperty("username", "test username");
    testEntity.setProperty("password", "test password");
    testEntity.setProperty("biography", "test bio");
    testEntity.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    testEntity.setProperty("email", "testEmail@gmail.com");
    testEntity.setProperty("conversationFriends", testConversationFriends);
    ds.put(testEntity);

    Query testQuery =
        new Query("chat-users")
            .setFilter(new Query.FilterPredicate("uuid", Query.FilterOperator.EQUAL, testUUID));
    PreparedQuery preparedTestQuery = ds.prepare(testQuery);
    Entity testResultEntity = preparedTestQuery.asSingleEntity();

    @SuppressWarnings("unchecked")
    List<String> updatedTestConversationFriends =
        (List<String>) testResultEntity.getProperty("conversationFriends");
    updatedTestConversationFriends.add(UUID.randomUUID().toString());
    testResultEntity.setProperty("conversationFriends", updatedTestConversationFriends);
    ds.put(testResultEntity);

    Key entityKey = testResultEntity.getKey();
    Entity retrievedEntity = ds.get(entityKey);
    @SuppressWarnings("unchecked")
    List<String> conversationFriends =
        (List<String>) retrievedEntity.getProperty("conversationFriends");
    assertEquals(updatedTestConversationFriends, conversationFriends);
  }

  @Test
  public void testUpdateUserEntityStoredNotifications()
      throws PersistentDataStoreException, EntityNotFoundException {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    Entity testEntity = new Entity("chat-users");
    String testUUID = UUID.randomUUID().toString();
    testEntity.setProperty("uuid", testUUID);
    testEntity.setProperty("username", "test username");
    testEntity.setProperty("password", "test password");
    testEntity.setProperty("biography", "test bio");
    testEntity.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    testEntity.setProperty("email", "testEmail@gmail.com");
    testEntity.setProperty("notifications", true);
    ds.put(testEntity);

    Query testQuery =
        new Query("chat-users")
            .setFilter(new Query.FilterPredicate("uuid", Query.FilterOperator.EQUAL, testUUID));
    PreparedQuery preparedTestQuery = ds.prepare(testQuery);
    Entity testResultEntity = preparedTestQuery.asSingleEntity();
    boolean updatedNotificationStatus = false;
    testResultEntity.setProperty("notifications", updatedNotificationStatus);
    ds.put(testResultEntity);

    Key entityKey = testResultEntity.getKey();
    Entity retrievedEntity = ds.get(entityKey);
    Boolean notificationStatus = (Boolean) retrievedEntity.getProperty("notifications");
    assertEquals(updatedNotificationStatus, notificationStatus);
  }

  @Test
  public void testUpdateUserNotificationFrequency()
      throws PersistentDataStoreException, EntityNotFoundException {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    Entity testEntity = new Entity("chat-users");
    String testUUID = UUID.randomUUID().toString();
    testEntity.setProperty("uuid", testUUID);
    testEntity.setProperty("username", "test username");
    testEntity.setProperty("password", "test password");
    testEntity.setProperty("biography", "test bio");
    testEntity.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    testEntity.setProperty("email", "testEmail@gmail.com");
    testEntity.setProperty("notifications", true);
    testEntity.setProperty("notificationFrequency", "everyMessage");
    ds.put(testEntity);

    Query testQuery =
        new Query("chat-users")
            .setFilter(new Query.FilterPredicate("uuid", Query.FilterOperator.EQUAL, testUUID));
    PreparedQuery preparedTestQuery = ds.prepare(testQuery);
    Entity testResultEntity = preparedTestQuery.asSingleEntity();
    String updatedNotificationFrequency = "everyHour";
    testResultEntity.setProperty("notificationFrequency", updatedNotificationFrequency);
    ds.put(testResultEntity);

    Key entityKey = testResultEntity.getKey();
    Entity retrievedEntity = ds.get(entityKey);
    String notificationFrequency = (String) retrievedEntity.getProperty("notificationFrequency");
    assertEquals(updatedNotificationFrequency, notificationFrequency);
  }

  @Test
  public void testUpdateUserEntityEmail()
      throws PersistentDataStoreException, EntityNotFoundException {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    Entity testEntity = new Entity("chat-users");
    String testUUID = UUID.randomUUID().toString();
    testEntity.setProperty("uuid", testUUID);
    testEntity.setProperty("username", "test username");
    testEntity.setProperty("password", "test password");
    testEntity.setProperty("biography", "test bio");
    testEntity.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    testEntity.setProperty("email", "testEmail@gmail.com");
    ds.put(testEntity);

    Query testQuery =
        new Query("chat-users")
            .setFilter(new Query.FilterPredicate("uuid", Query.FilterOperator.EQUAL, testUUID));
    PreparedQuery preparedTestQuery = ds.prepare(testQuery);
    Entity testResultEntity = preparedTestQuery.asSingleEntity();
    String updatedEmail = "updatedEmail@gmail.com";
    testResultEntity.setProperty("email", updatedEmail);
    ds.put(testResultEntity);

    Key entityKey = testResultEntity.getKey();
    Entity retrievedEntity = ds.get(entityKey);
    String email = (String) retrievedEntity.getProperty("email");
    assertEquals(updatedEmail, email);
  }

  @Test
  public void testUpdateUserEntityPassword()
      throws PersistentDataStoreException, EntityNotFoundException {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    Entity testEntity = new Entity("chat-users");
    String testUUID = UUID.randomUUID().toString();
    testEntity.setProperty("uuid", testUUID);
    testEntity.setProperty("username", "test username");
    testEntity.setProperty("password", "test password");
    testEntity.setProperty("biography", "test bio");
    testEntity.setProperty("creation_time", Instant.ofEpochMilli(1000).toString());
    testEntity.setProperty("email", "testEmail@gmail.com");
    ds.put(testEntity);

    Query testQuery =
        new Query("chat-users")
            .setFilter(new Query.FilterPredicate("uuid", Query.FilterOperator.EQUAL, testUUID));
    PreparedQuery preparedTestQuery = ds.prepare(testQuery);
    Entity testResultEntity = preparedTestQuery.asSingleEntity();
    String updatedPassword = "updatedPassword";
    testResultEntity.setProperty("password", updatedPassword);
    ds.put(testResultEntity);

    Key entityKey = testResultEntity.getKey();
    Entity retrievedEntity = ds.get(entityKey);
    String password = (String) retrievedEntity.getProperty("password");
    assertEquals(updatedPassword, password);
  }
}
