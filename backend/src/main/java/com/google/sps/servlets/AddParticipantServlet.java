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
import com.google.sps.data.Match;
import com.google.sps.data.Participant;
import com.google.sps.FindMatchQuery;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* Servlet that returns some example content.
*/
@WebServlet("/add-participant")
public class AddParticipantServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Request parameter values
    // TODO: Error check input values (before allowing to submit)
    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();
    String ldap = email.split("@")[0];
    long timeAvailableUntil = Long.parseLong(request.getParameter("timeAvailableUntil"));
    String timezone = request.getParameter("timezone");
    int duration = Integer.parseInt(request.getParameter("duration"));
    long timestamp = System.currentTimeMillis();
    
    // Set properties of entity
    Entity participantEntity = new Entity("Participant");
    participantEntity.setProperty("ldap", ldap);
    participantEntity.setProperty("timeAvailableUntil", timeAvailableUntil);
    participantEntity.setProperty("timezone", timezone);
    participantEntity.setProperty("duration", duration);
    participantEntity.setProperty("timestamp", timestamp);

    // Insert entity into datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(participantEntity);

    // Find match if possible or add to datastore.
    FindMatchQuery findMatchQuery = new FindMatchQuery();
    Match match = findMatchQuery.findMatchQuery(getParticipants());
    if (match != null) {
      addMatchToDatastore(match, datastore);
      deleteParticipantsFromList(match, datastore);
    }

    // Redirect back to the HTML page
    response.sendRedirect("/index.html");
  }

  /** Return list of current participants from datastore*/
  private List<Participant> getParticipants() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Create and sort participant queries by time
    Query query = new Query("Participant").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    // Convert list of entities to list of participants
    List<Participant> participants = new ArrayList<Participant>();
    for (Entity entity : results.asIterable()) {
        long id = (long) entity.getKey().getId();
        String ldap = (String) entity.getProperty("ldap");
        long timeAvailableUntil = (long) entity.getProperty("timeAvailableUntil"); 
        String timezone = (String) entity.getProperty("timezone"); 
        int duration = (int) entity.getProperty("duration"); 
        long timestamp = (long) entity.getProperty("timestamp");
        Participant newParticipant = new Participant(id, ldap, timeAvailableUntil, timezone, duration, timestamp);
        participants.add(newParticipant);
    }

    return participants;
  }

  /** Add Match pair to datastore */
  private void addMatchToDatastore(Match match, DatastoreService datastore) {
    // Set properties of entity
    Entity matchEntity = new Entity("Match");
    matchEntity.setProperty("firstParticipant", match.getFirstParticipant());
    matchEntity.setProperty("secondParticipant", match.getSecondParticipant());
    matchEntity.setProperty("duration", match.getDuration());
    matchEntity.setProperty("timestamp", match.getTimestamp());

    // Insert entity into datastore
    datastore.put(matchEntity);
  }

  /** Delete matched participants from datastore */
  private void deleteParticipantsFromList(Match match, DatastoreService datastore) {
    Key firstParticipantEntityKey = KeyFactory.createKey("Participant", match.getFirstParticipant().getId());
    datastore.delete(firstParticipantEntityKey);
    Key secondParticipantEntityKey = KeyFactory.createKey("Participant", match.getSecondParticipant().getId());
    datastore.delete(secondParticipantEntityKey);
  }
}