package com.google.sps.datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.sps.data.User;
import javax.annotation.Nullable;

/** Separates datastore method calls involving User type from caller */
public final class UserDatastore {

  // Datastore Key/Property constants
  private static final String KIND_USER = "User";
  private static final String PROPERTY_USERNAME = "username";

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

    // Insert entity into datastore
    datastore.put(userEntity);
  }

  /** Return User Entity from username, or null if entity is not found */
  @Nullable
  private Entity getEntityFromUsername(String username) {
    Key userKey = KeyFactory.createKey(KIND_USER, username);
    try {
      return datastore.get(userKey);
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  /** Return User from username, or null if user with username not in datastore */
  @Nullable
  public User getUserFromUsername(String username) {
    return getUserFromEntity(getEntityFromUsername(username));
  }

  /** Return user object from datastore user entity, or null if entity is null */
  @Nullable
  private static User getUserFromEntity(Entity entity) {
    if (entity == null) {
      return null;
    }

    // Get entity properties
    String username = (String) entity.getProperty(PROPERTY_USERNAME);

    // Create and return new User
    return new User(id, username);
  }

  /** Return String representation of users for logging purposes */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Query query = new Query(KIND_USER);
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      User user = getUserFromEntity(entity);
      sb.append("username=" + user.getUsername());
    }
    return sb.toString();
  }
}
