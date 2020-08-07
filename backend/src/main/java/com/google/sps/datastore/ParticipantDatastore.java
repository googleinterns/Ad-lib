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

import com.google.appengine.api.datastore.DatastoreNeedIndexException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.sps.data.MatchPreference;
import com.google.sps.data.MatchStatus;
import com.google.sps.data.Participant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Separates datastore method calls involving Participant type from caller */
public class ParticipantDatastore {

  // Datastore Key/Property constants
  private static final String KIND_PARTICIPANT = "Participant";
  private static final String PROPERTY_USERNAME = "username";
  private static final String PROPERTY_START_TIME_AVAILABLE = "startTimeAvailable";
  private static final String PROPERTY_END_TIME_AVAILABLE = "endTimeAvailable";
  private static final String PROPERTY_DURATION = "duration";
  private static final String PROPERTY_ROLE = "role";
  private static final String PROPERTY_PRODUCT_AREA = "productArea";
  private static final String PROPERTY_INTERESTS = "interests";
  private static final String PROPERTY_MATCH_PREFERENCE = "matchPreference";
  private static final String PROPERTY_MATCH_ID = "matchId";
  private static final String PROPERTY_MATCH_STATUS = "matchStatus";
  private static final String PROPERTY_TIMESTAMP = "timestamp";

  /** Datastore */
  private final DatastoreService datastore;

  /** Constructor that takes in DatastoreService */
  public ParticipantDatastore(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /** Return entity created from participant */
  private static Entity createEntityFromParticipant(Participant participant) {
    // Set properties of entity based on participant fields
    Entity entity = new Entity(KIND_PARTICIPANT, participant.getUsername());
    entity.setProperty(PROPERTY_USERNAME, participant.getUsername());
    entity.setProperty(PROPERTY_START_TIME_AVAILABLE, participant.getStartTimeAvailable());
    entity.setProperty(PROPERTY_END_TIME_AVAILABLE, participant.getEndTimeAvailable());
    entity.setProperty(PROPERTY_DURATION, participant.getDuration());
    entity.setProperty(PROPERTY_ROLE, participant.getRole());
    entity.setProperty(PROPERTY_PRODUCT_AREA, participant.getProductArea());
    entity.setProperty(PROPERTY_INTERESTS, convertListToString(participant.getInterests()));
    entity.setProperty(PROPERTY_MATCH_PREFERENCE, participant.getMatchPreference().getValue());
    entity.setProperty(PROPERTY_MATCH_ID, participant.getMatchId());
    entity.setProperty(PROPERTY_MATCH_STATUS, participant.getMatchStatus().getValue());
    entity.setProperty(PROPERTY_TIMESTAMP, participant.getTimestamp());

    return entity;
  }

  /**
   * Put participant in datastore. Overwrite participant entity if participant with same username
   * already exists.
   */
  public void addParticipant(Participant participant) {
    // Insert entity into datastore
    datastore.put(createEntityFromParticipant(participant));
  }

  /** Return Participant Entity from username, or null if entity is not found */
  @Nullable
  private Entity getEntity(String username) {
    Key participantKey = KeyFactory.createKey(KIND_PARTICIPANT, username);
    try {
      return datastore.get(participantKey);
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  /** Return participant object from datastore participant entity, or null if entity is null */
  @Nullable
  private static Participant getParticipantFromEntity(@Nonnull Entity entity) {
    return new Participant(
        (String) entity.getProperty(PROPERTY_USERNAME),
        (long) entity.getProperty(PROPERTY_START_TIME_AVAILABLE),
        (long) entity.getProperty(PROPERTY_END_TIME_AVAILABLE),
        ((Long) entity.getProperty(PROPERTY_DURATION)).intValue(),
        (String) entity.getProperty(PROPERTY_ROLE),
        (String) entity.getProperty(PROPERTY_PRODUCT_AREA),
        convertStringToList((String) entity.getProperty(PROPERTY_INTERESTS)),
        MatchPreference.forIntValue(
            ((Long) entity.getProperty(PROPERTY_MATCH_PREFERENCE)).intValue()),
        (long) entity.getProperty(PROPERTY_MATCH_ID),
        MatchStatus.forIntValue(((Long) entity.getProperty(PROPERTY_MATCH_STATUS)).intValue()),
        (long) entity.getProperty(PROPERTY_TIMESTAMP));
  }

  /** Return Participant from username, or null if participant with username not in datastore */
  @Nullable
  public Participant getParticipantFromUsername(String username) {
    Entity entity = getEntity(username);
    if (entity == null) {
      return null;
    }
    return getParticipantFromEntity(entity);
  }

  /** Return list of all unmatched participants with duration */
  public List<Participant> getUnmatchedParticipantsWithDuration(int duration)
      throws DatastoreNeedIndexException {
    Query query = new Query(KIND_PARTICIPANT);

    // Create filters to get only unmatched participants with compatible time availability
    Filter sameDurationFilter =
        new FilterPredicate(PROPERTY_DURATION, FilterOperator.EQUAL, duration);
    Filter unmatchedFilter =
        new FilterPredicate(
            PROPERTY_MATCH_STATUS, FilterOperator.EQUAL, MatchStatus.UNMATCHED.getValue());

    // Combine filters into one, and filter query
    CompositeFilter compositeFilter =
        CompositeFilterOperator.and(/*compatibleTimeFilter, */ sameDurationFilter, unmatchedFilter);

    query.setFilter(compositeFilter);

    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    List<Participant> participants =
        results.stream().map(p -> getParticipantFromEntity(p)).collect(Collectors.toList());
    return participants;
  }

  /** Remove Participant from datastore */
  public void removeParticipant(String username) {
    Key participantKey = KeyFactory.createKey(KIND_PARTICIPANT, username);
    try {
      datastore.delete(participantKey);
    } catch (IllegalArgumentException e) {
      System.out.println(
          "Participant with username "
              + username
              + " cannot be removed because it is not in the datastore.");
    }
  }

  /** Convert list of strings to a string with each element delimited by a comma */
  private static String convertListToString(List<String> list) {
    return String.join(",", list);
  }

  /** Convert a string with each element delimited by a comma to a list of strings */
  public static List<String> convertStringToList(String str) {
    return Arrays.asList(str.split(","));
  }
}
