package com.google.sps.data;

/** A user who want to be matched. */
public final class Participant {

  /** Datastore ID */
  private final long id;
  /** Google username */
  private final String ldap;
  /** Time user is available until */
  private final long timeAvailableUntil;
  /** User timezone */
  private final String timezone;
  /** How long user wants to chat */
  private final int duration;
  /** Time of submitted form */
  private final long timestamp;

  /** Initialize constructor fields */
  public Participant(
      long id,
      String ldap,
      long timeAvailableUntil,
      String timezone,
      int duration,
      long timestamp) {
    this.id = id;
    this.ldap = ldap;
    this.timeAvailableUntil = timeAvailableUntil;
    this.timezone = timezone;
    this.duration = duration;
    this.timestamp = timestamp;
  }

  public long getId() {
    return id;
  }

  public String getLdap() {
    return ldap;
  }

  public long getTimeAvailableUntil() {
    return timeAvailableUntil;
  }

  public String getTimezone() {
    return timezone;
  }

  public int getDuration() {
    return duration;
  }
}
