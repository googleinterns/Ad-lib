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
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. */
@WebServlet("/api/v1/add-participant")
public class AddParticipantServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Request parameter values
    // TODO: Check input values are filled (before allowing to submit)
    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();
    if (email == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid email.");
      return;
    }
    String username = email.split("@")[0];

    // Get and convert time availability parameters
    String timezone = request.getParameter("timezone");
    ZoneId zoneId = ZoneId.of(timezone); // TODO: convert input timezone to valid java ZoneId
    ZonedDateTime startTimeAvailable =
        ZonedDateTime.now(zoneId); // TODO: set to future time if not available now
    ZonedDateTime endTimeAvailable = getEndTimeAvailableParameter(request, zoneId);

    // Get duration parameter
    int duration = getDurationParameter(request, response);
    if (duration <= 0) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid duration.");
      return;
    }

    long timestamp = System.currentTimeMillis();

    // Create new Participant from input parameters
    // id is irrelevant, only relevant when getting from datastore
    Participant newParticipant =
        new Participant(
            username,
            startTimeAvailable,
            endTimeAvailable,
            duration,
            /* currentMatchId=*/ 0,
            timestamp);

    // Get DatastoreService and instiate Match and Participant Datastores
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    MatchDatastore matchDatastore = new MatchDatastore(datastore);
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);

    // Find immediate match if possible
    FindMatchQuery query = new FindMatchQuery(Clock.systemUTC(), participantDatastore);
    Match match = query.findMatch(newParticipant);

    // Add newParticipant to datastore
    participantDatastore.addParticipant(newParticipant);

    // Match found, add match to datastore, update recent match for Participants
    if (match != null) {
      long matchId = matchDatastore.addMatch(match);
      participantDatastore.updateNewMatch(match.getFirstParticipantUsername(), matchId);
      participantDatastore.updateNewMatch(match.getSecondParticipantUsername(), matchId);
    }

    // Redirect back to the HTML page
    response.sendRedirect("/index.html");
  }

  /** Return zoned endTimeAvailable */
  private ZonedDateTime getEndTimeAvailableParameter(HttpServletRequest request, ZoneId zoneId) {
    Instant endTimeAvailableInstant =
        Instant.ofEpochMilli(
            Long.parseLong(
                request.getParameter("endTimeAvailable"))); // TODO: figure out input format
    return endTimeAvailableInstant.atZone(zoneId);
  }

  /**
   * Return duration from request parameter (positive integer value, or -1 if invalid or negative)
   */
  private int getDurationParameter(HttpServletRequest request, HttpServletResponse response) {
    String durationString = request.getParameter("duration");
    int duration = -1;

    if (durationString != null) {
      try {
        int parsed = Integer.parseInt(durationString);
        if (parsed >= 0) {
          duration = parsed;
        }
      } catch (NumberFormatException e) {
        duration = -1;
      }
    }
    return duration;
  }
}
