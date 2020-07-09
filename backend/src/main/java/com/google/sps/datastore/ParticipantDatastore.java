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

public final class ParticipantDatastore {

  /** Datastore */
  private DatastoreService datastore;

  // Datastore Key/Property constants
  private static final String KEY_PARTICIPANT = "Participant";
  private static final String PROPERTY_USERNAME = "username";
  private static final String PROPERTY_STARTTIMEAVAILABLE = "startTimeAvailable";
  private static final String PROPERTY_ENDTIMEAVAILABLE = "endTimeAvailable";
  private static final String PROPERTY_DURATION = "duration";
  private static final String PROPERTY_CURRENTMATCHID = "currentMatchId";
  private static final String PROPERTY_TIMESTAMP = "timestamp";

  private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;

  /** Maximum difference in duration to be compatible */
  private static int MAX_DURATION_DIFF = 15;

  public ParticipantDatastore(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /** Add Participant to datastore */
  public void addParticipant(Participant participant) {
    // Set properties of entity
    Entity participantEntity = new Entity(KEY_PARTICIPANT);
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

  /** Remove Participant from datastore */
  public void removeParticipant(Participant participant) {
    Key participantEntityKey = KeyFactory.createKey(KEY_PARTICIPANT, participant.getId());
    datastore.delete(participantEntityKey);
  }

  /** Update Participant with new matchId, null out availability to show currently matched */
  public void updateNewMatch(long participantId, long matchId) {
    Key participantEntityKey = KeyFactory.createKey(KEY_PARTICIPANT, participantId);
    try {
      Entity participantEntity = datastore.get(participantEntityKey);

      participantEntity.setProperty(PROPERTY_CURRENTMATCHID, matchId);

      // Null out availibility fields
      participantEntity.setProperty(PROPERTY_STARTTIMEAVAILABLE, "");
      participantEntity.setProperty(PROPERTY_ENDTIMEAVAILABLE, "");
      participantEntity.setProperty(PROPERTY_DURATION, 0);

      // Overwrite existing entity in datastore
      datastore.put(participantEntity);
    } catch (EntityNotFoundException e) {
      return;
    }
  }

  /** Return Participant from username */
  public Participant getParticipantFromUsername(String username) {
    Query query = new Query(KEY_PARTICIPANT);

    // Create filter to get participant with username
    Filter thisParticipant = new FilterPredicate(PROPERTY_USERNAME, FilterOperator.EQUAL, username);
    query.setFilter(thisParticipant);

    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1));
    return getParticipantFromEntity(results.get(0));
  }

  /** Return Participant from unique key ID */
  public Participant getParticipantFromId(long participantId) {
    Key participantEntityKey = KeyFactory.createKey(KEY_PARTICIPANT, participantId);
    try {
      Entity participantEntity = datastore.get(participantEntityKey);
      return getParticipantFromEntity(participantEntity);
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  /** Return participant object from datastore participant entity */
  private Participant getParticipantFromEntity(Entity participantEntity) {
    // Get entity properties
    long id = (long) participantEntity.getKey().getId();

    String username = (String) participantEntity.getProperty(PROPERTY_USERNAME);
    System.out.println(username);

    String startTimeAvailableString =
        (String) participantEntity.getProperty(PROPERTY_STARTTIMEAVAILABLE);
    ZonedDateTime startTimeAvailable = ZonedDateTime.parse(startTimeAvailableString, formatter);

    String endTimeAvailableString =
        (String) participantEntity.getProperty(PROPERTY_ENDTIMEAVAILABLE);
    ZonedDateTime endTimeAvailable = ZonedDateTime.parse(endTimeAvailableString, formatter);

    int duration = ((Long) participantEntity.getProperty(PROPERTY_DURATION)).intValue();

    long currentMatchId = (long) participantEntity.getProperty(PROPERTY_CURRENTMATCHID);

    long timestamp = (long) participantEntity.getProperty(PROPERTY_TIMESTAMP);

    // Create and return new Participant
    return new Participant(
        id, username, startTimeAvailable, endTimeAvailable, duration, currentMatchId, timestamp);
  }

  /** Return list of all unmatched participants with similar duration */
  public List<Participant> getUnmatchedParticipants(int newDuration) {
    Query query =
        new Query(KEY_PARTICIPANT)
            .addSort(PROPERTY_DURATION, SortDirection.ASCENDING)
            .addSort(PROPERTY_TIMESTAMP, SortDirection.DESCENDING);

    // Create filter to get all currently unmatched participants
    Filter unmatched = new FilterPredicate(PROPERTY_DURATION, FilterOperator.GREATER_THAN, 0);
    query.setFilter(unmatched);

    // Filter by duration compatibility
    Filter durationLower =
        new FilterPredicate(
            PROPERTY_DURATION,
            FilterOperator.GREATER_THAN_OR_EQUAL,
            newDuration - MAX_DURATION_DIFF);
    Filter durationHigher =
        new FilterPredicate(
            PROPERTY_DURATION, FilterOperator.LESS_THAN_OR_EQUAL, newDuration + MAX_DURATION_DIFF);
    query.setFilter(durationLower).setFilter(durationHigher);

    PreparedQuery results = datastore.prepare(query);

    // Convert list of entities to list of participants
    List<Participant> participants = new ArrayList<Participant>();
    for (Entity entity : results.asIterable()) {
      participants.add(getParticipantFromEntity(entity));
    }
    return participants;
  }
}
