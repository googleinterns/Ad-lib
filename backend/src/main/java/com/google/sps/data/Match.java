package com.google.sps.data;

/**
 * A match of two participants. Note: Match includes entire Participants (with unique ID per
 * submitted form) instead of just usernames because a username may be involved in multiple matches
 * over time. This issue can be resolved if we delete matches from the datastore after the users are
 * notified of the match.
 */
public final class Match {

  /** Datastore key ID */
  private final long id;
  /** First participant datastore key ID */
  private final long firstParticipantId;
  /** Second participant datastore key ID */
  private final long secondParticipantId;
  /** Duration of meeting */
  private final int duration;
  /** Time match found */
  private final long timestamp;

  /** Constructor */
  public Match(
      long id, long firstParticipantId, long secondParticipantId, int duration, long timestamp) {
    this.id = id;
    this.firstParticipantId = firstParticipantId;
    this.secondParticipantId = secondParticipantId;
    this.duration = duration;
    this.timestamp = timestamp;
  }

  public long getId() {
    return id;
  }

  public long getFirstParticipantId() {
    return firstParticipantId;
  }

  public long getSecondParticipantId() {
    return secondParticipantId;
  }

  public int getDuration() {
    return duration;
  }

  public long getTimestamp() {
    return timestamp;
  }
}
