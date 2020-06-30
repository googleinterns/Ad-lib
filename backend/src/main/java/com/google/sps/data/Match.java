package com.google.sps.data;

/** A match of two users */
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
