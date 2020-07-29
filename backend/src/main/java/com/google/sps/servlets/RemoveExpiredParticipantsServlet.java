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

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.sps.data.MatchStatus;
import com.google.sps.data.Participant;
import com.google.sps.datastore.ParticipantDatastore;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that removes participant if expired */
@WebServlet("/api/v1/tasks/remove-expired")
public class RemoveExpiredParticipantsServlet extends HttpServlet {

  /** Extra padding time in minutes to ensure large enough meeting time block */
  private static final int PADDING_MINUTES = 10;

  // Get DatastoreService and instiate Match and Participant Datastores
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private final ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.println("Remove expired participants");

    List<Participant> unmatchedParticipants = participantDatastore.getUnmatchedParticipants();

    // Check if match exists and not returned yet
    for (Participant participant : unmatchedParticipants) {
      if (participant.getMatchStatus() == MatchStatus.UNMATCHED && isExpired(participant)) {
        participantDatastore.removeParticipant(participant.getUsername());
        return;
      }
    }
    response.setStatus(HttpServletResponse.SC_OK);
  }

  /**
   * Check if participant is expired (not enough time before endTimeAvailable to have a meeting of
   * duration with padding time)
   *
   * @return true if expired and should be removed, false if still valid
   */
  private boolean isExpired(Participant participant) {
    long currentTimeMillis = System.currentTimeMillis();
    // Participant is expired if the current time plus duration and padding time is after their
    // endTimeAvailable
    return (currentTimeMillis
            + TimeUnit.MINUTES.toMillis(participant.getDuration() + PADDING_MINUTES))
        > participant.getEndTimeAvailable();
  }
}
