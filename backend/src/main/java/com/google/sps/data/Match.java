package com.google.sps.data;

/** A match between two participants. */
public final class Match {

  /** Match datastore key ID */
  private final long id;
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
      long id,
      String firstParticipantUsername,
      String secondParticipantUsername,
      int duration,
      long timestamp) {
    this.id = id;
    this.firstParticipantUsername = firstParticipantUsername;
    this.secondParticipantUsername = secondParticipantUsername;
    this.duration = duration;
    this.timestamp = timestamp;
  }

  public long getId() {
    return id;
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
}
