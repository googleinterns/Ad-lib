package com.google.sps.data;


/** A user with saved preferences. */
public final class User {

  /** Datastore ID */
  private final long id;
  /** Google username (ldap) */
  private final String username;
  /** Time of submitted form */
  private final long timestamp;

  /** Initialize constructor fields */
  public Participant(long id, String username, long timestamp) {
    this.id = id;
    this.username = username;
    this.timestamp = timestamp;
  }

  public long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }
}
