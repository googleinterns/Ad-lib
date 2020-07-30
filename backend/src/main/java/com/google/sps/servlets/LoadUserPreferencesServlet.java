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
import com.google.sps.data.User;
import com.google.sps.datastore.UserDatastore;
import java.io.IOException;
import javax.annotation.Nullable;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/** Servlet that returns a JSON object of the user's preferences */
@WebServlet("/api/v1/load-user")
public class LoadUserPreferencesServlet extends HttpServlet {

  // JSON key constants
  private static final String JSON_EXISTING = "existing";
  private static final String JSON_USERNAME = "username";
  private static final String JSON_DURATION = "duration";
  private static final String JSON_ROLE = "role";
  private static final String JSON_PRODUCT_AREA = "productArea";
  private static final String JSON_INTERESTS = "interests";
  private static final String JSON_MATCH_PREFERENCE = "matchPreference";

  // Get DatastoreService and instantiate User Datastore
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private final UserDatastore userDatastore = new UserDatastore(datastore);
  // Get user username
  private final UserService userService = UserServiceFactory.getUserService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    System.out.println("Request received");

    response.setContentType("application/json");
    JSONObject userJson = new JSONObject();

    // Find participant's match, if exists and not returned yet
    String username = getUsername();
    if (username == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not retrieve email");
      return;
    }

    User user = userDatastore.getUserFromUsername(username);
    if (user == null) {
      userJson.put(JSON_EXISTING, "false");
      response.getWriter().println(userJson.toString());
      return;
    } else {
      userJson.put(JSON_EXISTING, "true");
      userJson.put(JSON_DURATION, user.getDuration());
      userJson.put(JSON_ROLE, user.getRole());
      userJson.put(JSON_PRODUCT_AREA, user.getProductArea());
      userJson.put(JSON_INTERESTS, new JSONArray(user.getInterests()));
      userJson.put(JSON_MATCH_PREFERENCE, user.getMatchPreference().getValue());
    }

    // Send the JSON back as the response
    response.getWriter().println(userJson.toString());
  }

  /** Retrieve user email address via Users API and parse for username */
  @Nullable
  private String getUsername() {
    String email = userService.getCurrentUser().getEmail();
    return email != null ? email.split("@")[0] : null;
  }
}
