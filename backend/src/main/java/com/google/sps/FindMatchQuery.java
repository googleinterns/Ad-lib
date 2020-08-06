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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

/** Class used to find a match for new Participant with unmatched Participants in datastore */
public final class FindMatchQuery {

  /** Extra padding time in minutes to ensure large enough meeting time block */
  private static final int PADDING_MINUTES = 10;
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
    Participant firstParticipant = newParticipant;
    int duration = firstParticipant.getDuration();

    // Get list of unmatched participants with same duration as and compatible time availaiblity
    // with firstParticipant
    List<Participant> compatibleTimeAvailabilityParticipants =
        participantDatastore.getUnmatchedParticipantsWithDuration(duration);

    // Compare first participant preferences with other participants to find match
    for (Participant secondParticipant : compatibleTimeAvailabilityParticipants) {
      // Make sure the first participant is not the same as the second
      if (firstParticipant.getUsername().equals(secondParticipant.getUsername())) {
        continue;
      }

      // Check endTimeAvailable compatibility
      boolean compatibleTime =
          secondParticipant.getEndTimeAvailable()
              > clock.millis() + TimeUnit.MINUTES.toMillis(duration + PADDING_MINUTES);
      if (!compatibleTime) {
        continue;
      }

      // Check match preference compatibility and get combined preference if compatible
      MatchPreference combinedMatchPreference =
          getCombinedMatchPreference(
              firstParticipant.getMatchPreference(), secondParticipant.getMatchPreference());
      if (combinedMatchPreference == null) {
        // Not compatible match pref
        continue;
      }
      // Check if combined match preference is satisfied depending on number of same inputs
      if (!isCombinedMatchPreferenceSatisfied(
          combinedMatchPreference, firstParticipant, secondParticipant)) {
        continue;
      }

      System.out.println(
          "match found: "
              + firstParticipant.getUsername()
              + " and "
              + secondParticipant.getUsername());
      // Found a match
      return new Match(
          firstParticipant.getUsername(),
          secondParticipant.getUsername(),
          duration,
          clock.millis());
    }
    // No inital match found
    return null;
  }

  /**
   * @return combined MatchPreference SIMILAR = both are SIMILAR OR one is SIMILAR and one is ANY;
   *     ANY = both are ANY; DIFFERENT = both are DIFFERENT = one is DIFFERENT and one is ANY. or
   *     null if match preferences are not compatible
   */
  private MatchPreference getCombinedMatchPreference(
      MatchPreference firstMatchPreference, MatchPreference secondMatchPreference) {
    if (firstMatchPreference == secondMatchPreference) {
      return firstMatchPreference;
    }
    if (firstMatchPreference == MatchPreference.ANY) {
      return secondMatchPreference;
    }
    if (secondMatchPreference == MatchPreference.ANY) {
      return firstMatchPreference;
    }
    return null;
  }

  /**
   * @return true if the participants are a match based on their combinedMatchPreference and the
   *     number of inputs that are the same, false if not a match
   */
  private boolean isCombinedMatchPreferenceSatisfied(
      MatchPreference combinedMatchPreference,
      Participant firstParticipant,
      Participant secondParticipant) {
    if (combinedMatchPreference == MatchPreference.ANY) {
      // both ANY, doesn't matter how many same inputs
      return true;
    }

    // Get lists of combined role, product area, interests for first and second participant
    List<String> firstFilledInputs = getAllFilledInputs(firstParticipant);
    List<String> secondFilledInputs = getAllFilledInputs(secondParticipant);
    System.out.println("First filled inputs: " + firstFilledInputs.toString());
    System.out.println("Second filled inputs: " + secondFilledInputs.toString());

    // Count number of shared inputs by finding size of intersection
    firstFilledInputs.retainAll(secondFilledInputs);
    int numSameInputs = firstFilledInputs.size();
    System.out.println("numSameInputs: " + numSameInputs);

    // Check if match based on preference and number of same inputs
    int maxNumFilledInputs = Math.max(firstFilledInputs.size(), secondFilledInputs.size());
    int minSameInputs = (maxNumFilledInputs + 1) / 2;
    System.out.println(minSameInputs);
    if (combinedMatchPreference == MatchPreference.SIMILAR && numSameInputs < minSameInputs) {
      return false;
    }
    if (combinedMatchPreference == MatchPreference.DIFFERENT && numSameInputs >= minSameInputs) {
      return false;
    }
    System.out.println("combined match pref satisfied");
    return true;
  }

  /**
   * @return all filled inputs of participant in one string delimited by a comma Assumes no role,
   *     PA, or interests have the same options
   */
  private List<String> getAllFilledInputs(Participant participant) {
    List<String> allFilledInputs = new ArrayList<String>();
    String role = participant.getRole();
    if (!role.equals("")) {
      allFilledInputs.add(role);
    }
    String productArea = participant.getProductArea();
    if (!productArea.equals("")) {
      allFilledInputs.add(productArea);
    }
    allFilledInputs.addAll(participant.getInterests());
    return allFilledInputs;
  }
}
