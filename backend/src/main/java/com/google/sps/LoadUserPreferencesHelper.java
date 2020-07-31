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

import com.google.sps.data.User;
import com.google.sps.datastore.UserDatastore;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/** Helper for LoadUserPreferencesServlet */
public class LoadUserPreferencesHelper {

  // JSON key constants
  private static final String JSON_EXISTING = "existing";
  private static final String JSON_USERNAME = "username";
  private static final String JSON_DURATION = "duration";
  private static final String JSON_ROLE = "role";
  private static final String JSON_PRODUCT_AREA = "productArea";
  private static final String JSON_INTERESTS = "interests";
  private static final String JSON_MATCH_PREFERENCE = "matchPreference";

  // HttpServlet request and response
  private final HttpServletRequest request;
  private final HttpServletResponse response;
  // User Datastore
  private final UserDatastore userDatastore;

  private final UsernameService usernameService;

  /** Constructor */
  public LoadUserPreferencesHelper(
      HttpServletRequest request,
      HttpServletResponse response,
      UserDatastore userDatastore,
      UsernameService usernameService) {
    this.request = request;
    this.response = response;
    this.userDatastore = userDatastore;
    this.usernameService = usernameService;
  }

  /** Returns a JSON object of the user's preferences */
  public void doGet() throws IOException {

    System.out.println("Request received");

    JSONObject userJson = new JSONObject();

    // Find participant's match, if exists and not returned yet
    String username = usernameService.getUsername();
    if (username == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not retrieve email");
      return;
    }

    User user = userDatastore.getUserFromUsername(username);
    if (user == null) {
      response.setStatus(
          HttpServletResponse.SC_OK,
          "No saved preferences for user with username " + username + ".");
      userJson.put(JSON_EXISTING, "false");
      response.setContentType("application/json");
      response.getWriter().println(userJson.toString());
      return;
    }

    response.setStatus(
        HttpServletResponse.SC_OK,
        "Loading saved preferences for user with username " + username + ".");

    userJson.put(JSON_EXISTING, "true");
    userJson.put(JSON_DURATION, user.getDuration());
    userJson.put(JSON_ROLE, user.getRole());
    userJson.put(JSON_PRODUCT_AREA, user.getProductArea());
    userJson.put(JSON_INTERESTS, new JSONArray(user.getInterests()));
    userJson.put(JSON_MATCH_PREFERENCE, user.getMatchPreference().getValue());

    // Send the JSON back as the response
    response.setContentType("application/json");
    response.getWriter().println(userJson.toString());
  }
}
