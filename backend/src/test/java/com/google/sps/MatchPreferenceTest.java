// Copyright 2019 Google LLC
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

package com.google.sps;

import static com.google.common.truth.Truth.assertThat;

import com.google.sps.data.MatchPreference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class MatchPreferenceTest {

  @Test
  public void validMatchPreferencesString() {
    assertThat(MatchPreference.forStringValue("different")).isEqualTo(MatchPreference.DIFFERENT);
    assertThat(MatchPreference.forStringValue("any")).isEqualTo(MatchPreference.ANY);
    assertThat(MatchPreference.forStringValue("similar")).isEqualTo(MatchPreference.SIMILAR);
  }

  @Test
  public void validMatchPreferencesInt() {
    assertThat(MatchPreference.forIntValue(0)).isEqualTo(MatchPreference.DIFFERENT);
    assertThat(MatchPreference.forIntValue(1)).isEqualTo(MatchPreference.ANY);
    assertThat(MatchPreference.forIntValue(2)).isEqualTo(MatchPreference.SIMILAR);
  }

  @Test(expected = IllegalStateException.class)
  public void invalidMatchPreference3Int() {
    MatchPreference mp = MatchPreference.forIntValue(3);
  }

  @Test(expected = IllegalStateException.class)
  public void invalidMatchPreferenceNeg1Int() {
    MatchPreference mp = MatchPreference.forIntValue(-1);
  }

  @Test
  public void validMatchPreferenceUppercaseString() {
    assertThat(MatchPreference.forStringValue("SIMILAR")).isEqualTo(MatchPreference.SIMILAR);
  }

  @Test(expected = IllegalStateException.class)
  public void invalidMatchPreferenceString() {
    MatchPreference mp = MatchPreference.forStringValue("none");
  }
}
