package com.google.sps.data;

/** Represent whether or not a participant is matched */
public enum MatchStatus {
  UNMATCHED(0),
  MATCHED(1);

  private final int value;

  MatchStatus(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static MatchStatus forIntValue(int value) {
    for (MatchStatus status : MatchStatus.values()) {
      if (status.value == value) {
        return status;
      }
    }
    throw new IllegalStateException("Unknown enum value.");
  }
}
