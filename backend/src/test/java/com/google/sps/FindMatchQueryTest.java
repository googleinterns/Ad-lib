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
import com.google.sps.data.Time;
import java.util.Arrays;
import java.util.Date;
import java.util.Calendar;
import java.util.Objects;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;

/** */
@RunWith(JUnit4.class)
public final class FindMatchQueryTest {

  // Some people that we can use in our tests
  private static final String PERSON_A = "Person A";
  private static final String PERSON_B = "Person B";
  private static final String PERSON_C = "Person C";
  private static final String PERSON_D = "Person D";

  // Default parameters unused in query
  private static final long ID_DEFAULT = 0;
  private static final String TIMEZONE_DEFAULT = "";
  private static final long TIMESTAMP_DEFAULT = 0;

  // Some times available until on 6/25/2020
  private static final long TIME_0250PM = Time.getTimeMillis(14, 50);
  private static final long TIME_0330PM = Time.getTimeMillis(15, 30);
  private static final long TIME_0400PM = Time.getTimeMillis(16, 0);
  private static final long TIME_0600PM = Time.getTimeMillis(18, 0);

  private static final int DURATION_15_MINUTES = 15;
  private static final int DURATION_30_MINUTES = 30;
  private static final int DURATION_45_MINUTES = 45;
  private static final int DURATION_60_MINUTES = 60;

  private FindMatchQuery query;
  private Time time;

  @Before
  public void setUp() {
    // Set "current" date to  1/1/2020 2:00pm GMT
    Calendar c = Calendar.getInstance();
    c.set(2020, 0, 1, 14, 0, 0);
    c.set(Calendar.MILLISECOND, 0);
    Date date = c.getTime();

    query = new FindMatchQuery(date);
    time = new Time(date);
  }

  @Test
  public void compatibleTimeAndDuration() {
    /* Two participants who are compatible in available time AND duration */
    Participant participantA = new Participant(ID_DEFAULT, PERSON_A, TIME_0400PM, TIMEZONE_DEFAULT, DURATION_30_MINUTES, TIMESTAMP_DEFAULT);
    Participant participantB = new Participant(ID_DEFAULT, PERSON_B, TIME_0600PM, TIMEZONE_DEFAULT, DURATION_15_MINUTES, TIMESTAMP_DEFAULT);
    
    Match actual = query.findMatchQuery(Arrays.asList(participantA, participantB));

    assertThat(actual.getFirstParticipant().getLdap().equals(PERSON_B));
    assertThat(actual.getSecondParticipant().getLdap().equals(PERSON_A));
    assertThat(actual.getDuration() == DURATION_15_MINUTES);
  }

  @Test
  public void compatibleTime() {
    /* Two participants who are compatible in available time but NOT duration */
    Participant participantA = new Participant(ID_DEFAULT, PERSON_A, TIME_0400PM, TIMEZONE_DEFAULT, DURATION_30_MINUTES, TIMESTAMP_DEFAULT);
    Participant participantB = new Participant(ID_DEFAULT, PERSON_B, TIME_0600PM, TIMEZONE_DEFAULT, DURATION_60_MINUTES, TIMESTAMP_DEFAULT);
    
    Match actual = query.findMatchQuery(Arrays.asList(participantA, participantB));

    assertThat(Objects.isNull(actual));
  }

  @Test
  public void compatibleDuration() {
    /* Two participants who are compatible in duration but NOT available time */
    Participant participantA = new Participant(ID_DEFAULT, PERSON_A, TIME_0250PM, TIMEZONE_DEFAULT, DURATION_45_MINUTES, TIMESTAMP_DEFAULT);
    Participant participantB = new Participant(ID_DEFAULT, PERSON_B, TIME_0400PM, TIMEZONE_DEFAULT, DURATION_60_MINUTES, TIMESTAMP_DEFAULT);
    
    Match actual = query.findMatchQuery(Arrays.asList(participantA, participantB));
    
    assertThat(Objects.isNull(actual));
  }

  @Test
  public void threeParticipants13() {
    /* Three participants, 1st and 2nd aren't compatible, but 1st and 3rd are */
    Participant participantA = new Participant(ID_DEFAULT, PERSON_A, TIME_0400PM, TIMEZONE_DEFAULT, DURATION_60_MINUTES, TIMESTAMP_DEFAULT);
    Participant participantB = new Participant(ID_DEFAULT, PERSON_B, TIME_0250PM, TIMEZONE_DEFAULT, DURATION_45_MINUTES, TIMESTAMP_DEFAULT);
    Participant participantC = new Participant(ID_DEFAULT, PERSON_C, TIME_0600PM, TIMEZONE_DEFAULT, DURATION_60_MINUTES, TIMESTAMP_DEFAULT);
    
    Match actual = query.findMatchQuery(Arrays.asList(participantA, participantB, participantC));
    
    assertThat(actual.getFirstParticipant().getLdap().equals(PERSON_C));
    assertThat(actual.getSecondParticipant().getLdap().equals(PERSON_A));
    assertThat(actual.getDuration() == DURATION_60_MINUTES);
  }

  @Test
  public void threeParticipants23() {
    /* Three participants, 1st and 2nd aren't compatible, but 2nd and 3rd are */
    Participant participantA = new Participant(ID_DEFAULT, PERSON_A, TIME_0250PM, TIMEZONE_DEFAULT, DURATION_45_MINUTES, TIMESTAMP_DEFAULT);
    Participant participantB = new Participant(ID_DEFAULT, PERSON_B, TIME_0400PM, TIMEZONE_DEFAULT, DURATION_60_MINUTES, TIMESTAMP_DEFAULT);
    Participant participantC = new Participant(ID_DEFAULT, PERSON_C, TIME_0600PM, TIMEZONE_DEFAULT, DURATION_60_MINUTES, TIMESTAMP_DEFAULT);
    
    Match actual = query.findMatchQuery(Arrays.asList(participantA, participantB, participantC));
    
    assertThat(actual.getFirstParticipant().getLdap().equals(PERSON_C));
    assertThat(actual.getSecondParticipant().getLdap().equals(PERSON_A));
    assertThat(actual.getDuration() == DURATION_60_MINUTES);
  }

  @Test
  public void fourParticipantsTwoMatches() {
    /* Three participants, 1st and 2nd aren't compatible, but 2nd and 3rd are */
    Participant participantA = new Participant(ID_DEFAULT, PERSON_A, TIME_0400PM, TIMEZONE_DEFAULT, DURATION_30_MINUTES, TIMESTAMP_DEFAULT);
    Participant participantB = new Participant(ID_DEFAULT, PERSON_B, TIME_0600PM, TIMEZONE_DEFAULT, DURATION_15_MINUTES, TIMESTAMP_DEFAULT);
    Participant participantC = new Participant(ID_DEFAULT, PERSON_C, TIME_0330PM, TIMEZONE_DEFAULT, DURATION_60_MINUTES, TIMESTAMP_DEFAULT);
    Participant participantD = new Participant(ID_DEFAULT, PERSON_D, TIME_0400PM, TIMEZONE_DEFAULT, DURATION_60_MINUTES, TIMESTAMP_DEFAULT);
    
    Match actual = query.findMatchQuery(Arrays.asList(participantA, participantB, participantC, participantD));
    
    // Return first match found, not second
    assertThat(actual.getFirstParticipant().getLdap().equals(PERSON_B));
    assertThat(actual.getSecondParticipant().getLdap().equals(PERSON_A));
    assertThat(actual.getDuration() == DURATION_15_MINUTES);
  }
}