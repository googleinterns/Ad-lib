// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
  /** List of interests delimited by commas */
  private final String interests;
  /** Whether they want to be matched with a similar, any, or different Googler */
  private final MatchPreference matchPreference;

  /** Initialize constructor fields */
  public User(
      String username,
      int duration,
      String role,
      String productArea,
      String interests,
      MatchPreference matchPreference) {
    this.username = username;
    this.duration = duration;
    this.role = role;
    this.productArea = productArea;
    this.interests = interests;
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

  public String getInterests() {
    return interests;
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
        .add("interests", interests)
        .add("matchPreference", matchPreference.getValue())
        .toString();
  }
}
