package com.google.sps;

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
import com.google.sps.datastore.MatchDatastore;
import com.google.sps.datastore.ParticipantDatastore;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SearchMatchHelperTest {

  // Inputs
  private static final String USERNAME_PERSON_A = "persona";
  private static final String USERNAME_PERSON_B = "personb";
  private static final int DURATION_DEFAULT = 30;
  private static final long START_TIME_AVAILABLE_DEFAULT = System.currentTimeMillis();
  private static final long END_TIME_AVAILABLE_DURATION =
      START_TIME_AVAILABLE_DEFAULT + TimeUnit.MINUTES.toMillis(DURATION_DEFAULT);
  private static final long END_TIME_AVAILABLE_DEFAULT =
      START_TIME_AVAILABLE_DEFAULT + TimeUnit.MINUTES.toMillis(100);
  private static final String ROLE_DEFAULT = "Software engineer";
  private static final String PRODUCT_AREA_DEFAULT = "Ads";
  private static final List<String> INTERESTS_DEFAULT = Arrays.asList("Books");
  private static final MatchPreference MATCH_PREFERENCE_DEFAULT = MatchPreference.ANY;
  private static final long MATCH_ID_DEFAULT = 123456;
  private static final MatchStatus MATCH_STATUS_MATCHED = MatchStatus.MATCHED;
  private static final MatchStatus MATCH_STATUS_UNMATCHED = MatchStatus.UNMATCHED;
  private static final long TIMESTAMP_DEFAULT = 0;

  private HttpServletRequest request;
  private HttpServletResponse response;
  private MatchDatastore matchDatastore;
  private ParticipantDatastore participantDatastore;
  private UsernameService usernameService;
  private SearchMatchHelper searchMatchHelper;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() throws IOException {
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    matchDatastore = mock(MatchDatastore.class);
    participantDatastore = mock(ParticipantDatastore.class);
    usernameService = mock(UsernameService.class);

    when(response.getWriter()).thenReturn(getWriter());
    when(usernameService.getUsername()).thenReturn(USERNAME_PERSON_A);

    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void noParticipantInDatastore() throws IOException {
    searchMatchHelper =
        new SearchMatchHelper(matchDatastore, participantDatastore, usernameService);
    searchMatchHelper.doGet(request, response);

    verify(participantDatastore).getParticipantFromUsername(USERNAME_PERSON_A);
    verify(response)
        .sendError(
            HttpServletResponse.SC_BAD_REQUEST,
            "Participant with username " + USERNAME_PERSON_A + " does not exist in datastore.");
  }

  @Test
  public void invalidMatchId() throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    Participant participantA =
        new Participant(
            USERNAME_PERSON_A,
            START_TIME_AVAILABLE_DEFAULT,
            END_TIME_AVAILABLE_DEFAULT,
            DURATION_DEFAULT,
            ROLE_DEFAULT,
            PRODUCT_AREA_DEFAULT,
            INTERESTS_DEFAULT,
            MATCH_PREFERENCE_DEFAULT,
            MATCH_ID_DEFAULT,
            MATCH_STATUS_MATCHED,
            TIMESTAMP_DEFAULT);
    participantDatastore.addParticipant(participantA);

    searchMatchHelper =
        new SearchMatchHelper(matchDatastore, participantDatastore, usernameService);
    searchMatchHelper.doGet(request, response);

    verify(response)
        .sendError(
            HttpServletResponse.SC_BAD_REQUEST,
            "No match entity in datastore with match id " + MATCH_ID_DEFAULT + ".");
  }

  @Test
  public void expiredParticipant() throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    Participant participantA =
        new Participant(
            USERNAME_PERSON_A,
            START_TIME_AVAILABLE_DEFAULT,
            END_TIME_AVAILABLE_DURATION,
            DURATION_DEFAULT,
            ROLE_DEFAULT,
            PRODUCT_AREA_DEFAULT,
            INTERESTS_DEFAULT,
            MATCH_PREFERENCE_DEFAULT,
            MATCH_ID_DEFAULT,
            MATCH_STATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    participantDatastore.addParticipant(participantA);

    searchMatchHelper =
        new SearchMatchHelper(matchDatastore, participantDatastore, usernameService);
    searchMatchHelper.doGet(request, response);

    verify(response).setStatus(HttpServletResponse.SC_OK, "Participant is expired");
  }

  @Test
  public void noMatchYet() throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    Participant participantA =
        new Participant(
            USERNAME_PERSON_A,
            START_TIME_AVAILABLE_DEFAULT,
            END_TIME_AVAILABLE_DEFAULT,
            DURATION_DEFAULT,
            ROLE_DEFAULT,
            PRODUCT_AREA_DEFAULT,
            INTERESTS_DEFAULT,
            MATCH_PREFERENCE_DEFAULT,
            MATCH_ID_DEFAULT,
            MATCH_STATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    participantDatastore.addParticipant(participantA);

    searchMatchHelper =
        new SearchMatchHelper(matchDatastore, participantDatastore, usernameService);
    searchMatchHelper.doGet(request, response);

    verify(response).setStatus(HttpServletResponse.SC_OK, "Participant has no match yet");
  }

  @Test
  public void foundMatch() throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    MatchDatastore matchDatastore = new MatchDatastore(datastore);
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    Match match =
        new Match(USERNAME_PERSON_A, USERNAME_PERSON_B, DURATION_DEFAULT, TIMESTAMP_DEFAULT);
    long matchId = matchDatastore.addMatch(match);
    Participant participantA =
        new Participant(
            USERNAME_PERSON_A,
            START_TIME_AVAILABLE_DEFAULT,
            END_TIME_AVAILABLE_DEFAULT,
            DURATION_DEFAULT,
            ROLE_DEFAULT,
            PRODUCT_AREA_DEFAULT,
            INTERESTS_DEFAULT,
            MATCH_PREFERENCE_DEFAULT,
            matchId,
            MATCH_STATUS_MATCHED,
            TIMESTAMP_DEFAULT);
    participantDatastore.addParticipant(participantA);

    searchMatchHelper =
        new SearchMatchHelper(matchDatastore, participantDatastore, usernameService);
    searchMatchHelper.doGet(request, response);

    verify(response).setStatus(HttpServletResponse.SC_OK, "Participant has a match!");
  }

  /** Mock HttpServletResponse method */
  private PrintWriter getWriter() {
    return new PrintWriter(System.out);
  }
}
