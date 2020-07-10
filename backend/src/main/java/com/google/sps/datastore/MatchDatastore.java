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
  private static final String KIND_MATCH = "Match";
  private static final String PROPERTY_FIRSTPARTICIPANTKEY = "firstParticipantKey";
  private static final String PROPERTY_SECONDPARTICIPANTKEY = "secondParticipantKey";
  private static final String PROPERTY_DURATION = "duration";
  private static final String PROPERTY_TIMESTAMP = "timestamp";

  /** Constructor */
  public MatchDatastore(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /** Add Match to datastore and return the match Key */
  public Key addMatch(Match match) {
    // Set properties of entity
    Entity matchEntity = new Entity(KIND_MATCH);
    matchEntity.setProperty(PROPERTY_FIRSTPARTICIPANTKEY, match.getFirstParticipantKey());
    matchEntity.setProperty(PROPERTY_SECONDPARTICIPANTKEY, match.getSecondParticipantKey());
    matchEntity.setProperty(PROPERTY_DURATION, match.getDuration());
    matchEntity.setProperty(PROPERTY_TIMESTAMP, match.getTimestamp());

    // Insert entity into datastore
    datastore.put(matchEntity);

    // Return matchKey
    return matchEntity.getKey();
  }

  /** Return match based on match datastore key */
  public Match getMatchFromKey(Key matchKey) {
    // No match
    if (matchKey == null) {
      return null;
    }

    try {
      // Match exists
      Entity matchEntity = datastore.get(matchKey);
      long id = (long) matchEntity.getKey().getId();
      Key firstParticipantKey = (Key) matchEntity.getProperty(PROPERTY_FIRSTPARTICIPANTKEY);
      Key secondParticipantKey = (Key) matchEntity.getProperty(PROPERTY_SECONDPARTICIPANTKEY);
      int duration = ((Long) matchEntity.getProperty(PROPERTY_DURATION)).intValue();
      long timestamp = (long) matchEntity.getProperty(PROPERTY_TIMESTAMP);

      return new Match(id, firstParticipantKey, secondParticipantKey, duration, timestamp);
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  /** Remove Match from datastore */
  public void removeMatch(Match match) {
    Key matchEntityKey = KeyFactory.createKey(KIND_MATCH, match.getId());
    datastore.delete(matchEntityKey);
  }
}
