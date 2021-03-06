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
