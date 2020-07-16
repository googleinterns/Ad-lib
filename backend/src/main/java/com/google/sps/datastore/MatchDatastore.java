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

  /** Return entity of match */
  private Entity getEntityFromMatch(Match match) {
    // Set properties of entity
    Entity entity = new Entity(KIND_MATCH);
    entity.setProperty(PROPERTY_FIRSTPARTICIPANTUSERNAME, match.getFirstParticipantUsername());
    entity.setProperty(PROPERTY_SECONDPARTICIPANTUSERNAME, match.getSecondParticipantUsername());
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
        (String) entity.getProperty(PROPERTY_FIRSTPARTICIPANTUSERNAME),
        (String) entity.getProperty(PROPERTY_SECONDPARTICIPANTUSERNAME),
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
