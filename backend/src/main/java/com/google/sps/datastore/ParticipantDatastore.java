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
  private DatastoreService datastore;

  /** Constructor */
  public ParticipantDatastore(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /** Add Participant to datastore */
  public void addParticipant(Participant participant) {
    // Set properties of entity
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

  /** Return Participant Entity from username */
  private Entity getEntityFromUsername(String username) {
    Key participantKey = KeyFactory.createKey(KIND_PARTICIPANT, username);
    try {
      return datastore.get(participantKey);
    } catch (EntityNotFoundException e) {
      // TODO: what error to send
      return null;
    }
  }

  /** Update Participant with new matchId, null out availability to show currently matched */
  public void updateNewMatch(String username, long matchId) {
    Entity participantEntity = getEntityFromUsername(username);

    participantEntity.setProperty(PROPERTY_CURRENTMATCHID, matchId);

    // Null out availibility fields if not already
    participantEntity.setProperty(PROPERTY_STARTTIMEAVAILABLE, null);
    participantEntity.setProperty(PROPERTY_ENDTIMEAVAILABLE, null);
    participantEntity.setProperty(PROPERTY_DURATION, 0);

    // Overwrite existing entity in datastore
    datastore.put(participantEntity);
  }

  /** Return Participant from username */
  @Nullable
  public Participant getParticipantFromUsername(String username) {
    return getParticipantFromEntity(getEntityFromUsername(username));
  }

  /** Return participant object from datastore participant entity */
  private Participant getParticipantFromEntity(Entity entity) {
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

  /** Return list of all unmatched participants */
  public List<Participant> getUnmatchedParticipants() {
    Query query = new Query(KIND_PARTICIPANT).addSort(PROPERTY_DURATION, SortDirection.DESCENDING);

    // Create filter to get all currently unmatched participants
    Filter unmatched = new FilterPredicate(PROPERTY_DURATION, FilterOperator.GREATER_THAN, 0);
    query.setFilter(unmatched);

    PreparedQuery results = datastore.prepare(query);

    // Convert entities to list of participants
    List<Participant> participants = new ArrayList<Participant>();
    for (Entity entity : results.asIterable()) {
      participants.add(getParticipantFromEntity(entity));
    }
    return participants;
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
