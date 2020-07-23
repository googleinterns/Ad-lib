// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import static com.google.common.truth.Truth.assertThat;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.Match;
import com.google.sps.data.MatchPreference;
import com.google.sps.data.MatchStatus;
import com.google.sps.data.Participant;
import com.google.sps.datastore.ParticipantDatastore;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class FindMatchQueryTest {

  // Some people that we can use in our tests
  private static final String PERSON_A = "Person A";
  private static final String PERSON_B = "Person B";
  private static final String PERSON_C = "Person C";

  // Default parameters unused in query
  private static final long MATCHID_DEFAULT = 0;
  private static final long TIMESTAMP_DEFAULT = 0;

  // Role constants
  private static final String ROLE_BLANK = "";
  private static final String ROLE_SOFTWARE_ENGINEER = "Software engineer";
  private static final String ROLE_PRODUCT_MANAGER = "Product manager";

  // Product area constants
  private static final String PRODUCT_AREA_BLANK = "";
  private static final String PRODUCT_AREA_ADS = "Ads";
  private static final String PRODUCT_AREA_CLOUD = "Cloud";

  // Interests constants
  private static final String INTERESTS_BLANK = "";
  private static final String INTERESTS_BOOKS = "Books";
  private static final String INTERESTS_BOOKS_SPORTS_TRAVEL = "Books,Sports,Travel";
  private static final String INTERESTS_BOOKS_TRAVEL = "Books,Travel";
  private static final String INTERESTS_GAMING_SPORTS = "Gaming,Sports";

  // Match preference constants
  private static final MatchPreference MATCH_PREFERENCE_DIFFERENT = MatchPreference.DIFFERENT;
  private static final MatchPreference MATCH_PREFERENCE_ANY = MatchPreference.ANY;
  private static final MatchPreference MATCH_PREFERENCE_SIMILAR = MatchPreference.SIMILAR;

  // Duration constants
  private static final int DURATION_15_MINUTES = 15;
  private static final int DURATION_30_MINUTES = 30;
  private static final int DURATION_45_MINUTES = 45;
  private static final int DURATION_60_MINUTES = 60;

  // Initial value before matched
  private static final MatchStatus MATCHSTATUS_UNMATCHED = MatchStatus.UNMATCHED;

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

  // Some times available until on 1/1/2020
  private static final long TIME_1400ET = currentDateTimeET.toInstant().toEpochMilli();
  private static final long TIME_1450ET = getNewTimeToday(currentDateTimeET, 14, 50);
  private static final long TIME_1455ET = getNewTimeToday(currentDateTimeET, 14, 55);
  private static final long TIME_1456ET = getNewTimeToday(currentDateTimeET, 14, 56);
  private static final long TIME_1500ET = getNewTimeToday(currentDateTimeET, 15, 0);
  private static final long TIME_1530ET = getNewTimeToday(currentDateTimeET, 15, 30);
  private static final long TIME_1600ET = getNewTimeToday(currentDateTimeET, 16, 0);
  private static final long TIME_1800ET = getNewTimeToday(currentDateTimeET, 18, 0);
  private static final long TIME_2000ET = getNewTimeToday(currentDateTimeET, 20, 0);

  private Clock clock;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();

    // Set "current" date to  1/1/2020 2:00pm ET
    clock = Clock.fixed(currentDateTimeET.toInstant(), currentDateTimeET.getZone());
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /** Return today's date with time of hour:minute */
  private static long getNewTimeToday(ZonedDateTime dateTime, int hour, int minute) {
    // Calculate current date but with hour:minute time
    // TODO: All times are currently today, wrap around times?
    return dateTime.withHour(hour).withMinute(minute).withNano(0).toInstant().toEpochMilli();
  }

  @Test
  public void compatibleTimeAndDuration() {
    // Two participants who are compatible in available time AND duration
    Participant participantA =
        new Participant(
            PERSON_A,
            TIME_1400ET,
            TIME_1600ET,
            DURATION_15_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            PERSON_B,
            TIME_1400ET,
            TIME_1800ET,
            DURATION_15_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    participantDatastore.addParticipant(participantA);

    FindMatchQuery query = new FindMatchQuery(clock, participantDatastore);
    Match match = query.findMatch(participantB);

    assertThat(match.getFirstParticipantUsername()).isEqualTo(PERSON_B);
    assertThat(match.getSecondParticipantUsername()).isEqualTo(PERSON_A);
    assertThat(match.getDuration()).isEqualTo(DURATION_15_MINUTES);
  }

  @Test
  public void compatibleTime() {
    // Two participants who are compatible in available time but NOT duration
    Participant participantA =
        new Participant(
            PERSON_A,
            TIME_1400ET,
            TIME_1600ET,
            DURATION_30_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            PERSON_B,
            TIME_1400ET,
            TIME_1800ET,
            DURATION_60_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    participantDatastore.addParticipant(participantA);

    FindMatchQuery query = new FindMatchQuery(clock, participantDatastore);
    Match match = query.findMatch(participantB);

    assertThat(match).isNull();
  }

  @Test
  public void compatibleDuration() {
    // Two participants who are compatible in duration but NOT available time
    Participant participantA =
        new Participant(
            PERSON_A,
            TIME_1400ET,
            TIME_1450ET,
            DURATION_45_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            PERSON_B,
            TIME_1400ET,
            TIME_1600ET,
            DURATION_45_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    participantDatastore.addParticipant(participantA);

    FindMatchQuery query = new FindMatchQuery(clock, participantDatastore);
    Match match = query.findMatch(participantB);

    assertThat(match).isNull();
  }

  @Test
  public void threeParticipantsAC() {
    // Three participants, A & B aren't compatible, but A & C are
    Participant participantA =
        new Participant(
            PERSON_A,
            TIME_1400ET,
            TIME_1600ET,
            DURATION_60_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            PERSON_B,
            TIME_1400ET,
            TIME_1450ET,
            DURATION_45_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    Participant participantC =
        new Participant(
            PERSON_C,
            TIME_1400ET,
            TIME_1800ET,
            DURATION_60_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    participantDatastore.addParticipant(participantA);
    participantDatastore.addParticipant(participantB);

    FindMatchQuery query = new FindMatchQuery(clock, participantDatastore);
    Match match = query.findMatch(participantC);

    assertThat(match.getFirstParticipantUsername()).isEqualTo(PERSON_C);
    assertThat(match.getSecondParticipantUsername()).isEqualTo(PERSON_A);
    assertThat(match.getDuration()).isEqualTo(DURATION_60_MINUTES);
  }

  @Test
  public void threeParticipantsBC() {
    // Three participants, A & B aren't compatible, but B & C are
    Participant participantA =
        new Participant(
            PERSON_A,
            TIME_1400ET,
            TIME_1450ET,
            DURATION_30_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            PERSON_B,
            TIME_1400ET,
            TIME_1600ET,
            DURATION_60_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    Participant participantC =
        new Participant(
            PERSON_C,
            TIME_1400ET,
            TIME_1800ET,
            DURATION_60_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    participantDatastore.addParticipant(participantA);
    participantDatastore.addParticipant(participantB);

    FindMatchQuery query = new FindMatchQuery(clock, participantDatastore);
    Match match = query.findMatch(participantC);

    assertThat(match.getFirstParticipantUsername()).isEqualTo(PERSON_C);
    assertThat(match.getSecondParticipantUsername()).isEqualTo(PERSON_B);
    assertThat(match.getDuration()).isEqualTo(DURATION_60_MINUTES);
  }

  @Test
  public void threeParticipantsTwoMatches() {
    // Three participants, A & B, A & C are compatible but only return A & C
    Participant participantA =
        new Participant(
            PERSON_A,
            TIME_1400ET,
            TIME_1600ET,
            DURATION_30_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            PERSON_B,
            TIME_1400ET,
            TIME_1800ET,
            DURATION_30_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    Participant participantC =
        new Participant(
            PERSON_C,
            TIME_1400ET,
            TIME_2000ET,
            DURATION_30_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    participantDatastore.addParticipant(participantA);
    participantDatastore.addParticipant(participantB);

    FindMatchQuery query = new FindMatchQuery(clock, participantDatastore);
    Match match = query.findMatch(participantC);

    assertThat(match.getFirstParticipantUsername()).isEqualTo(PERSON_C);
    assertThat(match.getSecondParticipantUsername()).isEqualTo(PERSON_A);
    assertThat(match.getDuration()).isEqualTo(DURATION_30_MINUTES);
  }

  @Test
  public void barelyNotCompatibleAvailibility() {
    // Two participants barely NOT compatible availability (edge case, need >10 minutes padding)
    Participant participantA =
        new Participant(
            PERSON_A,
            TIME_1400ET,
            TIME_1455ET,
            DURATION_45_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            PERSON_B,
            TIME_1400ET,
            TIME_1800ET,
            DURATION_45_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    participantDatastore.addParticipant(participantA);

    FindMatchQuery query = new FindMatchQuery(clock, participantDatastore);
    Match match = query.findMatch(participantB);

    assertThat(match).isNull();
  }

  @Test
  public void barelyCompatibleAvailability() {
    // Two participants barely compatible availability (edge case, need >10 minutes padding for
    // compatibility)
    Participant participantA =
        new Participant(
            PERSON_A,
            TIME_1400ET,
            TIME_1456ET,
            DURATION_45_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            PERSON_B,
            TIME_1400ET,
            TIME_1800ET,
            DURATION_45_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    participantDatastore.addParticipant(participantA);

    FindMatchQuery query = new FindMatchQuery(clock, participantDatastore);
    Match match = query.findMatch(participantB);

    assertThat(match.getFirstParticipantUsername()).isEqualTo(PERSON_B);
    assertThat(match.getSecondParticipantUsername()).isEqualTo(PERSON_A);
    assertThat(match.getDuration()).isEqualTo(DURATION_45_MINUTES);
  }

  @Test
  public void areCompletelyDifferentPreferSimilar() {
    // Two participants that are completely different (0/4) but prefer to be matched with someone
    // similar
    Participant participantA =
        new Participant(
            PERSON_A,
            TIME_1400ET,
            TIME_1456ET,
            DURATION_45_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            PERSON_B,
            TIME_1400ET,
            TIME_1800ET,
            DURATION_45_MINUTES,
            ROLE_PRODUCT_MANAGER,
            PRODUCT_AREA_CLOUD,
            INTERESTS_GAMING_SPORTS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    participantDatastore.addParticipant(participantA);

    FindMatchQuery query = new FindMatchQuery(clock, participantDatastore);
    Match match = query.findMatch(participantB);

    assertThat(match).isNull();
  }

  @Test
  public void areSimilarPreferDifferent() {
    // Two participants that are similar (3/3) but prefer to be matched with someone different
    Participant participantA =
        new Participant(
            PERSON_A,
            TIME_1400ET,
            TIME_1456ET,
            DURATION_45_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_DIFFERENT,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            PERSON_B,
            TIME_1400ET,
            TIME_1800ET,
            DURATION_45_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_DIFFERENT,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    participantDatastore.addParticipant(participantA);

    FindMatchQuery query = new FindMatchQuery(clock, participantDatastore);
    Match match = query.findMatch(participantB);

    assertThat(match).isNull();
  }

  @Test
  public void areDifferentPreferDifferent() {
    // Two participants that are different (1/3 match) and prefer to be matched with someone
    // different
    Participant participantA =
        new Participant(
            PERSON_A,
            TIME_1400ET,
            TIME_1456ET,
            DURATION_45_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_DIFFERENT,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            PERSON_B,
            TIME_1400ET,
            TIME_1800ET,
            DURATION_45_MINUTES,
            ROLE_PRODUCT_MANAGER,
            PRODUCT_AREA_CLOUD,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_DIFFERENT,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    participantDatastore.addParticipant(participantA);

    FindMatchQuery query = new FindMatchQuery(clock, participantDatastore);
    Match match = query.findMatch(participantB);

    assertThat(match.getFirstParticipantUsername()).isEqualTo(PERSON_B);
    assertThat(match.getSecondParticipantUsername()).isEqualTo(PERSON_A);
    assertThat(match.getDuration()).isEqualTo(DURATION_45_MINUTES);
  }

  @Test
  public void oneFilledFiveOtherFilledThreeAllMatching() {
    // Two participants, A with 5 filled fields, B with 3 filled fields. All 3 fields match
    Participant participantA =
        new Participant(
            PERSON_A,
            TIME_1400ET,
            TIME_1456ET,
            DURATION_45_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS_SPORTS_TRAVEL,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            PERSON_B,
            TIME_1400ET,
            TIME_1800ET,
            DURATION_45_MINUTES,
            ROLE_BLANK,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS_TRAVEL,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    participantDatastore.addParticipant(participantA);

    FindMatchQuery query = new FindMatchQuery(clock, participantDatastore);
    Match match = query.findMatch(participantB);

    assertThat(match.getFirstParticipantUsername()).isEqualTo(PERSON_B);
    assertThat(match.getSecondParticipantUsername()).isEqualTo(PERSON_A);
    assertThat(match.getDuration()).isEqualTo(DURATION_45_MINUTES);
  }

  @Test
  public void areSimilarPreferSimilarAny() {
    // Two participants that are similar, one prefers similar, other has no preference
    Participant participantA =
        new Participant(
            PERSON_A,
            TIME_1400ET,
            TIME_1456ET,
            DURATION_45_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_SIMILAR,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            PERSON_B,
            TIME_1400ET,
            TIME_1800ET,
            DURATION_45_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_ANY,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    participantDatastore.addParticipant(participantA);

    FindMatchQuery query = new FindMatchQuery(clock, participantDatastore);
    Match match = query.findMatch(participantB);

    assertThat(match.getFirstParticipantUsername()).isEqualTo(PERSON_B);
    assertThat(match.getSecondParticipantUsername()).isEqualTo(PERSON_A);
    assertThat(match.getDuration()).isEqualTo(DURATION_45_MINUTES);
  }

  @Test
  public void areDifferentPreferDifferentAny() {
    // Two participants that are different, one prefers different, other has no preference
    Participant participantA =
        new Participant(
            PERSON_A,
            TIME_1400ET,
            TIME_1456ET,
            DURATION_45_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_DIFFERENT,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            PERSON_B,
            TIME_1400ET,
            TIME_1800ET,
            DURATION_45_MINUTES,
            ROLE_PRODUCT_MANAGER,
            PRODUCT_AREA_CLOUD,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_ANY,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    participantDatastore.addParticipant(participantA);

    FindMatchQuery query = new FindMatchQuery(clock, participantDatastore);
    Match match = query.findMatch(participantB);

    assertThat(match.getFirstParticipantUsername()).isEqualTo(PERSON_B);
    assertThat(match.getSecondParticipantUsername()).isEqualTo(PERSON_A);
    assertThat(match.getDuration()).isEqualTo(DURATION_45_MINUTES);
  }

  @Test
  public void areDifferentNoPreference() {
    // Two participants that are different but both have no preference
    Participant participantA =
        new Participant(
            PERSON_A,
            TIME_1400ET,
            TIME_1456ET,
            DURATION_45_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_GAMING_SPORTS,
            MATCH_PREFERENCE_ANY,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            PERSON_B,
            TIME_1400ET,
            TIME_1800ET,
            DURATION_45_MINUTES,
            ROLE_PRODUCT_MANAGER,
            PRODUCT_AREA_CLOUD,
            INTERESTS_BOOKS_SPORTS_TRAVEL,
            MATCH_PREFERENCE_ANY,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    participantDatastore.addParticipant(participantA);

    FindMatchQuery query = new FindMatchQuery(clock, participantDatastore);
    Match match = query.findMatch(participantB);

    assertThat(match.getFirstParticipantUsername()).isEqualTo(PERSON_B);
    assertThat(match.getSecondParticipantUsername()).isEqualTo(PERSON_A);
    assertThat(match.getDuration()).isEqualTo(DURATION_45_MINUTES);
  }

  @Test
  public void areSimilarNoPreference() {
    // Two participants that are similar but both have no preference
    Participant participantA =
        new Participant(
            PERSON_A,
            TIME_1400ET,
            TIME_1456ET,
            DURATION_45_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_ANY,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            PERSON_B,
            TIME_1400ET,
            TIME_1800ET,
            DURATION_45_MINUTES,
            ROLE_SOFTWARE_ENGINEER,
            PRODUCT_AREA_ADS,
            INTERESTS_BOOKS,
            MATCH_PREFERENCE_ANY,
            MATCHID_DEFAULT,
            MATCHSTATUS_UNMATCHED,
            TIMESTAMP_DEFAULT);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    participantDatastore.addParticipant(participantA);

    FindMatchQuery query = new FindMatchQuery(clock, participantDatastore);
    Match match = query.findMatch(participantB);

    assertThat(match.getFirstParticipantUsername()).isEqualTo(PERSON_B);
    assertThat(match.getSecondParticipantUsername()).isEqualTo(PERSON_A);
    assertThat(match.getDuration()).isEqualTo(DURATION_45_MINUTES);
  }
}
