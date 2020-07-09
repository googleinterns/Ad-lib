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
import com.google.sps.datastore.MatchDatastore;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

/** Servlet that returns some example content. */
@WebServlet("/api/v1/search-match")
public class SearchMatchServlet extends HttpServlet {

  // Datastore Key/Property constants
  private static final String KEY_MATCH = "Match";
  private static final String PROPERTY_TIMESTAMP = "timestamp";

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

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    MatchDatastore matchDatastore = new MatchDatastore(datastore);

    // Convert list of entities to list of matches
    List<Match> matches = matchDatastore.getAllMatches();

    JSONObject matchDoesNotExist = new JSONObject();
    matchDoesNotExist.put(JSON_MATCHSTATUS, "false");
    String matchDetails = matchDoesNotExist.toString(); // default if no match found

    // Replace with datastore functionality
    // Brute force search for match
    for (Match match : matches) {
      String firstParticipantUsername = match.getFirstParticipant().getUsername();
      String secondParticipantUsername = match.getSecondParticipant().getUsername();
      if (username.equals(firstParticipantUsername) || username.equals(secondParticipantUsername)) {
        JSONObject matchExists = new JSONObject();
        matchExists.put(JSON_MATCHSTATUS, "true");
        matchExists.put(JSON_FIRSTPARTICIPANTUSERNAME, firstParticipantUsername);
        matchExists.put(JSON_SECONDPARTICIPANTUSERNAME, secondParticipantUsername);
        matchExists.put(JSON_DURATION, match.getDuration());
        matchDetails = matchExists.toString();

        break;
      }
    }

    // Send the JSON back as the response
    response.setContentType("application/json");
    response.getWriter().println(matchDetails);
  }
}
