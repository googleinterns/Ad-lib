package com.google.sps;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.datastore.MatchDatastore;
import com.google.sps.datastore.ParticipantDatastore;
import com.google.sps.datastore.UserDatastore;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class AddParticipantTest {
  // Reference date time of 1/1/20 2pm ET
  private static final ZonedDateTime currentDateTimeET =
      ZonedDateTime.of(
          /* year= */ 2020,
          /* month= */ 1,
          /* date= */ 1,
          /* hour= */ 14,
          /* minute= */ 0,
          /* second= */ 0,
          /* nanosecond= */ 0,
          /* zone= */ ZoneId.of("US/Eastern"));

  // HTTP Request JSON key constants
  private static final String REQUEST_FORM_DETAILS = "formDetails";
  private static final String REQUEST_END_TIME_AVAILABLE = "endTimeAvailable";
  private static final String REQUEST_DURATION = "duration";
  private static final String REQUEST_ROLE = "role";
  private static final String REQUEST_PRODUCT_AREA = "productArea";
  private static final String REQUEST_INTERESTS = "interests";
  private static final String REQUEST_SAVE_PREFERENCE = "savePreference";
  private static final String REQUEST_MATCH_PREFERENCE = "matchPreference";

  // Default inputs
  private static final String USERNAME_PERSON_A = "PersonA";
  private static final int DURATION_DEFAULT = 30;
  private static final long START_TIME_AVAILABLE_DEFAULT =
      currentDateTimeET.toInstant().toEpochMilli(); // not used
  private static final long END_TIME_AVAILABLE_DEFAULT =
      START_TIME_AVAILABLE_DEFAULT + TimeUnit.MINUTES.toMillis(DURATION_DEFAULT);
  private static final String ROLE_DEFAULT = "Software engineer";
  private static final String PRODUCT_AREA_DEFAULT = "Ads";
  private static final JSONArray INTERESTS_DEFAULT = new JSONArray().put("Books");
  private static final boolean SAVE_PREFERENCE_TRUE = true;
  private static final boolean SAVE_PREFERENCE_FALSE = false;
  private static final String MATCH_PREFERENCE_DEFAULT = "any";

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private AddParticipant addParticipant;
  private Clock clock;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() throws IOException {
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);

    helper.setUp();

    // Set "current" date to  1/1/2020 2:00pm ET
    clock = Clock.fixed(currentDateTimeET.toInstant(), currentDateTimeET.getZone());
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void firstTest() throws IOException {
    helper
        .setEnvIsAdmin(true)
        .setEnvIsLoggedIn(true)
        .setEnvAuthDomain("google.com")
        .setEnvEmail(USERNAME_PERSON_A + "@google.com");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    MatchDatastore matchDatastore = new MatchDatastore(datastore);
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    UserDatastore userDatastore = new UserDatastore(datastore);
    addParticipant =
        new AddParticipant(
            request, response, clock, matchDatastore, participantDatastore, userDatastore);
    JSONObject obj = new JSONObject();
    JSONObject formDetails = new JSONObject();
    formDetails.put(REQUEST_END_TIME_AVAILABLE, END_TIME_AVAILABLE_DEFAULT);
    formDetails.put(REQUEST_DURATION, DURATION_DEFAULT);
    formDetails.put(REQUEST_ROLE, ROLE_DEFAULT);
    formDetails.put(REQUEST_PRODUCT_AREA, PRODUCT_AREA_DEFAULT);
    formDetails.put(REQUEST_INTERESTS, INTERESTS_DEFAULT);
    formDetails.put(REQUEST_SAVE_PREFERENCE, SAVE_PREFERENCE_TRUE);
    formDetails.put(REQUEST_MATCH_PREFERENCE, MATCH_PREFERENCE_DEFAULT);
    obj.put(REQUEST_FORM_DETAILS, formDetails);
    when(request.getReader()).thenReturn(getReader(obj));
    when(response.getWriter()).thenReturn(getWriter());
    // verify...

    addParticipant.doPostHelper();

    // Check datastore
  }

  /** Mock HttpServletRequest method */
  private BufferedReader getReader(JSONObject jsonObject) {
    return new BufferedReader(new StringReader(jsonObject.toString()));
  }

  private PrintWriter getWriter() {
    return new PrintWriter(System.out);
  }
}
