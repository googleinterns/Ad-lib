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
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.AddParticipantHelper;
import com.google.sps.UsernameService;
import com.google.sps.datastore.MatchDatastore;
import com.google.sps.datastore.ParticipantDatastore;
import com.google.sps.datastore.UserDatastore;
import java.io.IOException;
import java.time.Clock;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that adds a participant to the queue and tries to find them a match immediately */
@WebServlet("/api/v1/add-participant")
public class AddParticipantServlet extends HttpServlet {

  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private final MatchDatastore matchDatastore = new MatchDatastore(datastore);
  private final ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);
  private final UserDatastore userDatastore = new UserDatastore(datastore);

  private final UsernameService usernameService =
      new UsernameService(UserServiceFactory.getUserService());

  private final AddParticipantHelper addParticipantHelper =
      new AddParticipantHelper(
          Clock.systemUTC(), matchDatastore, participantDatastore, userDatastore, usernameService);

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    addParticipantHelper.doPost(request, response);
  }
}
