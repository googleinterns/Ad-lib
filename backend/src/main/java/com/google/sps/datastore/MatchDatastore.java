// Copyright 2020 Google LLC
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

package com.google.sps.datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.sps.data.Match;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Separates datastore method calls involving Match type from caller */
public final class MatchDatastore {

  // Datastore Key/Property constants
  private static final String KIND_MATCH = "Match";
  private static final String PROPERTY_FIRST_PARTICIPANT_USERNAME = "firstParticipantUsername";
  private static final String PROPERTY_SECOND_PARTICIPANT_USERNAME = "secondParticipantUsername";
  private static final String PROPERTY_DURATION = "duration";
  private static final String PROPERTY_TIMESTAMP = "timestamp";

  /** Datastore */
  private final DatastoreService datastore;

  /** Constructor that takes in DatastoreService */
  public MatchDatastore(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /** Return entity of match */
  private static Entity getEntityFromMatch(Match match) {
    // Set properties of entity
    Entity entity = new Entity(KIND_MATCH);
    entity.setProperty(PROPERTY_FIRST_PARTICIPANT_USERNAME, match.getFirstParticipantUsername());
    entity.setProperty(PROPERTY_SECOND_PARTICIPANT_USERNAME, match.getSecondParticipantUsername());
    entity.setProperty(PROPERTY_DURATION, match.getDuration());
    entity.setProperty(PROPERTY_TIMESTAMP, match.getTimestamp());
    return entity;
  }

  /** Put Match in datastore and return the match key id */
  public long addMatch(Match match) {
    // Create and insert entity into datastore
    Entity entity = getEntityFromMatch(match);
    datastore.put(entity);

    // Return match key id
    return entity.getKey().getId();
  }

  /** Return Match from entity, or null if entity is null */
  private static Match getMatchFromEntity(@Nonnull Entity entity) {
    return new Match(
        (String) entity.getProperty(PROPERTY_FIRST_PARTICIPANT_USERNAME),
        (String) entity.getProperty(PROPERTY_SECOND_PARTICIPANT_USERNAME),
        ((Long) entity.getProperty(PROPERTY_DURATION)).intValue(),
        (long) entity.getProperty(PROPERTY_TIMESTAMP));
  }

  /** Return match based on match datastore key id, or null if entity not found */
  @Nullable
  public Match getMatchFromId(long matchId) {
    Key matchKey = KeyFactory.createKey(KIND_MATCH, matchId);
    try {
      return getMatchFromEntity(datastore.get(matchKey));
    } catch (EntityNotFoundException e) {
      return null;
    }
  }
}
