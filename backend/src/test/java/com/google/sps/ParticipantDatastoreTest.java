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
import com.google.sps.data.Participant;
import com.google.sps.datastore.ParticipantDatastore;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class ParticipantDatastoreTest {

  // Default values
  private static final long ID_DEFAULT = 123456;
  private static final ZonedDateTime STARTTIMEAVAILABLE_DEFAULT = ZonedDateTime.now();
  private static final ZonedDateTime ENDTIMEAVAILABLE_DEFAULT = ZonedDateTime.now();
  private static final int DURATION_DEFAULT = 30;
  private static final long CURRENTMATCHID_DEFAULT = 0;
  private static final long TIMESTAMP_DEFAULT = 0;

  private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;

  // Some usernames
  private static final String PERSON_A = "Person A";
  private static final String PERSON_B = "Person B";
  private static final String PERSON_C = "Person C";
  private static final String PERSON_D = "Person D";

  // Datastore Key/Property constants
  private static final String KIND_PARTICIPANT = "Participant";
  private static final String PROPERTY_USERNAME = "username";
  private static final String PROPERTY_STARTTIMEAVAILABLE = "startTimeAvailable";
  private static final String PROPERTY_ENDTIMEAVAILABLE = "endTimeAvailable";
  private static final String PROPERTY_DURATION = "duration";
  private static final String PROPERTY_CURRENTMATCHID = "currentMatchId";
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
            STARTTIMEAVAILABLE_DEFAULT,
            ENDTIMEAVAILABLE_DEFAULT,
            DURATION_DEFAULT,
            CURRENTMATCHID_DEFAULT,
            TIMESTAMP_DEFAULT);

    participantDatastore.addParticipant(participant);

    Key key = KeyFactory.createKey(KIND_PARTICIPANT, PERSON_A);
    Entity entity = datastore.get(key);
    assertThat((String) entity.getProperty(PROPERTY_USERNAME)).isEqualTo(PERSON_A);
    assertThat(
            ZonedDateTime.parse(
                (String) entity.getProperty(PROPERTY_STARTTIMEAVAILABLE), formatter))
        .isEqualTo(STARTTIMEAVAILABLE_DEFAULT);
    assertThat(
            ZonedDateTime.parse((String) entity.getProperty(PROPERTY_ENDTIMEAVAILABLE), formatter))
        .isEqualTo(ENDTIMEAVAILABLE_DEFAULT);
    assertThat(((Long) entity.getProperty(PROPERTY_DURATION)).intValue())
        .isEqualTo(DURATION_DEFAULT);
    assertThat((long) entity.getProperty(PROPERTY_CURRENTMATCHID))
        .isEqualTo(CURRENTMATCHID_DEFAULT);
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
            STARTTIMEAVAILABLE_DEFAULT,
            ENDTIMEAVAILABLE_DEFAULT,
            DURATION_DEFAULT,
            CURRENTMATCHID_DEFAULT,
            TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            PERSON_B,
            STARTTIMEAVAILABLE_DEFAULT,
            ENDTIMEAVAILABLE_DEFAULT,
            DURATION_DEFAULT,
            CURRENTMATCHID_DEFAULT,
            TIMESTAMP_DEFAULT);

    participantDatastore.addParticipant(participantA);
    participantDatastore.addParticipant(participantB);

    Key keyA = KeyFactory.createKey(KIND_PARTICIPANT, PERSON_A);
    Key keyB = KeyFactory.createKey(KIND_PARTICIPANT, PERSON_B);
    Entity entityA = datastore.get(keyA);
    Entity entityB = datastore.get(keyB);
    assertThat((String) entityA.getProperty(PROPERTY_USERNAME)).isEqualTo(PERSON_A);
    assertThat(
            ZonedDateTime.parse(
                (String) entityA.getProperty(PROPERTY_STARTTIMEAVAILABLE), formatter))
        .isEqualTo(STARTTIMEAVAILABLE_DEFAULT);
    assertThat(
            ZonedDateTime.parse((String) entityA.getProperty(PROPERTY_ENDTIMEAVAILABLE), formatter))
        .isEqualTo(ENDTIMEAVAILABLE_DEFAULT);
    assertThat(((Long) entityA.getProperty(PROPERTY_DURATION)).intValue())
        .isEqualTo(DURATION_DEFAULT);
    assertThat((long) entityA.getProperty(PROPERTY_CURRENTMATCHID))
        .isEqualTo(CURRENTMATCHID_DEFAULT);
    assertThat((long) entityA.getProperty(PROPERTY_TIMESTAMP)).isEqualTo(TIMESTAMP_DEFAULT);
    assertThat((String) entityB.getProperty(PROPERTY_USERNAME)).isEqualTo(PERSON_B);
    assertThat(
            ZonedDateTime.parse(
                (String) entityB.getProperty(PROPERTY_STARTTIMEAVAILABLE), formatter))
        .isEqualTo(STARTTIMEAVAILABLE_DEFAULT);
    assertThat(
            ZonedDateTime.parse((String) entityB.getProperty(PROPERTY_ENDTIMEAVAILABLE), formatter))
        .isEqualTo(ENDTIMEAVAILABLE_DEFAULT);
    assertThat(((Long) entityB.getProperty(PROPERTY_DURATION)).intValue())
        .isEqualTo(DURATION_DEFAULT);
    assertThat((long) entityB.getProperty(PROPERTY_CURRENTMATCHID))
        .isEqualTo(CURRENTMATCHID_DEFAULT);
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
            STARTTIMEAVAILABLE_DEFAULT,
            ENDTIMEAVAILABLE_DEFAULT,
            DURATION_DEFAULT,
            CURRENTMATCHID_DEFAULT,
            TIMESTAMP_DEFAULT);

    participantDatastore.addParticipant(participant);

    Participant participantFromUsername = participantDatastore.getParticipantFromUsername(PERSON_A);
    assertThat(participantFromUsername.getUsername()).isEqualTo(PERSON_A);
    assertThat(participantFromUsername.getStartTimeAvailable())
        .isEqualTo(STARTTIMEAVAILABLE_DEFAULT);
    assertThat(participantFromUsername.getEndTimeAvailable()).isEqualTo(ENDTIMEAVAILABLE_DEFAULT);
    assertThat(participantFromUsername.getDuration()).isEqualTo(DURATION_DEFAULT);
    assertThat(participantFromUsername.getCurrentMatchId()).isEqualTo(CURRENTMATCHID_DEFAULT);
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
            STARTTIMEAVAILABLE_DEFAULT,
            ENDTIMEAVAILABLE_DEFAULT,
            DURATION_DEFAULT,
            CURRENTMATCHID_DEFAULT,
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
            STARTTIMEAVAILABLE_DEFAULT,
            ENDTIMEAVAILABLE_DEFAULT,
            DURATION_DEFAULT,
            CURRENTMATCHID_DEFAULT,
            TIMESTAMP_DEFAULT);

    participantDatastore.addParticipant(participant);
    participantDatastore.removeParticipant(participant.getUsername());

    Participant participantFromUsername = participantDatastore.getParticipantFromUsername(PERSON_A);
    assertThat(participantFromUsername).isNull();
  }
}
