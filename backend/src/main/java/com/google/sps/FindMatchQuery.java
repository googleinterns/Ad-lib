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
import java.util.StringTokenizer;
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
  public Match findMatch(Participant firstParticipant) {
    int duration = firstParticipant.getDuration();

    // Get list of unmatched participants with same duration as and compatible time availaiblity
    // with firstParticipant
    List<Participant> compatibleTimeAvailabilityParticipants =
        participantDatastore.getParticipantsCompatibleTimeAvailibility(
            duration, firstParticipant.getEndTimeAvailable(), PADDING_MINUTES, clock);

    // Compare first participant preferences with other participants to find match
    for (Participant secondParticipant : compatibleTimeAvailabilityParticipants) {
      // Check match preference compatibility and get combined preference if compatible
      MatchPreference combinedMatchPreference =
          getCombinedMatchPreference(
              firstParticipant.getMatchPreference(), secondParticipant.getMatchPreference());
      if (combinedMatchPreference == null) {
        // Not compatible match pref
        continue;
      }

      // Check if combined match preference is satisfied depending on number of same fields
      if (!isCombinedMatchPreferenceSatisfied(
          combinedMatchPreference, firstParticipant, secondParticipant)) {
        continue;
      }

      System.out.println("match found");
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
   *     number of fields that are the same, false if not a match
   */
  private boolean isCombinedMatchPreferenceSatisfied(
      MatchPreference combinedMatchPreference,
      Participant firstParticipant,
      Participant secondParticipant) {
    if (combinedMatchPreference == MatchPreference.ANY) {
      // both ANY, doesn't matter how many same fields
      return true;
    }

    // Get lists of combined role, product area, interests for first and second participant
    String firstFilledFieldsList = getAllFilledFieldsList(firstParticipant);
    String secondFilledFieldsList = getAllFilledFieldsList(secondParticipant);
    System.out.println("First filled fields: " + firstFilledFieldsList);
    System.out.println("Second filled fields: " + secondFilledFieldsList);

    // Count num shared interests
    StringTokenizer firstFields = new StringTokenizer(firstFilledFieldsList, ",");
    StringTokenizer secondFields = new StringTokenizer(secondFilledFieldsList, ",");
    int firstNumFilledFields = firstFields.countTokens();
    int secondNumFilledFields = secondFields.countTokens();
    int numSameFields = 0;
    while (firstFields.hasMoreTokens()) {
      String firstField = firstFields.nextToken();
      secondFields = new StringTokenizer(secondFilledFieldsList, ",");
      while (secondFields.hasMoreTokens()) {
        if (firstField.equals(secondFields.nextToken())) {
          numSameFields++;
          System.out.println(firstField + " matches");
        }
      }
    }
    System.out.println("numSameFields: " + numSameFields);

    // Check if match based on preference and number of same fields
    int maxNumFilledFields = Math.max(firstNumFilledFields, secondNumFilledFields);
    int MIN_SAME_FIELDS = maxNumFilledFields / 2 + maxNumFilledFields % 2;
    System.out.println(MIN_SAME_FIELDS);
    if (combinedMatchPreference == MatchPreference.SIMILAR && numSameFields < MIN_SAME_FIELDS) {
      return false;
    }
    if (combinedMatchPreference == MatchPreference.DIFFERENT && numSameFields >= MIN_SAME_FIELDS) {
      return false;
    }
    System.out.println("combined match pref satisfied");
    return true;
  }

  /** Return all filled fields of participant in one string delimited by , */
  private String getAllFilledFieldsList(Participant participant) {
    StringBuilder sb = new StringBuilder();
    String role = participant.getRole();
    boolean isRoleFilled = !role.equals("");
    if (isRoleFilled) {
      sb.append(role + ",");
    }
    String productArea = participant.getProductArea();
    boolean isProductAreaFilled = !productArea.equals("");
    if (isProductAreaFilled) {
      sb.append(productArea + ",");
    }
    sb.append(participant.getInterests());
    return sb.toString();
  }
}
