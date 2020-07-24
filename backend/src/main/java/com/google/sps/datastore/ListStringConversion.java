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

package com.google.sps.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ListStringConversion {

  /** Convert list of strings to a string with each element delimited by a comma */
  public static String listToString(List<String> list) {
    StringBuilder sb = new StringBuilder();
    for (String str : list) {
      sb.append(str + ",");
    }
    return sb.toString();
  }

  /** Convert a string with each element delimited by a comma to a list of strings */
  public static List<String> stringToList(String str) {
    StringTokenizer st = new StringTokenizer(str, ",");
    List<String> list = new ArrayList<String>();
    while (st.hasMoreTokens()) {
      list.add(st.nextToken());
    }
    return list;
  }
}
