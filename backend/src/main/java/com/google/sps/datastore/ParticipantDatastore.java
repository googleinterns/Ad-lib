package com.google.sps.datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.data.Participant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Separates datastore method calls involving Participant type from caller */
public final class ParticipantDatastore {

  // Datastore Key/Property constants
  private static final String KIND_PARTICIPANT = "Participant";
  private static final String PROPERTY_USERNAME = "username";
  private static final String PROPERTY_STARTTIMEAVAILABLE = "startTimeAvailable";
  private static final String PROPERTY_ENDTIMEAVAILABLE = "endTimeAvailable";
  private static final String PROPERTY_DURATION = "duration";
  private static final String PROPERTY_MATCHID = "matchId";
  private static final String PROPERTY_TIMESTAMP = "timestamp";

  /** Datastore */
  private final DatastoreService datastore;

  /** Constructor that takes in DatastoreService */
  public ParticipantDatastore(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /** Return entity of participant */
  private Entity getEntityFromParticipant(Participant participant) {
    // Set properties of entity based on participant fields
    Entity entity = new Entity(KIND_PARTICIPANT, participant.getUsername());
    entity.setProperty(PROPERTY_USERNAME, participant.getUsername());
    entity.setProperty(PROPERTY_STARTTIMEAVAILABLE, participant.getStartTimeAvailable());
    entity.setProperty(PROPERTY_ENDTIMEAVAILABLE, participant.getEndTimeAvailable());
    entity.setProperty(PROPERTY_DURATION, participant.getDuration());
    entity.setProperty(PROPERTY_MATCHID, participant.getMatchId());
    entity.setProperty(PROPERTY_TIMESTAMP, participant.getTimestamp());

    return entity;
  }

  /**
   * Put participant in datastore. Overwrite participant entity if participant with same username
   * already exists.
   */
  public void addParticipant(Participant participant) {
    // Insert entity into datastore
    datastore.put(getEntityFromParticipant(participant));
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

  /** Return Participant from username, or null if participant with username not in datastore */
  @Nullable
  public Participant getParticipantFromUsername(String username) {
    Entity entity = getEntity(username);
    if (entity == null) {
      return null;
    }
    return getParticipantFromEntity(entity);
  }

  /** Return participant object from datastore participant entity, or null if entity is null */
  @Nullable
  private static Participant getParticipantFromEntity(@Nonnull Entity entity) {
    return new Participant(
        (String) entity.getProperty(PROPERTY_USERNAME),
        (long) entity.getProperty(PROPERTY_STARTTIMEAVAILABLE),
        (long) entity.getProperty(PROPERTY_ENDTIMEAVAILABLE),
        ((Long) entity.getProperty(PROPERTY_DURATION)).intValue(),
        (long) entity.getProperty(PROPERTY_MATCHID),
        (long) entity.getProperty(PROPERTY_TIMESTAMP));
  }

  /** Return list of all unmatched participants with duration */
  public List<Participant> getParticipantsWithDuration(int duration) {
    Query query = new Query(KIND_PARTICIPANT).addSort(PROPERTY_DURATION, SortDirection.ASCENDING);

    // Create filter to get only participants with same duration
    Filter sameDuration = new FilterPredicate(PROPERTY_DURATION, FilterOperator.EQUAL, duration);
    query.setFilter(sameDuration);

    PreparedQuery results = datastore.prepare(query);

    // Convert entities to list of participants
    List<Participant> participants = new ArrayList<Participant>();
    for (Entity entity : results.asIterable()) {
      participants.add(getParticipantFromEntity(entity));
    }
    return participants;
  }

  /** Remove Participant from datastore */
  public void removeParticipant(String username) {
    Key participantKey = KeyFactory.createKey(KIND_PARTICIPANT, username);
    datastore.delete(participantKey);
  }
}
