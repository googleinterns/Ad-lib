package com.google.sps.data;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/** A user who wants to be matched. */
public final class Participant {

  /** Google username (ldap) */
  private final String username;
  /** Time user starts being available */
  private long startTimeAvailable;
  /** Time user is available until */
  private long endTimeAvailable;
  /** How long user wants to chat */
  private int duration;
  /** Id of match in datastore, 0 if never found a match (can assign 0 at construction) */
  private long matchId;
  /** Time of submitted form */
  private final long timestamp;

  /** Initialize constructor fields */
  public Participant(
      String username,
      long startTimeAvailable,
      long endTimeAvailable,
      int duration,
      long matchId,
      long timestamp) {
    this.username = username;
    Preconditions.checkArgument(
        startTimeAvailable < endTimeAvailable,
        "Start available time must be before end available time.");
    this.startTimeAvailable = startTimeAvailable;
    this.endTimeAvailable = endTimeAvailable;
    this.duration = duration;
    this.matchId = matchId;
    this.timestamp = timestamp;
  }

  public String getUsername() {
    return username;
  }

  public long getStartTimeAvailable() {
    return startTimeAvailable;
  }

  public void setStartTimeAvailable(long startTimeAvailable) {
    this.startTimeAvailable = startTimeAvailable;
  }

  public long getEndTimeAvailable() {
    return endTimeAvailable;
  }

  public void setEndTimeAvailable(long endTimeAvailable) {
    this.endTimeAvailable = endTimeAvailable;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public long getMatchId() {
    return matchId;
  }

  public void setMatchId(long matchId) {
    this.matchId = matchId;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("username", username)
        .add("startTimeAvailable", startTimeAvailable)
        .add("endTimeAvailable", endTimeAvailable)
        .add("duration", duration)
        .add("matchId", matchId)
        .add("timestamp", timestamp)
        .toString();
  }
}
