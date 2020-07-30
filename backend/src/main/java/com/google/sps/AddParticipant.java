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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.Match;
import com.google.sps.data.MatchPreference;
import com.google.sps.data.MatchStatus;
import com.google.sps.data.Participant;
import com.google.sps.data.User;
import com.google.sps.datastore.MatchDatastore;
import com.google.sps.datastore.ParticipantDatastore;
import com.google.sps.datastore.UserDatastore;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class AddParticipant {

  // HTTP Request JSON key constants
  private static final String REQUEST_FORM_DETAILS = "formDetails";
  private static final String REQUEST_END_TIME_AVAILABLE = "endTimeAvailable";
  private static final String REQUEST_DURATION = "duration";
  private static final String REQUEST_ROLE = "role";
  private static final String REQUEST_PRODUCT_AREA = "productArea";
  private static final String REQUEST_INTERESTS = "interests";
  private static final String REQUEST_SAVE_PREFERENCE = "savePreference";
  private static final String REQUEST_MATCH_PREFERENCE = "matchPreference";

  // HttpServlet request and response
  private final HttpServletRequest request;
  private final HttpServletResponse response;
  /** Reference clock */
  private final Clock clock;

  // Match, Participant, and User Datastores
  private final MatchDatastore matchDatastore;
  private final ParticipantDatastore participantDatastore;
  private final UserDatastore userDatastore;

  /** Constructor */
  public AddParticipant(
      HttpServletRequest request,
      HttpServletResponse response,
      Clock clock,
      MatchDatastore matchDatastore,
      ParticipantDatastore participantDatastore,
      UserDatastore userDatastore) {
    this.request = request;
    this.response = response;
    this.clock = clock;
    this.matchDatastore = matchDatastore;
    this.participantDatastore = participantDatastore;
    this.userDatastore = userDatastore;
  }

  /** Add participant do datastore and try to find match immediately */
  public void doPostHelper() throws IOException {
    // Retrieve JSON object request
    JSONObject obj = retrieveRequestBody(request);
    if (obj == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not read request body");
    }
    JSONObject formDetails = obj.getJSONObject(REQUEST_FORM_DETAILS);

    // Get new Participant from input parameters
    Participant newParticipant = getParticipantFromInputs(formDetails);
    if (newParticipant == null) {
      return;
    }

    // Add User to datastore if opted to save preferences
    boolean savePreference = formDetails.getBoolean(REQUEST_SAVE_PREFERENCE);
    if (savePreference) {
      userDatastore.addUser(getUserFromParticipant(newParticipant));
    }

    // Find immediate match if possible
    FindMatchQuery query = new FindMatchQuery(Clock.systemUTC(), participantDatastore);
    Match match = query.findMatch(newParticipant);

    if (match != null) {
      // Match found, add to match datastore, update participant datastore
      long matchId = matchDatastore.addMatch(match);

      // Update current participant entity with new matchId and null availability
      Participant secondParticipant =
          participantDatastore.getParticipantFromUsername(match.getSecondParticipantUsername());
      participantDatastore.addParticipant(secondParticipant.foundMatch(matchId));

      // Add new participant to datastore with new matchId and null availability
      participantDatastore.addParticipant(newParticipant.foundMatch(matchId));
    } else {
      // Match not found, add participant to datastore
      participantDatastore.addParticipant(newParticipant);
    }

    // Confirm received form input
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

  /** Get a Participant from form inputs */
  private Participant getParticipantFromInputs(JSONObject formDetails) throws IOException {
    // Get username from email
    String username = getUsername();
    if (username == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not retrieve email.");
      return null;
    }

    // Get endTimeAvailable and startTimeAvailable in milliseconds
    long endTimeAvailable = formDetails.getLong(REQUEST_END_TIME_AVAILABLE);
    long startTimeAvailable = clock.millis();

    // Get desired meeting duration
    int duration = formDetails.getInt(REQUEST_DURATION);
    if (duration <= 0) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid duration.");
      return null;
    }

    // Get personal preference fields
    String role = formDetails.getString(REQUEST_ROLE);
    String productArea = formDetails.getString(REQUEST_PRODUCT_AREA);
    JSONArray interestsJsonArray = formDetails.getJSONArray(REQUEST_INTERESTS);
    int numInterests = interestsJsonArray.length();
    List<String> interests = new ArrayList<String>();
    for (int i = 0; i < numInterests; i++) {
      interests.add(interestsJsonArray.getString(i));
    }
    MatchPreference matchPreference =
        getMatchPreference(formDetails.getString(REQUEST_MATCH_PREFERENCE));
    if (matchPreference == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid match preference.");
      return null;
    }

    long timestamp = System.currentTimeMillis();

    // Create and return new Participant from input parameters
    return new Participant(
        username,
        startTimeAvailable,
        endTimeAvailable,
        duration,
        role,
        productArea,
        interests,
        matchPreference,
        /* matchId=*/ 0,
        MatchStatus.UNMATCHED,
        timestamp);
  }

  /** Retrieve user email address via Users API and parse for username */
  public static String getUsername() {
    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();
    return email != null ? email.split("@")[0] : null;
  }

  /** Parse match preference string to */
  private static MatchPreference getMatchPreference(String matchPreferenceString) {
    switch (matchPreferenceString) {
      case "different":
        return MatchPreference.DIFFERENT;
      case "any":
        return MatchPreference.ANY;
      case "similar":
        return MatchPreference.SIMILAR;
      default:
        return null;
    }
  }

  /** Extract and return user fields from participant */
  private User getUserFromParticipant(Participant participant) {
    return new User(
        participant.getUsername(),
        participant.getDuration(),
        participant.getRole(),
        participant.getProductArea(),
        participant.getInterests(),
        participant.getMatchPreference());
  }
}
