package com.google.sps.data;

/** A match of two users */
public final class Match {

  private final long id;
  private final Participant firstParticipant; // First participant
  private final Participant secondParticipant; // Second participant
  private final int duration; // Duration of meeting
  private final long timestamp; // Time match found
  
  /** Initialize constructor fields */
  public Match(long id, Participant firstParticipant, Participant secondParticipant, int duration, long timestamp) {
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