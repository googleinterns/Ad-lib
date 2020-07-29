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
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.MatchPreference;
import com.google.sps.data.MatchStatus;
import com.google.sps.data.Participant;
import com.google.sps.datastore.ParticipantDatastore;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class ParticipantDatastoreTest {

  // Default values
  private static final long START_TIME_AVAILABLE_DEFAULT = 0;
  private static final long END_TIME_AVAILABLE_DEFAULT =
      ZonedDateTime.now().toInstant().toEpochMilli();
  private static final int DURATION_DEFAULT = 30;
  private static final String ROLE_DEFAULT = "Software engineer";
  private static final String PRODUCT_AREA_DEFAULT = "Ads";
  private static final List<String> INTERESTS_DEFAULT = Arrays.asList("Books", "Travel");
  private static final MatchPreference MATCH_PREFERENCE_DEFAULT = MatchPreference.SIMILAR;
  private static final long MATCH_ID_DEFAULT = 0;
  private static final MatchStatus MATCH_STATUS_DEFAULT = MatchStatus.UNMATCHED;
  private static final long TIMESTAMP_DEFAULT = 0;

  // Some usernames
  private static final String PERSON_A = "Person A";
  private static final String PERSON_B = "Person B";

  // Datastore Key/Property constants
  private static final String KIND_PARTICIPANT = "Participant";
  private static final String PROPERTY_USERNAME = "username";
  private static final String PROPERTY_START_TIME_AVAILABLE = "startTimeAvailable";
  private static final String PROPERTY_END_TIME_AVAILABLE = "endTimeAvailable";
  private static final String PROPERTY_DURATION = "duration";
  private static final String PROPERTY_ROLE = "role";
  private static final String PROPERTY_PRODUCT_AREA = "productArea";
  private static final String PROPERTY_INTERESTS = "interests";
  private static final String PROPERTY_MATCH_PREFERENCE = "matchPreference";
  private static final String PROPERTY_MATCH_ID = "matchId";
  private static final String PROPERTY_MATCH_STATUS = "matchStatus";
  private static final String PROPERTY_TIMESTAMP = "timestamp";

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  // TODO(#37): Find/write JUnit rule to encapsulate setUp() and tearDown()
  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void addOneParticipant() throws EntityNotFoundException {
    // Add one participant to datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    Participant participant =
        new Participant(
            PERSON_A,
            START_TIME_AVAILABLE_DEFAULT,
            END_TIME_AVAILABLE_DEFAULT,
            DURATION_DEFAULT,
            ROLE_DEFAULT,
            PRODUCT_AREA_DEFAULT,
            INTERESTS_DEFAULT,
            MATCH_PREFERENCE_DEFAULT,
            MATCH_ID_DEFAULT,
            MATCH_STATUS_DEFAULT,
            TIMESTAMP_DEFAULT);

    participantDatastore.addParticipant(participant);

    Key key = KeyFactory.createKey(KIND_PARTICIPANT, PERSON_A);
    Entity entity = datastore.get(key);
    assertThat((String) entity.getProperty(PROPERTY_USERNAME)).isEqualTo(PERSON_A);
    assertThat((long) entity.getProperty(PROPERTY_START_TIME_AVAILABLE))
        .isEqualTo(START_TIME_AVAILABLE_DEFAULT);
    assertThat((long) entity.getProperty(PROPERTY_END_TIME_AVAILABLE))
        .isEqualTo(END_TIME_AVAILABLE_DEFAULT);
    assertThat(((Long) entity.getProperty(PROPERTY_DURATION)).intValue())
        .isEqualTo(DURATION_DEFAULT);
    assertThat((String) entity.getProperty(PROPERTY_ROLE)).isEqualTo(ROLE_DEFAULT);
    assertThat((String) entity.getProperty(PROPERTY_PRODUCT_AREA)).isEqualTo(PRODUCT_AREA_DEFAULT);
    assertThat(
            ParticipantDatastore.convertStringToList(
                (String) entity.getProperty(PROPERTY_INTERESTS)))
        .isEqualTo(INTERESTS_DEFAULT);
    assertThat(
            MatchPreference.forIntValue(
                ((Long) entity.getProperty(PROPERTY_MATCH_PREFERENCE)).intValue()))
        .isEqualTo(MATCH_PREFERENCE_DEFAULT);
    assertThat((long) entity.getProperty(PROPERTY_MATCH_ID)).isEqualTo(MATCH_ID_DEFAULT);
    assertThat(
            MatchStatus.forIntValue(((Long) entity.getProperty(PROPERTY_MATCH_STATUS)).intValue()))
        .isEqualTo(MATCH_STATUS_DEFAULT);
    assertThat((long) entity.getProperty(PROPERTY_TIMESTAMP)).isEqualTo(TIMESTAMP_DEFAULT);
  }

  @Test
  public void addTwoParticipants() throws EntityNotFoundException {
    // Add two participants to datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    Participant participantA =
        new Participant(
            PERSON_A,
            START_TIME_AVAILABLE_DEFAULT,
            END_TIME_AVAILABLE_DEFAULT,
            DURATION_DEFAULT,
            ROLE_DEFAULT,
            PRODUCT_AREA_DEFAULT,
            INTERESTS_DEFAULT,
            MATCH_PREFERENCE_DEFAULT,
            MATCH_ID_DEFAULT,
            MATCH_STATUS_DEFAULT,
            TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            PERSON_B,
            START_TIME_AVAILABLE_DEFAULT,
            END_TIME_AVAILABLE_DEFAULT,
            DURATION_DEFAULT,
            ROLE_DEFAULT,
            PRODUCT_AREA_DEFAULT,
            INTERESTS_DEFAULT,
            MATCH_PREFERENCE_DEFAULT,
            MATCH_ID_DEFAULT,
            MATCH_STATUS_DEFAULT,
            TIMESTAMP_DEFAULT);

    participantDatastore.addParticipant(participantA);
    participantDatastore.addParticipant(participantB);

    Key keyA = KeyFactory.createKey(KIND_PARTICIPANT, PERSON_A);
    Key keyB = KeyFactory.createKey(KIND_PARTICIPANT, PERSON_B);
    Entity entityA = datastore.get(keyA);
    Entity entityB = datastore.get(keyB);
    assertThat((String) entityA.getProperty(PROPERTY_USERNAME)).isEqualTo(PERSON_A);
    assertThat((long) entityA.getProperty(PROPERTY_START_TIME_AVAILABLE))
        .isEqualTo(START_TIME_AVAILABLE_DEFAULT);
    assertThat((long) entityA.getProperty(PROPERTY_END_TIME_AVAILABLE))
        .isEqualTo(END_TIME_AVAILABLE_DEFAULT);
    assertThat(((Long) entityA.getProperty(PROPERTY_DURATION)).intValue())
        .isEqualTo(DURATION_DEFAULT);
    assertThat((String) entityA.getProperty(PROPERTY_ROLE)).isEqualTo(ROLE_DEFAULT);
    assertThat((String) entityA.getProperty(PROPERTY_PRODUCT_AREA)).isEqualTo(PRODUCT_AREA_DEFAULT);
    assertThat(
            ParticipantDatastore.convertStringToList(
                (String) entityA.getProperty(PROPERTY_INTERESTS)))
        .isEqualTo(INTERESTS_DEFAULT);
    assertThat(
            MatchPreference.forIntValue(
                ((Long) entityA.getProperty(PROPERTY_MATCH_PREFERENCE)).intValue()))
        .isEqualTo(MATCH_PREFERENCE_DEFAULT);
    assertThat((long) entityA.getProperty(PROPERTY_MATCH_ID)).isEqualTo(MATCH_ID_DEFAULT);
    assertThat(
            MatchStatus.forIntValue(((Long) entityA.getProperty(PROPERTY_MATCH_STATUS)).intValue()))
        .isEqualTo(MATCH_STATUS_DEFAULT);
    assertThat((long) entityA.getProperty(PROPERTY_TIMESTAMP)).isEqualTo(TIMESTAMP_DEFAULT);
    assertThat((String) entityB.getProperty(PROPERTY_USERNAME)).isEqualTo(PERSON_B);
    assertThat((long) entityB.getProperty(PROPERTY_START_TIME_AVAILABLE))
        .isEqualTo(START_TIME_AVAILABLE_DEFAULT);
    assertThat((long) entityB.getProperty(PROPERTY_END_TIME_AVAILABLE))
        .isEqualTo(END_TIME_AVAILABLE_DEFAULT);
    assertThat(((Long) entityB.getProperty(PROPERTY_DURATION)).intValue())
        .isEqualTo(DURATION_DEFAULT);
    assertThat((String) entityB.getProperty(PROPERTY_ROLE)).isEqualTo(ROLE_DEFAULT);
    assertThat((String) entityB.getProperty(PROPERTY_PRODUCT_AREA)).isEqualTo(PRODUCT_AREA_DEFAULT);
    assertThat(
            ParticipantDatastore.convertStringToList(
                (String) entityB.getProperty(PROPERTY_INTERESTS)))
        .isEqualTo(INTERESTS_DEFAULT);
    assertThat(
            MatchPreference.forIntValue(
                ((Long) entityB.getProperty(PROPERTY_MATCH_PREFERENCE)).intValue()))
        .isEqualTo(MATCH_PREFERENCE_DEFAULT);
    assertThat((long) entityB.getProperty(PROPERTY_MATCH_ID)).isEqualTo(MATCH_ID_DEFAULT);
    assertThat(
            MatchStatus.forIntValue(((Long) entityB.getProperty(PROPERTY_MATCH_STATUS)).intValue()))
        .isEqualTo(MATCH_STATUS_DEFAULT);
    assertThat((long) entityB.getProperty(PROPERTY_TIMESTAMP)).isEqualTo(TIMESTAMP_DEFAULT);
  }

  @Test
  public void addGetOneParticipant() throws EntityNotFoundException {
    // Add one participant to datastore and get participant
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    Participant participant =
        new Participant(
            PERSON_A,
            START_TIME_AVAILABLE_DEFAULT,
            END_TIME_AVAILABLE_DEFAULT,
            DURATION_DEFAULT,
            ROLE_DEFAULT,
            PRODUCT_AREA_DEFAULT,
            INTERESTS_DEFAULT,
            MATCH_PREFERENCE_DEFAULT,
            MATCH_ID_DEFAULT,
            MATCH_STATUS_DEFAULT,
            TIMESTAMP_DEFAULT);

    participantDatastore.addParticipant(participant);

    Participant participantFromUsername = participantDatastore.getParticipantFromUsername(PERSON_A);
    assertThat(participantFromUsername.getUsername()).isEqualTo(PERSON_A);
    assertThat(participantFromUsername.getStartTimeAvailable())
        .isEqualTo(START_TIME_AVAILABLE_DEFAULT);
    assertThat(participantFromUsername.getEndTimeAvailable()).isEqualTo(END_TIME_AVAILABLE_DEFAULT);
    assertThat(participantFromUsername.getDuration()).isEqualTo(DURATION_DEFAULT);
    assertThat(participantFromUsername.getRole()).isEqualTo(ROLE_DEFAULT);
    assertThat(participantFromUsername.getProductArea()).isEqualTo(PRODUCT_AREA_DEFAULT);
    assertThat(participantFromUsername.getInterests()).isEqualTo(INTERESTS_DEFAULT);
    assertThat(participantFromUsername.getMatchPreference()).isEqualTo(MATCH_PREFERENCE_DEFAULT);
    assertThat(participantFromUsername.getMatchId()).isEqualTo(MATCH_ID_DEFAULT);
    assertThat(participantFromUsername.getMatchStatus()).isEqualTo(MATCH_STATUS_DEFAULT);
    assertThat(participantFromUsername.getTimestamp()).isEqualTo(TIMESTAMP_DEFAULT);
  }

  @Test
  public void getNonexistentParticipant() {
    // Try to get participant from username that's not in datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    Participant participant =
        new Participant(
            PERSON_A,
            START_TIME_AVAILABLE_DEFAULT,
            END_TIME_AVAILABLE_DEFAULT,
            DURATION_DEFAULT,
            ROLE_DEFAULT,
            PRODUCT_AREA_DEFAULT,
            INTERESTS_DEFAULT,
            MATCH_PREFERENCE_DEFAULT,
            MATCH_ID_DEFAULT,
            MATCH_STATUS_DEFAULT,
            TIMESTAMP_DEFAULT);

    Participant participantFromUsername = participantDatastore.getParticipantFromUsername(PERSON_A);
    assertThat(participantFromUsername).isNull();
  }

  @Test
  public void removeParticipant() {
    // Add and then remove participant from datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
    Participant participant =
        new Participant(
            PERSON_A,
            START_TIME_AVAILABLE_DEFAULT,
            END_TIME_AVAILABLE_DEFAULT,
            DURATION_DEFAULT,
            ROLE_DEFAULT,
            PRODUCT_AREA_DEFAULT,
            INTERESTS_DEFAULT,
            MATCH_PREFERENCE_DEFAULT,
            MATCH_ID_DEFAULT,
            MATCH_STATUS_DEFAULT,
            TIMESTAMP_DEFAULT);

    participantDatastore.addParticipant(participant);
    participantDatastore.removeParticipant(participant.getUsername());

    Participant participantFromUsername = participantDatastore.getParticipantFromUsername(PERSON_A);
    assertThat(participantFromUsername).isNull();
  }
}
