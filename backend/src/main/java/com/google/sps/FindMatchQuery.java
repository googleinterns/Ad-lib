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
import com.google.sps.data.MatchPreference;
import com.google.sps.data.Participant;
import com.google.sps.datastore.ParticipantDatastore;
import java.time.Clock;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

/** Class used to find a match for new Participant with unmatched Participants in datastore */
public final class FindMatchQuery {

  /** Extra padding time in minutes to ensure large enough meeting time block */
  private static final int PADDING_MINUTES = 10;
  /** Minimum number of matching fields to be similar */
  private static final int MIN_SAME_FIELDS = 1;
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
        participantDatastore.getParticipantsWithDuration(duration);

    // Set reference date time using clock
    long currentTimeMillis = clock.millis();

    long newEndTimeAvailable = newParticipant.getEndTimeAvailable();
    MatchPreference newMatchPreference = newParticipant.getMatchPreference();
    String newRole = newParticipant.getRole();
    String newProductArea = newParticipant.getProductArea();

    // Compare new participant preferences with other participants to find match
    for (Participant currParticipant : sameDurationParticipants) {
      // Check if participants are both free for that duration + extra
      long currEndTimeAvailable = currParticipant.getEndTimeAvailable();
      if (!isCompatibleTime(
          currentTimeMillis, duration, newEndTimeAvailable, currEndTimeAvailable)) {
        System.out.println("not compatible time");
        continue;
      }

      // Check match preference compatibility and get combined preference if compatible
      MatchPreference currMatchPreference = currParticipant.getMatchPreference();
      if (!isCompatibleMatchPreference(newMatchPreference, currMatchPreference)) {
        System.out.println("not compatible match pref");
        continue;
      }
      MatchPreference combinedMatchPreference =
          getCombinedMatchPreference(newMatchPreference, currMatchPreference);

      if (combinedMatchPreference != MatchPreference.ANY) {
        int numSameFields = getNumSameFields(newParticipant, currParticipant);
        System.out.println(numSameFields);
        if (combinedMatchPreference == MatchPreference.SIMILAR && numSameFields < MIN_SAME_FIELDS) {
          continue;
        } else if (combinedMatchPreference == MatchPreference.DIFFERENT
            && numSameFields >= MIN_SAME_FIELDS) {
          continue;
        }
      }
      System.out.println("found a match");
      return new Match(
          newParticipant.getUsername(), currParticipant.getUsername(), duration, currentTimeMillis);
    }
    // No inital match found
    return null;
  }

  /** Return true if compatible endTimeAvailable considering duration and padding */
  private boolean isCompatibleTime(
      long currentTimeMillis, int duration, long newEndTimeAvailable, long currEndTimeAvailable) {
    long earliestEndTimeAvailable = Math.min(newEndTimeAvailable, currEndTimeAvailable);
    return (currentTimeMillis + TimeUnit.MINUTES.toMillis(duration + PADDING_MINUTES))
        < earliestEndTimeAvailable;
  }

  /** Return true if compatible match preferences (same preference, one or both are any) */
  private boolean isCompatibleMatchPreference(
      MatchPreference newMatchPreference, MatchPreference currMatchPreference) {
    return (newMatchPreference == currMatchPreference)
        || (newMatchPreference == MatchPreference.ANY)
        || (currMatchPreference == MatchPreference.ANY);
  }

  /**
   * @return combined MatchPreference similar: both are similar, one is similar and one is any any:
   *     both are any different: both are different, one is different and one is any
   */
  private MatchPreference getCombinedMatchPreference(
      MatchPreference newMatchPreference, MatchPreference currMatchPreference) {
    return (newMatchPreference == MatchPreference.ANY) ? currMatchPreference : newMatchPreference;
  }

  /** Return the number of fields that both participants have in common */
  private int getNumSameFields(Participant newParticipant, Participant currParticipant) {
    int numSameFields = 0;
    if (newParticipant.getRole().equals(currParticipant.getRole())) {
      numSameFields++;
    }
    if (newParticipant.getProductArea().equals(currParticipant.getProductArea())) {
      numSameFields++;
    }
    return numSameFields;
  }
}
