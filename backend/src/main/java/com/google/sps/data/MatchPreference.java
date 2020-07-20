package com.google.sps.data;

/** Represent whether or not a Participant is matched */
public enum MatchPreference {
  /** Prefer to be matched with different Googler */
  DIFFERENT(-1),
  /** No preference on match similarity */
  ANY(0),
  /** Prefer to be matched with similar Googler */
  SIMILAR(1);

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
}
