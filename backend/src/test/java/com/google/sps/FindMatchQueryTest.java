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

import com.google.sps.data.Match;
import com.google.sps.data.Participant;
import com.google.sps.data.TimeHelper;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
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
  private static final String PERSON_D = "Person D";

  // Default parameters unused in query
  private static final long ID_DEFAULT = 0;
  private static final long TIMESTAMP_DEFAULT = 0;

  // Some times available until on 1/1/2020
  private static final ZonedDateTime TIME_0200PM = TimeHelper.getNewTimeToday(14, 0);
  private static final ZonedDateTime TIME_0250PM = TimeHelper.getNewTimeToday(14, 50);
  private static final ZonedDateTime TIME_0330PM = TimeHelper.getNewTimeToday(15, 30);
  private static final ZonedDateTime TIME_0400PM = TimeHelper.getNewTimeToday(16, 0);
  private static final ZonedDateTime TIME_0600PM = TimeHelper.getNewTimeToday(18, 0);
  private static final ZonedDateTime TIME_0800PM = TimeHelper.getNewTimeToday(20, 0);

  private static final int DURATION_15_MINUTES = 15;
  private static final int DURATION_30_MINUTES = 30;
  private static final int DURATION_45_MINUTES = 45;
  private static final int DURATION_60_MINUTES = 60;

  private FindMatchQuery query;
  private TimeHelper timeHelper;

  @Before
  public void setUp() {
    // Set "current" date to  1/1/2020 2:00pm ET
    ZonedDateTime dateTime =
        ZonedDateTime.of(
            /* year= */ 2020,
            /* month= */ 1,
            /* date= */ 1,
            /* hour= */ 14,
            /* minute= */ 0,
            /* second= */ 0,
            /* nanosecond= */ 0,
            /* zone= */ ZoneId.of("US/Eastern"));
    timeHelper = new TimeHelper(dateTime);

    query = new FindMatchQuery(dateTime);
    timeHelper = new TimeHelper(dateTime);
  }

  @Test
  public void compatibleTimeAndDuration() {
    // Two participants who are compatible in available time AND duration
    Participant participantA =
        new Participant(
            ID_DEFAULT, PERSON_A, TIME_0200PM, TIME_0400PM, DURATION_30_MINUTES, TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            ID_DEFAULT, PERSON_B, TIME_0200PM, TIME_0600PM, DURATION_15_MINUTES, TIMESTAMP_DEFAULT);

    Match match = query.findMatch(Arrays.asList(participantA), participantB);

    assertThat(match.getFirstParticipant().getUsername()).isEqualTo(PERSON_B);
    assertThat(match.getSecondParticipant().getUsername()).isEqualTo(PERSON_A);
    assertThat(match.getDuration()).isEqualTo(DURATION_15_MINUTES);
  }

  @Test
  public void compatibleTime() {
    // Two participants who are compatible in available time but NOT duration
    Participant participantA =
        new Participant(
            ID_DEFAULT, PERSON_A, TIME_0200PM, TIME_0400PM, DURATION_30_MINUTES, TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            ID_DEFAULT, PERSON_B, TIME_0200PM, TIME_0600PM, DURATION_60_MINUTES, TIMESTAMP_DEFAULT);

    Match match = query.findMatch(Arrays.asList(participantA), participantB);

    assertThat(match).isNull();
  }

  @Test
  public void compatibleDuration() {
    // Two participants who are compatible in duration but NOT available time
    Participant participantA =
        new Participant(
            ID_DEFAULT, PERSON_A, TIME_0200PM, TIME_0250PM, DURATION_45_MINUTES, TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            ID_DEFAULT, PERSON_B, TIME_0200PM, TIME_0400PM, DURATION_60_MINUTES, TIMESTAMP_DEFAULT);

    Match match = query.findMatch(Arrays.asList(participantA), participantB);

    assertThat(match).isNull();
  }

  @Test
  public void threeParticipantsAC() {
    // Three participants, A & B aren't compatible, but A & C are
    Participant participantA =
        new Participant(
            ID_DEFAULT, PERSON_A, TIME_0200PM, TIME_0400PM, DURATION_60_MINUTES, TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            ID_DEFAULT, PERSON_B, TIME_0200PM, TIME_0250PM, DURATION_45_MINUTES, TIMESTAMP_DEFAULT);
    Participant participantC =
        new Participant(
            ID_DEFAULT, PERSON_C, TIME_0200PM, TIME_0600PM, DURATION_60_MINUTES, TIMESTAMP_DEFAULT);

    Match match = query.findMatch(Arrays.asList(participantA, participantB), participantC);

    assertThat(match.getFirstParticipant().getUsername()).isEqualTo(PERSON_C);
    assertThat(match.getSecondParticipant().getUsername()).isEqualTo(PERSON_A);
    assertThat(match.getDuration()).isEqualTo(DURATION_60_MINUTES);
  }

  @Test
  public void threeParticipantsBC() {
    // Three participants, A & B aren't compatible, but B & C are
    Participant participantA =
        new Participant(
            ID_DEFAULT, PERSON_A, TIME_0200PM, TIME_0250PM, DURATION_30_MINUTES, TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            ID_DEFAULT, PERSON_B, TIME_0200PM, TIME_0400PM, DURATION_60_MINUTES, TIMESTAMP_DEFAULT);
    Participant participantC =
        new Participant(
            ID_DEFAULT, PERSON_C, TIME_0200PM, TIME_0600PM, DURATION_60_MINUTES, TIMESTAMP_DEFAULT);

    Match match = query.findMatch(Arrays.asList(participantA, participantB), participantC);

    assertThat(match.getFirstParticipant().getUsername()).isEqualTo(PERSON_C);
    assertThat(match.getSecondParticipant().getUsername()).isEqualTo(PERSON_B);
    assertThat(match.getDuration()).isEqualTo(DURATION_60_MINUTES);
  }

  @Test
  public void threeParticipantsTwoMatches() {
    // Three participants, A & B, A & C are compatible but only return A & C
    Participant participantA =
        new Participant(
            ID_DEFAULT, PERSON_A, TIME_0200PM, TIME_0400PM, DURATION_30_MINUTES, TIMESTAMP_DEFAULT);
    Participant participantB =
        new Participant(
            ID_DEFAULT, PERSON_B, TIME_0200PM, TIME_0600PM, DURATION_15_MINUTES, TIMESTAMP_DEFAULT);
    Participant participantC =
        new Participant(
            ID_DEFAULT, PERSON_C, TIME_0200PM, TIME_0800PM, DURATION_45_MINUTES, TIMESTAMP_DEFAULT);

    Match match = query.findMatch(Arrays.asList(participantA, participantB), participantC);

    assertThat(match.getFirstParticipant().getUsername()).isEqualTo(PERSON_C);
    assertThat(match.getSecondParticipant().getUsername()).isEqualTo(PERSON_A);
    assertThat(match.getDuration()).isEqualTo(DURATION_30_MINUTES);
  }
}
