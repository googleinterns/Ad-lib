package com.google.sps.datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.sps.data.User;
import javax.annotation.Nonnull;
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
  private Entity getEntity(String username) {
    Key userKey = KeyFactory.createKey(KIND_USER, username);
    try {
      return datastore.get(userKey);
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  /** Return user object from datastore user entity, or null if entity is null */
  @Nullable
  private static User getUserFromEntity(@Nonnull Entity entity) {
    // Get entity properties
    long id = (long) entity.getKey().getId();
    String username = (String) entity.getProperty(PROPERTY_USERNAME);

    // Create and return new User
    return new User(id, username);
  }

  /** Return User from username, or null if user with username not in datastore */
  @Nullable
  public User getUserFromUsername(String username) {
    Entity entity = getEntity(username);
    if (entity == null) {
      return null;
    }
    return getUserFromEntity(entity);
  }
}
