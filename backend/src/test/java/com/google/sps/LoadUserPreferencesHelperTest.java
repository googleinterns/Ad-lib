package com.google.sps;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.MatchPreference;
import com.google.sps.data.User;
import com.google.sps.datastore.UserDatastore;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class LoadUserPreferencesHelperTest {
  // Input constants
  private static final String USERNAME_PERSON_A = "persona";
  private static final int DURATION_DEFAULT = 30;
  private static final String ROLE_DEFAULT = "Software engineer";
  private static final String PRODUCT_AREA_DEFAULT = "Ads";
  private static final List<String> INTERESTS_DEFAULT = Arrays.asList("Books");
  private static final MatchPreference MATCH_PREFERENCE_DEFAULT = MatchPreference.ANY;

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private UserDatastore userDatastore;
  @Mock private UsernameService usernameService;
  private LoadUserPreferencesHelper loadUserPreferencesHelper;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() throws IOException {
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    userDatastore = mock(UserDatastore.class);
    usernameService = mock(UsernameService.class);

    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void noUserWithUsernameInDatastore() throws IOException {
    when(response.getWriter()).thenReturn(getWriter());
    when(usernameService.getUsername()).thenReturn(USERNAME_PERSON_A);

    loadUserPreferencesHelper =
        new LoadUserPreferencesHelper(request, response, userDatastore, usernameService);
    loadUserPreferencesHelper.doGet();

    verify(response)
        .setStatus(
            HttpServletResponse.SC_OK,
            "No saved preferences for user with username " + USERNAME_PERSON_A + ".");
  }

  @Test
  public void userWithUsernameExistsInDatastore() throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserDatastore userDatastore = new UserDatastore(datastore);
    when(response.getWriter()).thenReturn(getWriter());
    when(usernameService.getUsername()).thenReturn(USERNAME_PERSON_A);
    User userA =
        new User(
            USERNAME_PERSON_A,
            DURATION_DEFAULT,
            ROLE_DEFAULT,
            PRODUCT_AREA_DEFAULT,
            INTERESTS_DEFAULT,
            MATCH_PREFERENCE_DEFAULT);
    userDatastore.addUser(userA);

    loadUserPreferencesHelper =
        new LoadUserPreferencesHelper(request, response, userDatastore, usernameService);
    loadUserPreferencesHelper.doGet();

    verify(response)
        .setStatus(
            HttpServletResponse.SC_OK,
            "Loading saved preferences for user with username " + USERNAME_PERSON_A + ".");
  }

  /** Mock HttpServletResponse method */
  private PrintWriter getWriter() {
    return new PrintWriter(System.out);
  }
}
