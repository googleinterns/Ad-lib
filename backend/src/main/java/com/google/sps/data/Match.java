package com.google.sps.data;

/** A match of two participants */
public final class Match {

  /** Datastore entity ID */
  private final long id;
  /** First participant */
  private final Participant firstParticipant;
  /** Second participant */
  private final Participant secondParticipant;
  /** Duration of meeting */
  private final int duration;
  /** Time match found */
  private final long timestamp;

  /** Constructor */
  public Match(
      long id,
      Participant firstParticipant,
      Participant secondParticipant,
      int duration,
      long timestamp) {
    // Note: Match includes entire Participants instead of just usernames to distinguish between a
    // user participating
    // and finding a match multiple times. This wouldn't be an issue if we deleted matches from the
    // datastore.
    this.id = id;
    this.firstParticipant = firstParticipant;
    this.secondParticipant = secondParticipant;
    this.duration = duration;
    this.timestamp = timestamp;
  }

  public long getId() {
    return id;
  }

  public Participant getFirstParticipant() {
    return firstParticipant;
  }

  public Participant getSecondParticipant() {
    return secondParticipant;
  }

  public int getDuration() {
    return duration;
  }

  public long getTimestamp() {
    return timestamp;
  }
}
