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
import com.google.sps.FindMatchQuery;
import com.google.sps.data.Match;
import com.google.sps.data.Participant;
import com.google.sps.datastore.MatchDatastore;
import com.google.sps.datastore.ParticipantDatastore;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/** Servlet that returns some example content. */
@WebServlet("/api/v1/add-participant")
public class AddParticipantServlet extends HttpServlet {

  // Datastore Key/Property constants
  private static final String KEY_PARTICIPANT = "Participant";
  private static final String KEY_MATCH = "Match";
  private static final String PROPERTY_USERNAME = "username";
  private static final String PROPERTY_START_TIME_AVAILABLE = "startTimeAvailable";
  private static final String PROPERTY_END_TIME_AVAILABLE = "endTimeAvailable";
  private static final String PROPERTY_DURATION = "duration";
  private static final String PROPERTY_TIMESTAMP = "timestamp";
  private static final String PROPERTY_FIRST_PARTICIPANT = "firstParticipant";
  private static final String PROPERTY_SECOND_PARTICIPANT = "secondParticipant";

  // HTTP Request JSON key constants
  private static final String REQUEST_FORM_DETAILS = "formDetails";
  private static final String REQUEST_TIME_AVAILABLE_UNTIL = "timeAvailableUntil";
  private static final String REQUEST_DURATION = "duration";
  private static final String REQUEST_ROLE = "role";
  private static final String REQUEST_PRODUCT_AREA = "productArea";
  private static final String REQUEST_SAVE_PREFERENCE = "savePreference";
  private static final String REQUEST_MATCH_PREFERENCE = "matchPreference";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    JSONObject obj = retrieveRequestBody(request);
    if (obj == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not read request body");
    }
    JSONObject formDetails = obj.getJSONObject(REQUEST_FORM_DETAILS);

    // Retrieve the timeAvailableUntil input and convert to a UTC milliseconds
    long endTimeAvailable = formDetails.getLong(REQUEST_TIME_AVAILABLE_UNTIL);
    long startTimeAvailable = Instant.now().toEpochMilli();

    int duration = formDetails.getInt(REQUEST_DURATION);
    if (duration <= 0) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid duration.");
      return;
    }
    String role = formDetails.getString(REQUEST_ROLE);
    String productArea = formDetails.getString(REQUEST_PRODUCT_AREA);
    boolean savePreference = formDetails.getBoolean(REQUEST_SAVE_PREFERENCE);
    String matchPreference = formDetails.getString(REQUEST_MATCH_PREFERENCE);
    long timestamp = System.currentTimeMillis();

    String username = getUsername();
    if (username == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not retrieve email.");
      return;
    }

    // Create new Participant from input parameters
    // id is irrelevant, created when added to datastore
    Participant newParticipant =
        new Participant(
            username, startTimeAvailable, endTimeAvailable, duration, /* matchId=*/ 0, timestamp);

    // Get DatastoreService and instiate Match and Participant Datastores
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    MatchDatastore matchDatastore = new MatchDatastore(datastore);
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);

    // Find immediate match if possible
    FindMatchQuery query = new FindMatchQuery(Clock.systemUTC(), participantDatastore);
    Match match = query.findMatch(newParticipant);

    if (match != null) {
      // Match found, add to match datastore, update participant datastore
      long matchId = matchDatastore.addMatch(match);

      System.out.println(matchDatastore.getMatchFromId(matchId).toString());

      // Update participant entities with new matchId and null availability
      Participant currParticipant =
          participantDatastore.getParticipantFromUsername(match.getSecondParticipantUsername());
      nullAvailabilityAdd(participantDatastore, currParticipant, matchId);
      nullAvailabilityAdd(participantDatastore, newParticipant, matchId);
    } else {
      // Match not found, add participant to datastore
      participantDatastore.addParticipant(newParticipant);
    }

    response.setContentType("text/plain;charset=UTF-8");
    response.getWriter().println("Received form input details!");
  }

  /** Retrieve JSON body payload and convert to a JSONObject for parsing purposes */
  private JSONObject retrieveRequestBody(HttpServletRequest request) throws IOException {
    StringBuilder requestBuffer = new StringBuilder();
    try {
      BufferedReader reader = request.getReader();
      String currentLine;
      while ((currentLine = reader.readLine()) != null) {
        requestBuffer.append(currentLine);
      }
    } catch (IOException e) {
      return null;
    }
    return new JSONObject(requestBuffer.toString());
  }

  /** Retrieve user email address via Users API and parse for username */
  private String getUsername() {
    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();
    return email != null ? email.split("@")[0] : null;
  }

  /** Update participant entity with new matchId and null availability */
  private void nullAvailabilityAdd(
      ParticipantDatastore participantDatastore, Participant participant, long matchId) {
    participant.setMatchId(matchId);
    participant.setStartTimeAvailable(0);
    participant.setEndTimeAvailable(0);
    participant.setDuration(0);
    participantDatastore.addParticipant(participant);
  }
}
