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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.Match;
import com.google.sps.data.Participant;
import com.google.sps.datastore.MatchDatastore;
import com.google.sps.datastore.ParticipantDatastore;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

/** Servlet that returns some example content. */
@WebServlet("/api/v1/search-match")
public class SearchMatchServlet extends HttpServlet {

  // JSON key constants
  private static final String JSON_MATCHSTATUS = "matchStatus";
  private static final String JSON_FIRSTPARTICIPANTUSERNAME = "firstParticipantUsername";
  private static final String JSON_SECONDPARTICIPANTUSERNAME = "secondParticipantUsername";
  private static final String JSON_DURATION = "duration";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get participant username
    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();
    if (email == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid email.");
      return;
    }
    String username = email.split("@")[0];

    // Get DatastoreService and instiate Match and Participant Datastores
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    MatchDatastore matchDatastore = new MatchDatastore(datastore);
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);

    // Find participant's current match, if exists and not returned yet
    Participant participant = participantDatastore.getParticipantFromUsername(username);
    if (participant == null) {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST,
          "Participant with username " + username + "does not exist.");
      return;
    }
    long currentMatchId = participant.getCurrentMatchId();

    // Check if match exists and not returned yet
    if (currentMatchId == 0) {
      // No match yet
      JSONObject matchDoesNotExist = new JSONObject();
      matchDoesNotExist.put(JSON_MATCHSTATUS, "false");

      // Send the JSON back as the response
      response.setContentType("application/json");
      response.getWriter().println(matchDoesNotExist.toString());
    } else {
      // Match found
      Match match = matchDatastore.getMatchFromId(currentMatchId);
      if (match == null) {
        response.sendError(
            HttpServletResponse.SC_BAD_REQUEST,
            "No match entity in datastore with match id " + currentMatchId + ".");
        return;
      }

      // Remove matched participants from datastore
      participantDatastore.removeParticipant(match.getFirstParticipantUsername());
      participantDatastore.removeParticipant(match.getSecondParticipantUsername());

      JSONObject matchExists = new JSONObject();
      matchExists.put(JSON_MATCHSTATUS, "true");
      matchExists.put(JSON_FIRSTPARTICIPANTUSERNAME, match.getFirstParticipantUsername());
      matchExists.put(JSON_SECONDPARTICIPANTUSERNAME, match.getSecondParticipantUsername());
      matchExists.put(JSON_DURATION, match.getDuration());

      // Send the JSON back as the response
      response.setContentType("application/json");
      response.getWriter().println(matchExists.toString());
    }
  }
}
