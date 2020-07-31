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

import com.google.sps.data.Match;
import com.google.sps.data.MatchStatus;
import com.google.sps.data.Participant;
import com.google.sps.datastore.MatchDatastore;
import com.google.sps.datastore.ParticipantDatastore;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

public class SearchMatchHelper {

  /** Extra padding time in minutes to ensure large enough meeting time block */
  private static final int PADDING_MINUTES = 10;

  // JSON key constants
  private static final String JSON_MATCH_STATUS = "matchStatus";
  private static final String JSON_THIS_USERNAME = "thisUsername";
  private static final String JSON_MATCH_USERNAME = "matchUsername";
  private static final String JSON_END_TIME_AVAILABLE = "endTimeAvailable";
  private static final String JSON_DURATION = "duration";

  // HttpServlet request and response
  private final HttpServletRequest request;
  private final HttpServletResponse response;

  // Match and Participant Datastores
  private final MatchDatastore matchDatastore;
  private final ParticipantDatastore participantDatastore;

  private final UsernameService usernameService;

  /** Constructor */
  public SearchMatchHelper(
      HttpServletRequest request,
      HttpServletResponse response,
      MatchDatastore matchDatastore,
      ParticipantDatastore participantDatastore,
      UsernameService usernameService) {
    this.request = request;
    this.response = response;
    this.matchDatastore = matchDatastore;
    this.participantDatastore = participantDatastore;
    this.usernameService = usernameService;
  }

  /** Search for the participant's current match and removes participant if expired */
  public void doGet() throws IOException {
    System.out.println("Request received");

    // Find participant's match, if exists and not returned yet
    String username = usernameService.getUsername();
    Participant participant = participantDatastore.getParticipantFromUsername(username);
    if (participant == null) {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST,
          "Participant with username " + username + " does not exist in datastore.");
      return;
    }

    // Check if match exists and not returned yet
    if (participant.getMatchStatus() == MatchStatus.UNMATCHED) {
      if (isExpired(participant)) {
        participantDatastore.removeParticipant(username);
        sendExpiredResponse(response, participant);
        return;
      }
      // No match yet
      sendNoMatchResponse(response);
      return;
    }

    // Match found (MatchStatus.MATCHED)
    long matchId = participant.getMatchId();
    Match match = matchDatastore.getMatchFromId(matchId);
    if (match == null) {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST,
          "No match entity in datastore with match id " + matchId + ".");
      return;
    }

    // Remove matched participants from datastore
    participantDatastore.removeParticipant(username);

    sendMatchResponse(response, match);
  }

  /**
   * Check if participant is expired (not enough time before endTimeAvailable to have a meeting of
   * duration with padding time)
   *
   * @return true if expired and should be removed, false if still valid
   */
  private boolean isExpired(Participant participant) {
    long currentTimeMillis = System.currentTimeMillis();
    // Participant is expired if the current time plus duration and padding time is after their
    // endTimeAvailable
    return (currentTimeMillis
            + TimeUnit.MINUTES.toMillis(participant.getDuration() + PADDING_MINUTES))
        > participant.getEndTimeAvailable();
  }

  /** Send JSON response for expired participant that has been removed from datastore */
  private void sendExpiredResponse(HttpServletResponse response, Participant participant)
      throws IOException {
    JSONObject expired = new JSONObject();
    expired.put(JSON_MATCH_STATUS, "expired");
    expired.put(JSON_END_TIME_AVAILABLE, participant.getEndTimeAvailable());
    expired.put(JSON_DURATION, participant.getDuration());

    // Send the JSON back as the response
    response.setStatus(HttpServletResponse.SC_OK, "Participant is expired");
    response.setContentType("application/json");
    response.getWriter().println(expired.toString());
  }

  /** Send JSON response for no match yet */
  private void sendNoMatchResponse(HttpServletResponse response) throws IOException {
    JSONObject noMatchYet = new JSONObject();
    noMatchYet.put(JSON_MATCH_STATUS, "false");

    // Send the JSON back as the response
    response.setStatus(HttpServletResponse.SC_OK, "Participant has no match yet");
    response.setContentType("application/json");
    response.getWriter().println(noMatchYet.toString());
  }

  /** Send JSON response for found a match */
  private void sendMatchResponse(HttpServletResponse response, Match match) throws IOException {
    String thisUsername = usernameService.getUsername();
    String matchUsername =
        thisUsername.equals(match.getFirstParticipantUsername())
            ? match.getSecondParticipantUsername()
            : match.getFirstParticipantUsername();
    JSONObject matchExists = new JSONObject();
    matchExists.put(JSON_MATCH_STATUS, "true");
    matchExists.put(JSON_THIS_USERNAME, thisUsername);
    matchExists.put(JSON_MATCH_USERNAME, matchUsername);
    matchExists.put(JSON_DURATION, match.getDuration());

    // Send the JSON back as the response
    response.setStatus(HttpServletResponse.SC_OK, "Participant has a match!");
    response.setContentType("application/json");
    response.getWriter().println(matchExists.toString());
  }
}
