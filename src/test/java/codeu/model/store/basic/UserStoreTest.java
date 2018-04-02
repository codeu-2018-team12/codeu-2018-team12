package codeu.model.store.basic;

import codeu.model.data.User;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class UserStoreTest {

  private UserStore userStore;
  private PersistentStorageAgent mockPersistentStorageAgent;

  private final User USER_ONE =
      new User(
          UUID.randomUUID(),
          "test_username_one",
          "password one",
          null,
          Instant.ofEpochMilli(1000),
          "codeUChatTestEmail@gmail.com");
  private final User USER_TWO =
      new User(
          UUID.randomUUID(),
          "test_username_two",
          "password two",
          null,
          Instant.ofEpochMilli(2000),
          "codeUChatTestEmail@gmail.com");
  private final User USER_THREE =
      new User(
          UUID.randomUUID(),
          "test_username_three",
          "password three",
          null,
          Instant.ofEpochMilli(3000),
          "codeUChatTestEmail@gmail.com");
  private final User USER_FOUR =
      new User(
          UUID.randomUUID(), "username_four", "password four",
              null, Instant.ofEpochMilli(4000), "codeUChatTestEmail@gmail.com");
  private final User USER_FIVE =
      new User(UUID.randomUUID(), "test_user", "password five",
              null, Instant.ofEpochMilli(4000), "codeUChatTestEmail@gmail.com");

  @Before
  public void setup() {
    mockPersistentStorageAgent = Mockito.mock(PersistentStorageAgent.class);
    userStore = UserStore.getTestInstance(mockPersistentStorageAgent);

    final List<User> userList = new ArrayList<>();
    userList.add(USER_ONE);
    userList.add(USER_TWO);
    userList.add(USER_THREE);
    userList.add(USER_FOUR);
    userList.add(USER_FIVE);
    userStore.setUsers(userList);
  }

  @Test
  public void testGetUser_byUsername_found() {
    User resultUser = userStore.getUser(USER_ONE.getName());

    assertEquals(USER_ONE, resultUser);
  }

  @Test
  public void testGetUser_byId_found() {
    User resultUser = userStore.getUser(USER_ONE.getId());

    assertEquals(USER_ONE, resultUser);
  }

  @Test
  public void testGetUser_byUsername_notFound() {
    User resultUser = userStore.getUser("fake username");

    Assert.assertNull(resultUser);
  }

  @Test
  public void testGetUser_byId_notFound() {
    User resultUser = userStore.getUser(UUID.randomUUID());

    Assert.assertNull(resultUser);
  }

  @Test
  public void testAddUser() {
    User inputUser = new User(UUID.randomUUID(), "test_username", "password",
            null, Instant.now(), "codeUChatTestEmail@gmail.com");

    userStore.addUser(inputUser);
    User resultUser = userStore.getUser("test_username");

    assertEquals(inputUser, resultUser);
    Mockito.verify(mockPersistentStorageAgent).writeThrough(inputUser);
  }

  @Test
  public void testIsUserRegistered_true() {
    Assert.assertTrue(userStore.isUserRegistered(USER_ONE.getName()));
  }

  @Test
  public void testIsUserRegistered_false() {
    Assert.assertFalse(userStore.isUserRegistered("fake username"));
  }

  @Test
  public void testSearchUsers() {
    List<User> results = userStore.searchUsersSorted("test");
    Assert.assertEquals(results.size(), 4);
    assertEquals(results.get(0), USER_FIVE);
    assertEquals(results.get(1), USER_ONE);
    assertEquals(results.get(2), USER_TWO);
    assertEquals(results.get(3), USER_THREE);
  }

  @Test
  public void testGetUsers() {
    List<User> results = userStore.getUsers();
    Assert.assertEquals(results.size(), 5);
    assertEquals(results.get(0), USER_ONE);
    assertEquals(results.get(1), USER_TWO);
    assertEquals(results.get(2), USER_THREE);
    assertEquals(results.get(3), USER_FOUR);
    assertEquals(results.get(4), USER_FIVE);
  }

  private void assertEquals(User expectedUser, User actualUser) {
    Assert.assertEquals(expectedUser.getId(), actualUser.getId());
    Assert.assertEquals(expectedUser.getName(), actualUser.getName());
    Assert.assertEquals(expectedUser.getPassword(), actualUser.getPassword());
    Assert.assertEquals(expectedUser.getCreationTime(), actualUser.getCreationTime());
  }
}
