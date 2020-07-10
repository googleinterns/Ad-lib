package com.google.sps.data;

import com.google.appengine.api.datastore.Key;

/** A match between two participants. */
public final class Match {

  /** Match datastore key ID */
  private final long id;
  /** First participant datastore key */
  private final Key firstParticipantKey;
  /** Second participant datastore key */
  private final Key secondParticipantKey;
  /** Duration of meeting */
  private final int duration;
  /** Time match found */
  private final long timestamp;

  /** Constructor */
  public Match(
      long id, Key firstParticipantKey, Key secondParticipantKey, int duration, long timestamp) {
    this.id = id;
    this.firstParticipantKey = firstParticipantKey;
    this.secondParticipantKey = secondParticipantKey;
    this.duration = duration;
    this.timestamp = timestamp;
  }

  public long getId() {
    return id;
  }

  public Key getFirstParticipantKey() {
    return firstParticipantKey;
  }

  public Key getSecondParticipantKey() {
    return secondParticipantKey;
  }

  public int getDuration() {
    return duration;
  }

  public long getTimestamp() {
    return timestamp;
  }
}
