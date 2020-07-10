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

import com.google.sps.data.Match;
import com.google.sps.data.Participant;
import com.google.sps.datastore.ParticipantDatastore;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import javax.annotation.Nullable;

/** Class used to find a match for new Participant with unmatched Participants in datastore */
public final class FindMatchQuery {

  /** Maximum difference in duration to be compatible */
  private static final int MAX_DURATION_DIFF = 15;
  /** Extra padding time to ensure large enough meeting time block */
  private static final int PADDING_TIME = 10;
  /** Reference clock */
  private final Clock clock;
  /** Datastore of Participants */
  private final ParticipantDatastore participantDatastore;

  /** Constructor */
  public FindMatchQuery(Clock clock, ParticipantDatastore participantDatastore) {
    this.clock = clock;
    this.participantDatastore = participantDatastore;
  }

  /**
   * @return Match of new participant with unmatched participants by comparing duration and
   *     availibility, or null if no match yet
   */
  @Nullable
  public Match findMatch(Participant newParticipant) {
    int duration = newParticipant.getDuration();
    
    // Get list of unmatched participants with same duration as newParticipant
    List<Participant> sameDurationParticipants =
        participantDatastore.getSameDurationParticipants(duration);

    // Set reference date time using clock
    ZonedDateTime dateTime = ZonedDateTime.now(clock);

    // Compare new participant preferences with other participants to find match
    for (Participant currParticipant : unmatchedParticipants) {
      // Check if participants are both free for that duration + extra
      ZonedDateTime newEndTimeAvailable = newParticipant.getEndTimeAvailable();
      ZonedDateTime currEndTimeAvailable = currParticipant.getEndTimeAvailable();
      ZonedDateTime earliestEndTimeAvailable =
          getEarlier(newEndTimeAvailable, currEndTimeAvailable);
      boolean compatibleTime =
          dateTime.plusMinutes(duration + PADDING_TIME).isBefore(earliestEndTimeAvailable);

      if (compatibleTime) {
        return new Match(
            /* id= */ -1L,
            newParticipant.getUsername(),
            currParticipant.getUsername(),
            duration,
            dateTime.toInstant().toEpochMilli());
      }
    }
    // No inital match found
    return null;
  }

  /** Return earlier of two ZonedDateTime objects */
  private ZonedDateTime getEarlier(ZonedDateTime first, ZonedDateTime second) {
    return first.isBefore(second) ? first : second;
  }
}
