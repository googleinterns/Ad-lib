package com.google.sps.data;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import java.time.ZonedDateTime;

/** A user who want to be matched. */
public final class Participant {

  /** Datastore key ID */
  private final long id;
  /** Google username (ldap) */
  private final String username;
  /** Time user is starts being available */
  private final ZonedDateTime startTimeAvailable;
  /** Time user is available until */
  private final ZonedDateTime endTimeAvailable;
  /** How long user wants to chat */
  private final int duration;
  /** Current match in datastore, -1 if no match or already been returned */
  private final Key currentMatchKey;
  /** Time of submitted form */
  private final long timestamp;

  /** Initialize constructor fields */
  public Participant(
      long id,
      String username,
      ZonedDateTime startTimeAvailable,
      ZonedDateTime endTimeAvailable,
      int duration,
      Key currentMatchKey,
      long timestamp) {
    this.id = id;
    this.username = username;
    Preconditions.checkArgument(
        startTimeAvailable.isBefore(endTimeAvailable),
        "Start available time must be before end available time.");
    this.startTimeAvailable = startTimeAvailable;
    this.endTimeAvailable = endTimeAvailable;
    this.duration = duration;
    this.currentMatchKey = currentMatchKey;
    this.timestamp = timestamp;
  }

  public long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public ZonedDateTime getStartTimeAvailable() {
    return startTimeAvailable;
  }

  public ZonedDateTime getEndTimeAvailable() {
    return endTimeAvailable;
  }

  public int getDuration() {
    return duration;
  }

  public Key getCurrentMatchKey() {
    return currentMatchKey;
  }

  public long getTimestamp() {
    return timestamp;
  }
}
