package com.google.sps.datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
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
  private static final String PROPERTY_TIMESTAMP = "timestamp";

  private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;

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
    participantEntity.setProperty(PROPERTY_TIMESTAMP, participant.getTimestamp());

    // Insert entity into datastore
    datastore.put(participantEntity);
  }

  /** Remove Participant from datastore */
  public void removeParticipant(Participant participant) {
    Key participantEntityKey = KeyFactory.createKey(KEY_PARTICIPANT, participant.getId());
    datastore.delete(participantEntityKey);
  }

  /** Return list of all Participants */
  public List<Participant> getAllParticipants() {
    // Create and sort queries by time
    Query query = new Query(KEY_PARTICIPANT).addSort(PROPERTY_TIMESTAMP, SortDirection.DESCENDING);

    PreparedQuery results = datastore.prepare(query);

    // Convert list of entities to list of participants
    List<Participant> participants = new ArrayList<Participant>();
    for (Entity entity : results.asIterable()) {
      long id = (long) entity.getKey().getId();

      String username = (String) entity.getProperty(PROPERTY_USERNAME);

      String startTimeAvailableString = (String) entity.getProperty(PROPERTY_STARTTIMEAVAILABLE);
      ZonedDateTime startTimeAvailable = ZonedDateTime.parse(startTimeAvailableString, formatter);

      String endTimeAvailableString = (String) entity.getProperty(PROPERTY_ENDTIMEAVAILABLE);
      ZonedDateTime endTimeAvailable = ZonedDateTime.parse(endTimeAvailableString, formatter);

      int duration = ((Long) entity.getProperty(PROPERTY_DURATION)).intValue();

      long timestamp = (long) entity.getProperty(PROPERTY_TIMESTAMP);

      Participant currParticipant =
          new Participant(id, username, startTimeAvailable, endTimeAvailable, duration, timestamp);
      participants.add(currParticipant);
    }
    return participants;
  }
}
