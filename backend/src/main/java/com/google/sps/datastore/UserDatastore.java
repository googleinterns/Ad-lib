// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.sps.data.MatchPreference;
import com.google.sps.data.User;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Separates datastore method calls involving User type from caller */
public final class UserDatastore {

  // Datastore Key/Property constants
  private static final String KIND_USER = "User";
  private static final String PROPERTY_USERNAME = "username";
  private static final String PROPERTY_DURATION = "duration";
  private static final String PROPERTY_ROLE = "role";
  private static final String PROPERTY_PRODUCT_AREA = "productArea";
  private static final String PROPERTY_INTERESTS = "interests";
  private static final String PROPERTY_MATCH_PREFERENCE = "matchPreference";

  /** Datastore */
  private final DatastoreService datastore;

  /** Constructor that takes in DatastoreService */
  public UserDatastore(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /** Return entity created from user */
  private Entity createEntityFromUser(User user) {
    // Set properties of entity based on user fields
    Entity entity = new Entity(KIND_USER, user.getUsername());
    entity.setProperty(PROPERTY_USERNAME, user.getUsername());
    entity.setProperty(PROPERTY_DURATION, user.getDuration());
    entity.setProperty(PROPERTY_ROLE, user.getRole());
    entity.setProperty(PROPERTY_PRODUCT_AREA, user.getProductArea());
    entity.setProperty(PROPERTY_INTERESTS, user.getInterests());
    entity.setProperty(PROPERTY_MATCH_PREFERENCE, user.getMatchPreference().getValue());

    return entity;
  }

  /** Put user in datastore. Overwrite user entity if user with same username already exists. */
  public void addUser(User user) {
    // Insert entity into datastore
    datastore.put(createEntityFromUser(user));
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
    return new User(
        (String) entity.getProperty(PROPERTY_USERNAME),
        ((Long) entity.getProperty(PROPERTY_DURATION)).intValue(),
        (String) entity.getProperty(PROPERTY_ROLE),
        (String) entity.getProperty(PROPERTY_PRODUCT_AREA),
        (String) entity.getProperty(PROPERTY_INTERESTS),
        MatchPreference.forIntValue(
            ((Long) entity.getProperty(PROPERTY_MATCH_PREFERENCE)).intValue()));
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
