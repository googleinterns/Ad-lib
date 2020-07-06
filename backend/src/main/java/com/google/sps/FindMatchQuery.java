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
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/** Class used to find a match in a list of Participants with the most recently added Participant */
public final class FindMatchQuery {

  private Clock clock;
  private static int MAX_DURATION_DIFF = 15; // maximum difference in duration to be compatible
  private static int PADDING_TIME =
      15; // extra padding time to ensure large enough meeting time block

  /** Constructor */
  public FindMatchQuery() {
    clock = new Clock();
  }

  /** Constructor with manually set date */
  public FindMatchQuery(Clock clock) {
    this.clock = clock;
  }

  /**
   * Find match of new participant or add to list of participants, return whether or not match was
   * found right after being added
   */
  public Match findMatch(List<Participant> participants, Participant newParticipant) {

    // Compare new participant preferences with others in list to find match
    for (Participant currParticipant : participants) {

      // Check if participants are looking for similar meeting duration
      int newDuration = newParticipant.getDuration();
      int currDuration = currParticipant.getDuration();
      boolean compatibleDuration = Math.abs(newDuration - currDuration) <= MAX_DURATION_DIFF;
      int duration = Math.min(newDuration, currDuration);

      if (!compatibleDuration) {
        continue;
      }

      // Check if participants are both free for that duration + extra
      long newTimeAvailableUntil = newParticipant.getTimeAvailableUntil();
      long currTimeAvailableUntil = currParticipant.getTimeAvailableUntil();
      boolean compatibleTime =
          Instant.now(clock)
              .plusMillis(Duration.ofMinutes(duration + PADDING_TIME).toMillis())
              .isBefore(
                  Instant.ofEpochMillis(Math.min(newTimeAvailableUntil, currTimeAvailableUntil)));

      if (compatibleTime) {
        // TODO: change match ID (currently -1 for easy error checking)
        return new Match(
            /* id= */ -1L,
            /* firstParticipant= */ newParticipant,
            /* secondParticipant= */ currParticipant,
            /* duration= */ duration,
            /* timestamp= */ date.getTime());
      }
    }
    // No inital match found
    return null;
  }
}
