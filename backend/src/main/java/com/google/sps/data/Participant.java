package com.google.sps.data;

import java.time.ZonedDateTime;

/** A user who want to be matched. */
public final class Participant {

  /** Datastore ID */
  private final long id;
  /** Google username */
  private final String ldap;
  /** Time user is starts being available */
  private final ZonedDateTime startTimeAvailable;
  /** Time user is available until */
  private final ZonedDateTime endTimeAvailable;
  /** How long user wants to chat */
  private final int duration;
  /** Time of submitted form */
  private final long timestamp;

  /** Initialize constructor fields */
  public Participant(
      long id,
      String ldap,
      ZonedDateTime startTimeAvailable,
      ZonedDateTime endTimeAvailable,
      int duration,
      long timestamp) {
    this.id = id;
    this.ldap = ldap;
    this.startTimeAvailable = startTimeAvailable;
    this.endTimeAvailable = endTimeAvailable;
    this.duration = duration;
    this.timestamp = timestamp;
  }

  public long getId() {
    return id;
  }

  public String getLdap() {
    return ldap;
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
}
