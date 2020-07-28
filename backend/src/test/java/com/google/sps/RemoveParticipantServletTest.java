// Copyright 2020 Google LLC
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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.Participant;
import com.google.sps.datastore.ParticipantDatastore;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RunWith(JUnit4.class)
public final class RemoveParticipantServletTest {

  private static final RemoveParticipantServlet removeParticipantServlet = mock(RemoveParticipantServlet.class);

  // Default values
  private static final long START_TIME_AVAILABLE_DEFAULT = 0;
  private static final long END_TIME_AVAILABLE_DEFAULT =
      ZonedDateTime.now().toInstant().toEpochMilli();
  private static final int DURATION_DEFAULT = 30;
  private static final String ROLE_DEFAULT = "Software engineer";
  private static final String PRODUCT_AREA_DEFAULT = "Ads";
  private static final MatchPreference MATCH_PREFERENCE_DEFAULT = MatchPreference.SIMILAR;
  private static final long MATCH_ID_DEFAULT = 0;
  private static final MatchStatus MATCH_STATUS_DEFAULT = MatchStatus.UNMATCHED;
  private static final long TIMESTAMP_DEFAULT = 0;
  private static final String USER = "User";

  // Get DatastoreService and instantiate Participant Datastore
  private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private static final ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();

    // Add one participant to datastore
    Participant participant =
        new Participant(
            USER,
            START_TIME_AVAILABLE_DEFAULT,
            END_TIME_AVAILABLE_DEFAULT,
            DURATION_DEFAULT,
            ROLE_DEFAULT,
            PRODUCT_AREA_DEFAULT,
            MATCH_PREFERENCE_DEFAULT,
            MATCH_ID_DEFAULT,
            MATCH_STATUS_DEFAULT,
            TIMESTAMP_DEFAULT);
    participantDatastore.addParticipant(participant);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Begin
  public void setUpTests() {
    when(removeParticipantServlet.getUsername()).thenReturn("User");
  }

  @Test
  public void testDoPost_shouldRemoveParticipant() {
    participantDatastore.removeParticipant(removeParticipantServlet.getUsername());

    Participant participantFromUsername = participantDatastore.getParticipantFromUsername(PERSON_A);
    assertThat(participantFromUsername).isNull();
  }
}