package com.google.sps.datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.sps.data.Match;

public final class MatchDatastore {

  /** Datastore */
  private DatastoreService datastore;

  // Datastore Key/Property constants
  private static final String KEY_MATCH = "Match";
  private static final String PROPERTY_FIRSTPARTICIPANTID = "firstParticipantId";
  private static final String PROPERTY_SECONDPARTICIPANTID = "secondParticipantId";
  private static final String PROPERTY_DURATION = "duration";
  private static final String PROPERTY_TIMESTAMP = "timestamp";

  public MatchDatastore(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /** Add Match to datastore and return the match Id */
  public long addMatch(Match match) {
    // Set properties of entity
    Entity matchEntity = new Entity(KEY_MATCH);
    matchEntity.setProperty(PROPERTY_FIRSTPARTICIPANTID, match.getFirstParticipantId());
    matchEntity.setProperty(PROPERTY_SECONDPARTICIPANTID, match.getSecondParticipantId());
    matchEntity.setProperty(PROPERTY_DURATION, match.getDuration());
    matchEntity.setProperty(PROPERTY_TIMESTAMP, match.getTimestamp());

    // Insert entity into datastore
    datastore.put(matchEntity);

    // Return matchId
    return matchEntity.getKey().getId();
  }

  /** Return match based on match datastore key ID */
  public Match getMatchFromId(long matchId) {
    // Never found a match
    if (matchId == 0) {
      return null;
    }

    try {
      // Match has been found before
      Key matchEntityKey = KeyFactory.createKey(KEY_MATCH, matchId);

      Entity matchEntity = datastore.get(matchEntityKey);
      long id = (long) matchEntity.getKey().getId();
      long firstParticipantId = (long) matchEntity.getProperty(PROPERTY_FIRSTPARTICIPANTID);
      long secondParticipantId = (long) matchEntity.getProperty(PROPERTY_SECONDPARTICIPANTID);
      int duration = ((Long) matchEntity.getProperty(PROPERTY_DURATION)).intValue();
      long timestamp = (long) matchEntity.getProperty(PROPERTY_TIMESTAMP);

      return new Match(id, firstParticipantId, secondParticipantId, duration, timestamp);
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  /** Remove Match from datastore */
  public void removeMatch(Match match) {
    Key matchEntityKey = KeyFactory.createKey(KEY_MATCH, match.getId());
    datastore.delete(matchEntityKey);
  }
}
