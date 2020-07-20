package com.google.sps.data;

import com.google.common.base.MoreObjects;

/** A user with saved preferences. */
public final class User {

  /** Google username (ldap) */
  private final String username;
  /** How long user wants to chat */
  private final int duration;
  /** Role at Google */
  private final String role;
  /** Product area at Google */
  private final String productArea;
  /** Whether they want to be matched with a similar, any, or different Googler */
  private final MatchPreference matchPreference;

  /** Initialize constructor fields */
  public User(
      String username,
      int duration,
      String role,
      String productArea,
      MatchPreference matchPreference) {
    this.username = username;
    this.duration = duration;
    this.role = role;
    this.productArea = productArea;
    this.matchPreference = matchPreference;
  }

  public String getUsername() {
    return username;
  }

  public int getDuration() {
    return duration;
  }

  public String getRole() {
    return role;
  }

  public String getProductArea() {
    return productArea;
  }

  public MatchPreference getMatchPreference() {
    return matchPreference;
  }

  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("username", username)
        .add("duration", duration)
        .add("role", role)
        .add("productArea", productArea)
        .add("matchPreference", matchPreference.getValue())
        .toString();
  }
}
