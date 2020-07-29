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

/** Represent whether or not a Participant is matched */
public enum MatchPreference {
  /** Prefer to be matched with different Googler */
  DIFFERENT(0),
  /** No preference on match similarity */
  ANY(1),
  /** Prefer to be matched with similar Googler */
  SIMILAR(2);

  private final int value;

  MatchPreference(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static MatchPreference forIntValue(int value) {
    for (MatchPreference preference : MatchPreference.values()) {
      if (preference.value == value) {
        return preference;
      }
    }
    throw new IllegalStateException("Unknown enum value.");
  }

  public static MatchPreference forStringValue(String value) {
    switch (value) {
      case "different":
        return MatchPreference.DIFFERENT;
      case "any":
        return MatchPreference.ANY;
      case "similar":
        return MatchPreference.SIMILAR;
      default:
        throw new IllegalStateException("Unknown enum value.");
    }
  }
}
