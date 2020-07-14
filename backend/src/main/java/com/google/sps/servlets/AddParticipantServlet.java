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
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.FindMatchQuery;
import com.google.sps.data.Match;
import com.google.sps.data.Participant;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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
      return;
    }
    JSONObject formDetails = obj.getJSONObject(REQUEST_FORM_DETAILS);

    // Retrieve the timeAvailableUntil input and convert to a UTC ZonedDateTime
    long timeAvailableUntil = formDetails.getLong(REQUEST_TIME_AVAILABLE_UNTIL);
    ZoneId zoneId = ZoneId.of("UTC");
    ZonedDateTime startTimeAvailable = ZonedDateTime.now(Clock.systemUTC());
    ZonedDateTime endTimeAvailable =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(timeAvailableUntil), zoneId);

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

    // Retrieve user email address via Users API and parse for ldap
    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();
    if (email == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid email.");
      return;
    }
    String username = email.split("@")[0];

    // id is irrelevant, only relevant when getting from datastore
    Participant newParticipant =
        new Participant(
            /* id= */ -1L, username, startTimeAvailable, endTimeAvailable, duration, timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Find immediate match if possible
    FindMatchQuery query = new FindMatchQuery(Clock.systemUTC());
    Match match = query.findMatch(getParticipants(datastore), newParticipant);

    // Match found, add to datastore, delete matched participants from datastore
    if (match != null) {
      addMatchToDatastore(match, datastore);
      deleteParticipantFromDatastore(match.getSecondParticipant(), datastore);
    } else {
      // Match not found, insert participant entity into datastore
      Entity participantEntity = new Entity(KEY_PARTICIPANT);
      participantEntity.setProperty(PROPERTY_USERNAME, username);
      participantEntity.setProperty(PROPERTY_START_TIME_AVAILABLE, startTimeAvailable.toString());
      participantEntity.setProperty(PROPERTY_END_TIME_AVAILABLE, endTimeAvailable.toString());
      participantEntity.setProperty(PROPERTY_DURATION, duration);
      participantEntity.setProperty(PROPERTY_TIMESTAMP, timestamp);
      datastore.put(participantEntity);
    }

    response.setContentType("text/plain;charset=UTF-8");
    response.getWriter().println("Received form input details!");
  }

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

  /** Return list of current participants from datastore */
  private List<Participant> getParticipants(DatastoreService datastore) {
    // TODO: only return participants who are available now (not sometime in future)

    // Create and sort participant queries by time
    Query query = new Query(KEY_PARTICIPANT).addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    // Convert list of entities to list of participants
    List<Participant> participants = new ArrayList<Participant>();
    for (Entity entity : results.asIterable()) {
      long id = (long) entity.getKey().getId();
      String username = (String) entity.getProperty(PROPERTY_USERNAME);
      String startTimeAvailableString = (String) entity.getProperty(PROPERTY_START_TIME_AVAILABLE);
      ZonedDateTime startTimeAvailable =
          (ZonedDateTime) ZonedDateTime.parse(startTimeAvailableString);
      String endTimeAvailableString = (String) entity.getProperty(PROPERTY_END_TIME_AVAILABLE);
      ZonedDateTime endTimeAvailable = (ZonedDateTime) ZonedDateTime.parse(endTimeAvailableString);
      int duration = ((Long) entity.getProperty(PROPERTY_DURATION)).intValue();
      long timestamp = (long) entity.getProperty(PROPERTY_TIMESTAMP);
      Participant currParticipant =
          new Participant(id, username, startTimeAvailable, endTimeAvailable, duration, timestamp);
      participants.add(currParticipant);
    }
    return participants;
  }

  /** Add Match pair to datastore */
  private void addMatchToDatastore(Match match, DatastoreService datastore) {
    // Set properties of entity
    Entity matchEntity = new Entity(KEY_MATCH);
    matchEntity.setProperty(PROPERTY_FIRST_PARTICIPANT, match.getFirstParticipant());
    matchEntity.setProperty(PROPERTY_SECOND_PARTICIPANT, match.getSecondParticipant());
    matchEntity.setProperty(PROPERTY_DURATION, match.getDuration());
    matchEntity.setProperty(PROPERTY_TIMESTAMP, match.getTimestamp());

    // Insert entity into datastore
    datastore.put(matchEntity);
  }

  /** Delete matched participants from datastore */
  private void deleteParticipantFromDatastore(Participant participant, DatastoreService datastore) {
    Key participantEntityKey = KeyFactory.createKey(KEY_PARTICIPANT, participant.getId());
    datastore.delete(participantEntityKey);
  }
}
