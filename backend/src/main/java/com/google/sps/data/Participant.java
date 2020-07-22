package com.google.sps.data;

import com.google.sps.data.MatchStatus;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

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
  /** Id of match in datastore, 0 if never found a match (can assign 0 at construction) */
  private final long matchId;
  /** Matched already or not yet */
  private final com.google.sps.data.MatchStatus matchStatus;
  /** Time of submitted form */
  private final long timestamp;

  /** Initialize constructor fields */
  public Participant(
      String username,
      long startTimeAvailable,
      long endTimeAvailable,
      int duration,
      long matchId,
      com.google.sps.data.MatchStatus matchStatus,
      long timestamp) {
    this.username = username;
    Preconditions.checkArgument(
        startTimeAvailable < endTimeAvailable,
        "Start available time must be before end available time.");
    this.startTimeAvailable = startTimeAvailable;
    this.endTimeAvailable = endTimeAvailable;
    this.duration = duration;
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

  public long getMatchId() {
    return matchId;
  }

  public com.google.sps.data.MatchStatus getMatchStatus() {
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
        newMatchId,
        com.google.sps.data.MatchStatus.MATCHED,
        timestamp);
  }

  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("username", username)
        .add("startTimeAvailable", startTimeAvailable)
        .add("endTimeAvailable", endTimeAvailable)
        .add("duration", duration)
        .add("matchId", matchId)
        .add("matchStatus", matchStatus.getValue())
        .add("timestamp", timestamp)
        .toString();
  }
}
