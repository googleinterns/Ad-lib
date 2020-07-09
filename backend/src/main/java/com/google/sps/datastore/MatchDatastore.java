package com.google.sps.datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.data.Match;
import com.google.sps.data.Participant;
import java.util.ArrayList;
import java.util.List;

public final class MatchDatastore {

  /** Datastore */
  private DatastoreService datastore;

  // Datastore Key/Property constants
  private static final String KEY_MATCH = "Match";
  private static final String PROPERTY_FIRSTPARTICIPANT = "firstParticipant";
  private static final String PROPERTY_SECONDPARTICIPANT = "secondParticipant";
  private static final String PROPERTY_DURATION = "duration";
  private static final String PROPERTY_TIMESTAMP = "timestamp";

  public MatchDatastore(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /** Add Match to datastore */
  public void addMatch(Match match) {
    // Set properties of entity
    Entity matchEntity = new Entity(KEY_MATCH);
    matchEntity.setProperty(PROPERTY_FIRSTPARTICIPANT, match.getFirstParticipant());
    matchEntity.setProperty(PROPERTY_SECONDPARTICIPANT, match.getSecondParticipant());
    matchEntity.setProperty(PROPERTY_DURATION, match.getDuration());
    matchEntity.setProperty(PROPERTY_TIMESTAMP, match.getTimestamp());

    // Insert entity into datastore
    datastore.put(matchEntity);
  }

  /** Remove Match from datastore */
  public void removeMatch(Match match) {
    Key matchEntityKey = KeyFactory.createKey(KEY_MATCH, match.getId());
    datastore.delete(matchEntityKey);
  }

  /** Return list of all Matches */
  public List<Match> getAllMatches() {
    // Create and sort queries by time
    Query query = new Query(KEY_MATCH).addSort(PROPERTY_TIMESTAMP, SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    // Convert list of entities to list of matches
    List<Match> matches = new ArrayList<Match>();
    for (Entity entity : results.asIterable()) {
      long id = (long) entity.getKey().getId();
      Participant firstParticipant = (Participant) entity.getProperty(PROPERTY_FIRSTPARTICIPANT);
      Participant secondParticipant = (Participant) entity.getProperty(PROPERTY_SECONDPARTICIPANT);
      int duration = (int) entity.getProperty(PROPERTY_DURATION);
      long timestamp = (long) entity.getProperty(PROPERTY_TIMESTAMP);
      Match match = new Match(id, firstParticipant, secondParticipant, duration, timestamp);
      matches.add(match);
    }
    return matches;
  }
}
