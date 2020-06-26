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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.Match;
import com.google.sps.data.Participant;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* Servlet that returns some example content.
*/
@WebServlet("/search-match")
public class SearchMatchServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get participant ldap
    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();
    String ldap = email.split("@")[0];

    // Create and sort queries by time
    Query query = new Query("Match").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Convert list of entities to list of matches
    List<Match> matches = new ArrayList<Match>();
    for (Entity entity : results.asIterable()) {
        long id = (long) entity.getKey().getId();
        Participant firstParticipant = (Participant) entity.getProperty("firstParticipant");
        Participant secondParticipant = (Participant) entity.getProperty("secondParticipant");
        int duration = (int) entity.getProperty("duration"); 
        long timestamp = (long) entity.getProperty("timestamp");
        Match match = new Match(id, firstParticipant, secondParticipant, duration, timestamp);
        matches.add(match);
    }

    Match matchDetails = new Match(0, null, null, 0, System.currentTimeMillis()); // default if no match
    // Brute force search for match
    for (Match match : matches) {
      if (ldap.equals(match.getFirstParticipant().getLdap()) || 
            ldap.equals(match.getSecondParticipant().getLdap())) {
        matchDetails = match;
        break;
      }
    }
    
    // Send the JSON back as the response
    Gson gson = new Gson();
    response.setContentType("application/json");
    response.getWriter().println(gson.toJson(matchDetails));
  }
}