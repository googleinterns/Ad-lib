package com.google.sps.datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
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
  private static final String PROPERTY_CURRENTMATCHKEY = "currentMatchKey";
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
    Entity participantEntity = new Entity(KIND_PARTICIPANT);
    participantEntity.setProperty(PROPERTY_USERNAME, participant.getUsername());
    participantEntity.setProperty(
        PROPERTY_STARTTIMEAVAILABLE, participant.getStartTimeAvailable().format(formatter));
    participantEntity.setProperty(
        PROPERTY_ENDTIMEAVAILABLE, participant.getEndTimeAvailable().format(formatter));
    participantEntity.setProperty(PROPERTY_DURATION, participant.getDuration());
    participantEntity.setProperty(PROPERTY_CURRENTMATCHKEY, participant.getCurrentMatchKey());
    participantEntity.setProperty(PROPERTY_TIMESTAMP, participant.getTimestamp());

    // Insert entity into datastore
    datastore.put(participantEntity);
  }

  /** Update Participant with new matchKey, null out availability to show currently matched */
  public void updateNewMatch(Key participantKey, Key matchKey) {
    try {
      Entity participantEntity = datastore.get(participantKey);

      participantEntity.setProperty(PROPERTY_CURRENTMATCHKEY, matchKey);

      // Null out availibility fields if not already
      participantEntity.setProperty(PROPERTY_STARTTIMEAVAILABLE, null);
      participantEntity.setProperty(PROPERTY_ENDTIMEAVAILABLE, null);
      participantEntity.setProperty(PROPERTY_DURATION, 0);

      // Overwrite existing entity in datastore
      datastore.put(participantEntity);
    } catch (EntityNotFoundException e) {
      // TODO: what error to send
      return;
    }
  }

  /** Return Participant from username */
  public Participant getParticipantFromUsername(String username) {
    Query query = new Query(KIND_PARTICIPANT);

    // Create filter to get participant with username
    Filter thisParticipant = new FilterPredicate(PROPERTY_USERNAME, FilterOperator.EQUAL, username);
    query.setFilter(thisParticipant);

    // Return Participant with matching username from list of size 1
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1));
    return getParticipantFromEntity(results.get(0));
  }

  /** Return Key of Participant from unique key ID */
  public Key getKeyFromId(long participantId) {
    return KeyFactory.createKey(KIND_PARTICIPANT, participantId);
  }

  /** Return participant object from datastore participant entity */
  private Participant getParticipantFromEntity(Entity entity) {
    // Get entity properties
    long id = (long) entity.getKey().getId();

    String username = (String) entity.getProperty(PROPERTY_USERNAME);

    String startTimeAvailableString = (String) entity.getProperty(PROPERTY_STARTTIMEAVAILABLE);
    ZonedDateTime startTimeAvailable = ZonedDateTime.parse(startTimeAvailableString, formatter);

    String endTimeAvailableString = (String) entity.getProperty(PROPERTY_ENDTIMEAVAILABLE);
    ZonedDateTime endTimeAvailable = ZonedDateTime.parse(endTimeAvailableString, formatter);

    int duration = ((Long) entity.getProperty(PROPERTY_DURATION)).intValue();

    Key currentMatchKey = (Key) entity.getProperty(PROPERTY_CURRENTMATCHKEY);

    long timestamp = (long) entity.getProperty(PROPERTY_TIMESTAMP);

    // Create and return new Participant
    return new Participant(
        id, username, startTimeAvailable, endTimeAvailable, duration, currentMatchKey, timestamp);
  }

  /** Return Participant from unique Key */
  @Nullable
  public Participant getParticipantFromKey(Key participantKey) {
    try {
      Entity participantEntity = datastore.get(participantKey);
      return getParticipantFromEntity(participantEntity);
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  /** Return Participant from unique key ID */
  public Participant getParticipantFromId(long participantId) {
    Key participantKey = getKeyFromId(participantId);
    return getParticipantFromKey(participantKey);
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
    Key participantKey = KeyFactory.createKey(KIND_PARTICIPANT, participant.getId());
    datastore.delete(participantKey);
  }
}
