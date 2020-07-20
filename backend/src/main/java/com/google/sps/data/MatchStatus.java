package com.google.sps.data;

/** Represent whether or not a Participant is matched */
public enum MatchStatus {
  /** Not matched yet, waiting for match */
  UNMATCHED(0),
  /** Matched, can't be matched again yet */
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
