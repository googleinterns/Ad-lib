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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/** Separates datastore method calls involving Participant type from caller */
public final class ParticipantDatastore {

  // Datastore Key/Property constants
  private static final String KIND_PARTICIPANT = "Participant";
  private static final String PROPERTY_USERNAME = "username";
  private static final String PROPERTY_STARTTIMEAVAILABLE = "startTimeAvailable";
  private static final String PROPERTY_ENDTIMEAVAILABLE = "endTimeAvailable";
  private static final String PROPERTY_DURATION = "duration";
  private static final String PROPERTY_CURRENTMATCHID = "currentMatchId";
  private static final String PROPERTY_TIMESTAMP = "timestamp";

  /** Formatter for converting between String and ZonedDateTime */
  private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;

  /** Datastore */
  private final DatastoreService datastore;

  /** Constructor that takes in DatastoreService */
  public ParticipantDatastore(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /**
   * Put participant in datastore. Overwrite participant entity if participant with same username
   * already exists.
   */
  public void addParticipant(Participant participant) {
    // Set properties of entity based on participant fields
    Entity participantEntity = new Entity(KIND_PARTICIPANT, participant.getUsername());
    participantEntity.setProperty(PROPERTY_USERNAME, participant.getUsername());
    participantEntity.setProperty(
        PROPERTY_STARTTIMEAVAILABLE, participant.getStartTimeAvailable().format(formatter));
    participantEntity.setProperty(
        PROPERTY_ENDTIMEAVAILABLE, participant.getEndTimeAvailable().format(formatter));
    participantEntity.setProperty(PROPERTY_DURATION, participant.getDuration());
    participantEntity.setProperty(PROPERTY_CURRENTMATCHID, participant.getCurrentMatchId());
    participantEntity.setProperty(PROPERTY_TIMESTAMP, participant.getTimestamp());

    // Insert entity into datastore
    datastore.put(participantEntity);
  }

  /** Return Participant Entity from username, or null if entity is not found */
  @Nullable
  private Entity getEntityFromUsername(String username) {
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
    return getParticipantFromEntity(getEntityFromUsername(username));
  }

  /** Return participant object from datastore participant entity, or null if entity is null */
  @Nullable
  private static Participant getParticipantFromEntity(Entity entity) {
    if (entity == null) {
      return null;
    }

    // Get entity properties
    String username = (String) entity.getProperty(PROPERTY_USERNAME);
    ZonedDateTime startTimeAvailable =
        ZonedDateTime.parse((String) entity.getProperty(PROPERTY_STARTTIMEAVAILABLE), formatter);
    ZonedDateTime endTimeAvailable =
        ZonedDateTime.parse((String) entity.getProperty(PROPERTY_ENDTIMEAVAILABLE), formatter);
    int duration = ((Long) entity.getProperty(PROPERTY_DURATION)).intValue();
    long currentMatchId = (long) entity.getProperty(PROPERTY_CURRENTMATCHID);
    long timestamp = (long) entity.getProperty(PROPERTY_TIMESTAMP);

    // Create and return new Participant
    return new Participant(
        username, startTimeAvailable, endTimeAvailable, duration, currentMatchId, timestamp);
  }

  /** Return list of all unmatched participants with same duration */
  public List<Participant> getSameDurationParticipants(int duration) {
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

  /** Return String representation of participants for logging purposes */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Query query = new Query(KIND_PARTICIPANT);
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      Participant participant = getParticipantFromEntity(entity);
      sb.append(
          "username="
              + participant.getUsername()
              + ", endTimeAvailable="
              + participant.getEndTimeAvailable()
              + ", duration="
              + participant.getDuration());
    }
    return sb.toString();
  }

  /** Remove Participant from datastore */
  public void removeParticipant(Participant participant) {
    Key participantKey = KeyFactory.createKey(KIND_PARTICIPANT, participant.getUsername());
    datastore.delete(participantKey);
  }

  /** Return String representation of participants */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Query query = new Query(KIND_PARTICIPANT);
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      Participant participant = getParticipantFromEntity(entity);
      sb.append(
          "username="
              + participant.getUsername()
              + ", endTimeAvailable="
              + participant.getEndTimeAvailable()
              + ", duration="
              + participant.getDuration());
    }
    return sb.toString();
  }
}
