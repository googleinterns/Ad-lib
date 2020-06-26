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

import com.google.gson.Gson;
import com.google.sps.data.Match;
import com.google.sps.data.Participant;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class FindMatchQuery {

  private Date date;
  
  /** Constructor */
  public FindMatchQuery() {
    date = new Date();
  }

  /** Constructor with manually set date*/
  public FindMatchQuery(Date date) {
    this.date = date;
  }

  /** Find match of new participant or add to list of participants, return whether or not match was found 
      right after being added */
  public Match findMatchQuery(List<Participant> participants) {
    
    // Compare new participant preferences with others in list to find match
    int numParticipants = participants.size();
    Participant newParticipant = participants.get(numParticipants - 1);
    for (int i = 0; i < numParticipants - 1; i++) {
      Participant currParticipant = participants.get(i);
      
      // Check if participants are looking for similar meeting duration
      int newDuration = newParticipant.getDuration();
      int currDuration = currParticipant.getDuration();
      boolean compatibleDuration = Math.abs(newDuration - currDuration) <= 15;
      int duration = Math.min(newDuration, currDuration);
      
      if (compatibleDuration) {
        // Check if participants are both free for that duration + extra
        long newTimeAvailableUntil = newParticipant.getTimeAvailableUntil();
        long currTimeAvailableUntil = currParticipant.getTimeAvailableUntil();
        boolean compatibleTime = date.getTime() + Duration.ofMinutes(duration + 15).toMillis() <=
                                    Math.min(newTimeAvailableUntil, currTimeAvailableUntil);
        
        if (compatibleTime) {
          // TODO: change match ID (currently -1 for easy error checking)
          return new Match(-1L, newParticipant, currParticipant, duration, date.getTime());
        }
        else {
          continue;
        }
      }
      else {
        continue;
      }
    }
    // No inital match found
    return null;
  }
}
