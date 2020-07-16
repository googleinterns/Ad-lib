package com.google.sps.datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.sps.data.Match;
import javax.annotation.Nullable;

/** Separates datastore method calls involving Match type from caller */
public final class MatchDatastore {

  // Datastore Key/Property constants
  private static final String KIND_MATCH = "Match";
  private static final String PROPERTY_FIRSTPARTICIPANTUSERNAME = "firstParticipantUsername";
  private static final String PROPERTY_SECONDPARTICIPANTUSERNAME = "secondParticipantUsername";
  private static final String PROPERTY_DURATION = "duration";
  private static final String PROPERTY_TIMESTAMP = "timestamp";

  /** Datastore */
  private final DatastoreService datastore;

  /** Constructor that takes in DatastoreService */
  public MatchDatastore(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /** Put Match in datastore and return the match key id */
  public long addMatch(Match match) {
    // Set properties of entity
    Entity matchEntity = new Entity(KIND_MATCH);
    matchEntity.setProperty(PROPERTY_FIRSTPARTICIPANTUSERNAME, match.getFirstParticipantUsername());
    matchEntity.setProperty(
        PROPERTY_SECONDPARTICIPANTUSERNAME, match.getSecondParticipantUsername());
    matchEntity.setProperty(PROPERTY_DURATION, match.getDuration());
    matchEntity.setProperty(PROPERTY_TIMESTAMP, match.getTimestamp());

    // Insert entity into datastore
    datastore.put(matchEntity);

    // Return match key id
    return matchEntity.getKey().getId();
  }

  /** Return Match from entity, or null if entity is null */
  private static Match getMatchFromEntity(Entity entity) {
    if (entity == null) {
      return null;
    }

    String firstParticipantUsername =
        (String) entity.getProperty(PROPERTY_FIRSTPARTICIPANTUSERNAME);
    String secondParticipantUsername =
        (String) entity.getProperty(PROPERTY_SECONDPARTICIPANTUSERNAME);
    int duration = ((Long) entity.getProperty(PROPERTY_DURATION)).intValue();
    long timestamp = (long) entity.getProperty(PROPERTY_TIMESTAMP);

    return new Match(firstParticipantUsername, secondParticipantUsername, duration, timestamp);
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
