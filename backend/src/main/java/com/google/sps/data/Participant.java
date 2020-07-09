package com.google.sps.data;

import com.google.common.base.Preconditions;
import java.time.ZonedDateTime;

/** A user who wants to be matched. */
public final class Participant {

  /** Google username (ldap) */
  private final String username;
  /** Time user starts being available */
  // TODO: make these fields final once Participant and User classes are created
  private ZonedDateTime startTimeAvailable;
  /** Time user is available until */
  private ZonedDateTime endTimeAvailable;
  /** How long user wants to chat */
  private int duration;
  /** Current match in datastore, -1 if no match or already been returned */
  private final long currentMatchId;
  /** Time of submitted form */
  private final long timestamp;

  /** Initialize constructor fields */
  public Participant(
      String username,
      ZonedDateTime startTimeAvailable,
      ZonedDateTime endTimeAvailable,
      int duration,
      long currentMatchId,
      long timestamp) {
    this.username = username;
    Preconditions.checkArgument(
        startTimeAvailable.isBefore(endTimeAvailable),
        "Start available time must be before end available time.");
    this.startTimeAvailable = startTimeAvailable;
    this.endTimeAvailable = endTimeAvailable;
    this.duration = duration;
    this.currentMatchId = currentMatchId;
    this.timestamp = timestamp;
  }

  public String getUsername() {
    return username;
  }

  public ZonedDateTime getStartTimeAvailable() {
    return startTimeAvailable;
  }

  public void setStartTimeAvailable(ZonedDateTime startTimeAvailable) {
    this.startTimeAvailable = startTimeAvailable;
  }

  public ZonedDateTime getEndTimeAvailable() {
    return endTimeAvailable;
  }

  public void setEndTimeAvailable(ZonedDateTime endTimeAvailable) {
    this.endTimeAvailable = endTimeAvailable;
  }

  public int getDuration() {
    return duration;
  }

  public long getCurrentMatchId() {
    return currentMatchId;
  }

  public long getTimestamp() {
    return timestamp;
  }
}
