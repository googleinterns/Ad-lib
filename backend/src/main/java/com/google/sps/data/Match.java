package com.google.sps.data;

import com.google.common.base.MoreObjects;

/** A match between two participants. */
public final class Match {

  /** First participant username (datastore key name) */
  private final String firstParticipantUsername;
  /** Second participant username (datastore key name) */
  private final String secondParticipantUsername;
  /** Duration of meeting */
  private final int duration;
  /** Time match found */
  private final long timestamp;

  /** Constructor */
  public Match(
      String firstParticipantUsername,
      String secondParticipantUsername,
      int duration,
      long timestamp) {
    // TODO (#58): Add shared interests
    this.firstParticipantUsername = firstParticipantUsername;
    this.secondParticipantUsername = secondParticipantUsername;
    this.duration = duration;
    this.timestamp = timestamp;
  }

  public String getFirstParticipantUsername() {
    return firstParticipantUsername;
  }

  public String getSecondParticipantUsername() {
    return secondParticipantUsername;
  }

  public int getDuration() {
    return duration;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("firstParticipantUsername", firstParticipantUsername)
        .add("secondParticipantUsername", secondParticipantUsername)
        .add("duration", duration)
        .add("timestamp", timestamp)
        .toString();
  }
}
