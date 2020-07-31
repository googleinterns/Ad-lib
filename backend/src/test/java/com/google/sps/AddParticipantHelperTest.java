package com.google.sps;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.Match;
import com.google.sps.data.MatchPreference;
import com.google.sps.data.MatchStatus;
import com.google.sps.data.Participant;
import com.google.sps.data.User;
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

@RunWith(JUnit4.class)
public class AddParticipantHelperTest {
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

  // Input constants
  private static final String USERNAME_PERSON_A = "persona";
  private static final String USERNAME_PERSON_B = "personb";
  private static final int DURATION_DEFAULT = 30;
  private static final long START_TIME_AVAILABLE_DEFAULT =
      currentDateTimeET.toInstant().toEpochMilli(); // not used
  private static final long END_TIME_AVAILABLE_DEFAULT =
      START_TIME_AVAILABLE_DEFAULT + TimeUnit.MINUTES.toMillis(100);
  private static final String ROLE_DEFAULT = "Software engineer";
  private static final String PRODUCT_AREA_DEFAULT = "Ads";
  private static final JSONArray INTERESTS_DEFAULT = new JSONArray().put("Books");
  private static final boolean SAVE_PREFERENCE_TRUE = true;
  private static final boolean SAVE_PREFERENCE_FALSE = false;
  private static final String MATCH_PREFERENCE_ANY = "any";
  private static final String MATCH_PREFERENCE_DIFFERENT = "different";
  private static final long MATCH_ID_DEFAULT = 0;
  private static final MatchStatus MATCH_STATUS_DEFAULT = MatchStatus.UNMATCHED;
  private static final long TIMESTAMP_DEFAULT = 0;

  private HttpServletRequest request;
  private HttpServletResponse response;
  private MatchDatastore matchDatastore;
  private ParticipantDatastore participantDatastore;
  private UserDatastore userDatastore;
  private UsernameService usernameService;
  private AddParticipantHelper addParticipantHelper;
  private Clock clock;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() throws IOException {
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    matchDatastore = mock(MatchDatastore.class);
    participantDatastore = mock(ParticipantDatastore.class);
    userDatastore = mock(UserDatastore.class);
    usernameService = mock(UsernameService.class);

    when(response.getWriter()).thenReturn(new PrintWriter(System.out));

    helper.setUp();

    // Set "current" date to  1/1/2020 2:00pm ET
    clock = Clock.fixed(currentDateTimeET.toInstant(), currentDateTimeET.getZone());
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /** Return JSON object for default form details */
  private JSONObject getDefaultFormDetails() {
    JSONObject formDetails = new JSONObject();
    formDetails.put(REQUEST_END_TIME_AVAILABLE, END_TIME_AVAILABLE_DEFAULT);
    formDetails.put(REQUEST_DURATION, DURATION_DEFAULT);
    formDetails.put(REQUEST_ROLE, ROLE_DEFAULT);
    formDetails.put(REQUEST_PRODUCT_AREA, PRODUCT_AREA_DEFAULT);
    formDetails.put(REQUEST_INTERESTS, INTERESTS_DEFAULT);
    formDetails.put(REQUEST_SAVE_PREFERENCE, SAVE_PREFERENCE_TRUE);
    formDetails.put(REQUEST_MATCH_PREFERENCE, MATCH_PREFERENCE_ANY);
    return formDetails;
  }

  @Test
  public void invalidJsonObject() throws IOException {
    JSONObject obj = null;
    when(request.getReader()).thenThrow(IOException.class);
    when(usernameService.getUsername()).thenReturn(USERNAME_PERSON_A);

    addParticipantHelper =
        new AddParticipantHelper(
            clock, matchDatastore, participantDatastore, userDatastore, usernameService);
    addParticipantHelper.doPost(request, response);

    verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not read request body");
  }

  @Test
  public void invalidEmail() throws IOException {
    JSONObject obj = new JSONObject();
    JSONObject formDetails = getDefaultFormDetails();
    obj.put(REQUEST_FORM_DETAILS, formDetails);
    when(request.getReader()).thenReturn(getReader(obj));
    when(usernameService.getUsername()).thenReturn(null);

    addParticipantHelper =
        new AddParticipantHelper(
            clock, matchDatastore, participantDatastore, userDatastore, usernameService);
    addParticipantHelper.doPost(request, response);

    verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not retrieve email.");
  }

  @Test
  public void invalidDuration() throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    MatchDatastore matchDatastore = new MatchDatastore(datastore);
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    UserDatastore userDatastore = new UserDatastore(datastore);
    JSONObject obj = new JSONObject();
    JSONObject formDetails = getDefaultFormDetails();
    formDetails.put(REQUEST_DURATION, -10);
    obj.put(REQUEST_FORM_DETAILS, formDetails);
    when(request.getReader()).thenReturn(getReader(obj));
    when(usernameService.getUsername()).thenReturn(USERNAME_PERSON_A);

    addParticipantHelper =
        new AddParticipantHelper(
            clock, matchDatastore, participantDatastore, userDatastore, usernameService);
    addParticipantHelper.doPost(request, response);

    verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid duration.");
  }

  @Test(expected = IllegalStateException.class)
  public void invalidMatchPreference() throws IOException {
    JSONObject obj = new JSONObject();
    JSONObject formDetails = getDefaultFormDetails();
    formDetails.put(REQUEST_MATCH_PREFERENCE, "none"); // invalid match preference
    obj.put(REQUEST_FORM_DETAILS, formDetails);
    when(request.getReader()).thenReturn(getReader(obj));
    when(usernameService.getUsername()).thenReturn(USERNAME_PERSON_A);

    addParticipantHelper =
        new AddParticipantHelper(
            clock, matchDatastore, participantDatastore, userDatastore, usernameService);
    addParticipantHelper.doPost(request, response);
  }

  @Test
  public void savePreferences() throws IOException {
    JSONObject obj = new JSONObject();
    JSONObject formDetails = getDefaultFormDetails();
    formDetails.put(REQUEST_SAVE_PREFERENCE, SAVE_PREFERENCE_TRUE);
    obj.put(REQUEST_FORM_DETAILS, formDetails);
    when(request.getReader()).thenReturn(getReader(obj));
    when(usernameService.getUsername()).thenReturn(USERNAME_PERSON_A);

    addParticipantHelper =
        new AddParticipantHelper(
            clock, matchDatastore, participantDatastore, userDatastore, usernameService);
    addParticipantHelper.doPost(request, response);

    verify(participantDatastore).addParticipant(any());
    verify(userDatastore).addUser(any());
  }

  @Test
  public void dontSavePreferences() throws IOException {
    JSONObject obj = new JSONObject();
    JSONObject formDetails = getDefaultFormDetails();
    formDetails.put(REQUEST_SAVE_PREFERENCE, SAVE_PREFERENCE_FALSE);
    obj.put(REQUEST_FORM_DETAILS, formDetails);
    when(request.getReader()).thenReturn(getReader(obj));
    when(usernameService.getUsername()).thenReturn(USERNAME_PERSON_A);

    addParticipantHelper =
        new AddParticipantHelper(
            clock, matchDatastore, participantDatastore, userDatastore, usernameService);
    addParticipantHelper.doPost(request, response);

    verify(participantDatastore).addParticipant(any());
  }

  @Test
  public void twoParticipantsMatch() throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    MatchDatastore matchDatastore = new MatchDatastore(datastore);
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    UserDatastore userDatastore = new UserDatastore(datastore);
    Participant participantA =
        new Participant(
            USERNAME_PERSON_A,
            START_TIME_AVAILABLE_DEFAULT,
            END_TIME_AVAILABLE_DEFAULT,
            DURATION_DEFAULT,
            ROLE_DEFAULT,
            PRODUCT_AREA_DEFAULT,
            AddParticipantHelper.getListFromJsonArray(INTERESTS_DEFAULT),
            MatchPreference.forStringValue(MATCH_PREFERENCE_ANY),
            MATCH_ID_DEFAULT,
            MATCH_STATUS_DEFAULT,
            TIMESTAMP_DEFAULT);
    participantDatastore.addParticipant(participantA);
    JSONObject obj = new JSONObject();
    JSONObject formDetails = getDefaultFormDetails();
    obj.put(REQUEST_FORM_DETAILS, formDetails);
    when(request.getReader()).thenReturn(getReader(obj));
    when(usernameService.getUsername()).thenReturn(USERNAME_PERSON_B);

    addParticipantHelper =
        new AddParticipantHelper(
            clock, matchDatastore, participantDatastore, userDatastore, usernameService);
    addParticipantHelper.doPost(request, response);
    Participant participantB = participantDatastore.getParticipantFromUsername(USERNAME_PERSON_B);
    User userB = userDatastore.getUserFromUsername(USERNAME_PERSON_B);
    Match match = matchDatastore.getMatchFromId(participantB.getMatchId());

    assertThat(participantB).isNotNull();
    assertThat(userB).isNotNull();
    assertThat(match).isNotNull();
  }

  @Test
  public void twoParticipantsNoMatch() throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    MatchDatastore matchDatastore = new MatchDatastore(datastore);
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    UserDatastore userDatastore = new UserDatastore(datastore);
    Participant participantA =
        new Participant(
            USERNAME_PERSON_A,
            START_TIME_AVAILABLE_DEFAULT,
            END_TIME_AVAILABLE_DEFAULT,
            DURATION_DEFAULT,
            ROLE_DEFAULT,
            PRODUCT_AREA_DEFAULT,
            AddParticipantHelper.getListFromJsonArray(INTERESTS_DEFAULT),
            MatchPreference.forStringValue(MATCH_PREFERENCE_ANY),
            MATCH_ID_DEFAULT,
            MATCH_STATUS_DEFAULT,
            TIMESTAMP_DEFAULT);
    participantDatastore.addParticipant(participantA);
    JSONObject obj = new JSONObject();
    JSONObject formDetails = getDefaultFormDetails();
    formDetails.put(REQUEST_MATCH_PREFERENCE, MATCH_PREFERENCE_DIFFERENT);
    obj.put(REQUEST_FORM_DETAILS, formDetails);
    when(request.getReader()).thenReturn(getReader(obj));
    when(usernameService.getUsername()).thenReturn(USERNAME_PERSON_B);

    addParticipantHelper =
        new AddParticipantHelper(
            clock, matchDatastore, participantDatastore, userDatastore, usernameService);
    addParticipantHelper.doPost(request, response);
    Participant participantB = participantDatastore.getParticipantFromUsername(USERNAME_PERSON_B);
    User userB = userDatastore.getUserFromUsername(USERNAME_PERSON_B);

    assertThat(participantB).isNotNull();
    assertThat(userB).isNotNull();
    assertThat(participantB.getMatchId()).isEqualTo(MATCH_ID_DEFAULT);
  }

  /** Mock HttpServletRequest method */
  private BufferedReader getReader(JSONObject jsonObject) {
    return new BufferedReader(new StringReader(jsonObject.toString()));
  }
}
