package com.google.sps.data;

import com.google.common.base.MoreObjects;

/** A user with saved preferences. */
public final class User {

  /** Datastore ID */
  private final long id;
  /** Google username (ldap) */
  private final String username;

  /** Initialize constructor fields */
  public User(long id, String username) {
    // TODO(#32): add User interests
    this.id = id;
    this.username = username;
  }

  public long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).add("username", username).toString();
  }
}
