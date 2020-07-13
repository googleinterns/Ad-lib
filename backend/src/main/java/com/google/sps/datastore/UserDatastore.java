package com.google.sps.datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.sps.data.User;

/** Separates datastore method calls involving User type from caller */
public final class UserDatastore {

  // Datastore Key/Property constants
  private static final String KIND_USER = "User";
  private static final String PROPERTY_USERNAME = "username";
  private static final String PROPERTY_TIMESTAMP = "timestamp";

  /** Formatter for converting between String and ZonedDateTime */
  private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;

  /** Datastore */
  private final DatastoreService datastore;

  /** Constructor that takes in DatastoreService */
  public UserDatastore(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /** Put user in datastore. Overwrite user entity if user with same username already exists. */
  public void addUser(User user) {
    // Set properties of entity based on user fields
    Entity userEntity = new Entity(KIND_USER, user.getUsername());
    userEntity.setProperty(PROPERTY_USERNAME, user.getUsername());
    userEntity.setProperty(PROPERTY_TIMESTAMP, user.getTimestamp());

    // Insert entity into datastore
    datastore.put(userEntity);
  }

  /** Return String representation of participants for logging purposes */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Query query = new Query(KIND_USER);
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      User user = getParticipantFromEntity(entity);
      sb.append("username=" + user.getUsername());
    }
    return sb.toString();
  }
}
