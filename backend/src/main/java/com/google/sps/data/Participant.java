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

package com.google.sps.data;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.util.List;

/** A user who wants to be matched. */
public final class Participant {

  /** Google username (ldap) */
  private final String username;
  /** Time user starts being available */
  private final long startTimeAvailable;
  /** Time user is available until */
  private final long endTimeAvailable;
  /** How long user wants to chat */
  private final int duration;
  /** Role at Google */
  private final String role;
  /** Product area at Google */
  private final String productArea;
  /** List of interests */
  private final List<String> interests;
  /** Whether they want to be matched with a similar, any, or different Googler */
  private final MatchPreference matchPreference;
  /** Id of match in datastore, 0 if never found a match (can assign 0 at construction) */
  private final long matchId;
  /** Matched already or not yet */
  private final MatchStatus matchStatus;
  /** Time of submitted form */
  private final long timestamp;

  /** Initialize constructor fields */
  public Participant(
      String username,
      long startTimeAvailable,
      long endTimeAvailable,
      int duration,
      String role,
      String productArea,
      List<String> interests,
      MatchPreference matchPreference,
      long matchId,
      MatchStatus matchStatus,
      long timestamp) {
    this.username = username;
    Preconditions.checkArgument(
        startTimeAvailable < endTimeAvailable,
        "Start available time must be before end available time.");
    this.startTimeAvailable = startTimeAvailable;
    this.endTimeAvailable = endTimeAvailable;
    this.duration = duration;
    this.role = role;
    this.productArea = productArea;
    this.interests = interests;
    this.matchPreference = matchPreference;
    this.matchId = matchId;
    this.matchStatus = matchStatus;
    this.timestamp = timestamp;
  }

  public String getUsername() {
    return username;
  }

  public long getStartTimeAvailable() {
    return startTimeAvailable;
  }

  public long getEndTimeAvailable() {
    return endTimeAvailable;
  }

  public int getDuration() {
    return duration;
  }

  public String getRole() {
    return role;
  }

  public String getProductArea() {
    return productArea;
  }

  public List<String> getInterests() {
    return interests;
  }

  public MatchPreference getMatchPreference() {
    return matchPreference;
  }

  public long getMatchId() {
    return matchId;
  }

  public MatchStatus getMatchStatus() {
    return matchStatus;
  }

  public long getTimestamp() {
    return timestamp;
  }

  /** Return participant with new matchId and nulled out availability */
  public Participant foundMatch(long newMatchId) {
    return new Participant(
        username,
        startTimeAvailable,
        endTimeAvailable,
        duration,
        role,
        productArea,
        interests,
        matchPreference,
        newMatchId,
        MatchStatus.MATCHED,
        timestamp);
  }

  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("username", username)
        .add("startTimeAvailable", startTimeAvailable)
        .add("endTimeAvailable", endTimeAvailable)
        .add("duration", duration)
        .add("role", role)
        .add("productArea", productArea)
        .add("matchPreference", matchPreference.getValue())
        .add("interests", interests.toString())
        .add("matchId", matchId)
        .add("matchStatus", matchStatus.getValue())
        .add("timestamp", timestamp)
        .toString();
  }
}
