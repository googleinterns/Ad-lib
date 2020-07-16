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
import com.google.sps.data.Match;
import com.google.sps.datastore.MatchDatastore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class MatchDatastoreTest {

  // Default values
  private static final long ID_DEFAULT = 123456;
  private static final int DURATION_DEFAULT = 30;
  private static final long TIMESTAMP_DEFAULT = 0;

  // Some usernames
  private static final String PERSON_A = "Person A";
  private static final String PERSON_B = "Person B";
  private static final String PERSON_C = "Person C";
  private static final String PERSON_D = "Person D";

  // Datastore Key/Property constants
  private static final String KIND_MATCH = "Match";
  private static final String PROPERTY_FIRSTPARTICIPANTUSERNAME = "firstParticipantUsername";
  private static final String PROPERTY_SECONDPARTICIPANTUSERNAME = "secondParticipantUsername";
  private static final String PROPERTY_DURATION = "duration";
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
  public void addOneMatch() throws EntityNotFoundException {
    // Add one match to datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    MatchDatastore matchDatastore = new MatchDatastore(datastore);
    Match match = new Match(PERSON_A, PERSON_B, DURATION_DEFAULT, TIMESTAMP_DEFAULT);

    long id = matchDatastore.addMatch(match);

    Key key = KeyFactory.createKey(KIND_MATCH, id);
    Entity entity = datastore.get(key);
    assertThat((String) entity.getProperty(PROPERTY_FIRSTPARTICIPANTUSERNAME)).isEqualTo(PERSON_A);
    assertThat((String) entity.getProperty(PROPERTY_SECONDPARTICIPANTUSERNAME)).isEqualTo(PERSON_B);
    assertThat(((Long) entity.getProperty(PROPERTY_DURATION)).intValue())
        .isEqualTo(DURATION_DEFAULT);
    assertThat((long) entity.getProperty(PROPERTY_TIMESTAMP)).isEqualTo(TIMESTAMP_DEFAULT);
  }

  @Test
  public void addTwoMatches() throws EntityNotFoundException {
    // Add two matches to datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    MatchDatastore matchDatastore = new MatchDatastore(datastore);
    Match match1 = new Match(PERSON_A, PERSON_B, DURATION_DEFAULT, TIMESTAMP_DEFAULT);
    Match match2 = new Match(PERSON_C, PERSON_D, DURATION_DEFAULT, TIMESTAMP_DEFAULT);

    long id1 = matchDatastore.addMatch(match1);
    long id2 = matchDatastore.addMatch(match2);

    Key key1 = KeyFactory.createKey(KIND_MATCH, id1);
    Key key2 = KeyFactory.createKey(KIND_MATCH, id2);
    Entity entity1 = datastore.get(key1);
    Entity entity2 = datastore.get(key2);
    assertThat((String) entity1.getProperty(PROPERTY_FIRSTPARTICIPANTUSERNAME)).isEqualTo(PERSON_A);
    assertThat((String) entity1.getProperty(PROPERTY_SECONDPARTICIPANTUSERNAME))
        .isEqualTo(PERSON_B);
    assertThat(((Long) entity1.getProperty(PROPERTY_DURATION)).intValue())
        .isEqualTo(DURATION_DEFAULT);
    assertThat((long) entity1.getProperty(PROPERTY_TIMESTAMP)).isEqualTo(TIMESTAMP_DEFAULT);
    assertThat((String) entity2.getProperty(PROPERTY_FIRSTPARTICIPANTUSERNAME)).isEqualTo(PERSON_C);
    assertThat((String) entity2.getProperty(PROPERTY_SECONDPARTICIPANTUSERNAME))
        .isEqualTo(PERSON_D);
    assertThat(((Long) entity2.getProperty(PROPERTY_DURATION)).intValue())
        .isEqualTo(DURATION_DEFAULT);
    assertThat((long) entity2.getProperty(PROPERTY_TIMESTAMP)).isEqualTo(TIMESTAMP_DEFAULT);
  }

  @Test
  public void addGetOneMatch() {
    // Add one match and return it using id to datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    MatchDatastore matchDatastore = new MatchDatastore(datastore);
    Match match = new Match(PERSON_A, PERSON_B, DURATION_DEFAULT, TIMESTAMP_DEFAULT);

    long id = matchDatastore.addMatch(match);

    Match matchFromId = matchDatastore.getMatchFromId(id);
    assertThat(matchFromId.getFirstParticipantUsername()).isEqualTo(PERSON_A);
    assertThat(matchFromId.getSecondParticipantUsername()).isEqualTo(PERSON_B);
    assertThat(matchFromId.getDuration()).isEqualTo(DURATION_DEFAULT);
    assertThat(matchFromId.getTimestamp()).isEqualTo(TIMESTAMP_DEFAULT);
  }

  @Test
  public void getNonexistentMatch() {
    // Try to get match from id that's not in datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    MatchDatastore matchDatastore = new MatchDatastore(datastore);

    Match matchFromId = matchDatastore.getMatchFromId(ID_DEFAULT);
    assertThat(matchFromId).isNull();
  }
}
