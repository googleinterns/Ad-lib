package com.google.sps.data;

/** A user who want to be matched. */
public final class Participant {

  private final long id;
  private final String ldap; // Google username
  private final long timeAvailableUntil; // Time user is available until
  private final String timezone; // User timezone
  private final int duration; // How long user wants to chat
  private final long timestamp; // Time of submitted form
  
  /** Initialize constructor fields */
  public Participant(long id, String ldap, long timeAvailableUntil, String timezone, int duration, long timestamp) {
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