package com.google.sps.datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.sps.data.Match;
import javax.annotation.Nullable;

public final class MatchDatastore {

  // Datastore Key/Property constants
  private static final String KIND_MATCH = "Match";
  private static final String PROPERTY_FIRSTPARTICIPANTUSERNAME = "firstParticipantUsername";
  private static final String PROPERTY_SECONDPARTICIPANTUSERNAME = "secondParticipantUsername";
  private static final String PROPERTY_DURATION = "duration";
  private static final String PROPERTY_TIMESTAMP = "timestamp";

  /** Datastore */
  private final DatastoreService datastore;

  /** Constructor */
  public MatchDatastore(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /** Add Match to datastore and return the match Key */
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

    // Return matchKey
    return matchEntity.getKey().getId();
  }

  /** Return match based on match datastore key id */
  @Nullable
  public Match getMatchFromId(long matchId) {
    Key matchKey = KeyFactory.createKey(KIND_MATCH, matchId);
    try {
      return getMatchFromEntity(datastore.get(matchKey));
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  /** Return Match from Entity */
  private Match getMatchFromEntity(Entity matchEntity) {
    long id = (long) matchEntity.getKey().getId();
    String firstParticipantUsername =
        (String) matchEntity.getProperty(PROPERTY_FIRSTPARTICIPANTUSERNAME);
    String secondParticipantUsername =
        (String) matchEntity.getProperty(PROPERTY_SECONDPARTICIPANTUSERNAME);
    int duration = ((Long) matchEntity.getProperty(PROPERTY_DURATION)).intValue();
    long timestamp = (long) matchEntity.getProperty(PROPERTY_TIMESTAMP);

    return new Match(id, firstParticipantUsername, secondParticipantUsername, duration, timestamp);
  }

  /** Remove Match from datastore */
  public void removeMatch(long matchId) {
    Key matchKey = KeyFactory.createKey(KIND_MATCH, matchId);
    datastore.delete(matchKey);
  }

  /** Return String representation of matches */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Query query = new Query(KIND_MATCH);
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      Match match = getMatchFromEntity(entity);
      sb.append(
          "1. "
              + match.getFirstParticipantUsername()
              + ", 2. "
              + match.getSecondParticipantUsername()
              + ", duration="
              + match.getDuration());
    }
    return sb.toString();
  }
}
