// Copyright 2019 Google LLC
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

package com.google.sps;

import static com.google.common.truth.Truth.assertThat;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.MatchPreference;
import com.google.sps.data.User;
import com.google.sps.datastore.UserDatastore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class UserDatastoreTest {

  // Default values
  private static final long ID_DEFAULT = 0;
  private static final int DURATION_DEFAULT = 30;
  private static final String ROLE_DEFAULT = "Software engineer";
  private static final String PRODUCT_AREA_DEFAULT = "Ads";
  private static final MatchPreference MATCH_PREFERENCE_DEFAULT = MatchPreference.SIMILAR;

  // Some usernames
  private static final String PERSON_A = "Person A";
  private static final String PERSON_B = "Person B";

  // Datastore Key/Property constants
  private static final String KIND_USER = "User";
  private static final String PROPERTY_USERNAME = "username";
  private static final String PROPERTY_DURATION = "duration";
  private static final String PROPERTY_ROLE = "role";
  private static final String PROPERTY_PRODUCT_AREA = "productArea";
  private static final String PROPERTY_MATCH_PREFERENCE = "matchPreference";

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  // TODO(#37): Find/write JUnit rule to encapsulate setUp() and tearDown()
  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void addOneUser() throws EntityNotFoundException {
    // Add one user to datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserDatastore userDatastore = new UserDatastore(datastore);
    User user =
        new User(
            PERSON_A,
            DURATION_DEFAULT,
            ROLE_DEFAULT,
            PRODUCT_AREA_DEFAULT,
            MATCH_PREFERENCE_DEFAULT);

    userDatastore.addUser(user);

    Key key = KeyFactory.createKey(KIND_USER, PERSON_A);
    Entity entity = datastore.get(key);
    assertThat((String) entity.getProperty(PROPERTY_USERNAME)).isEqualTo(PERSON_A);
    assertThat(((Long) entity.getProperty(PROPERTY_DURATION)).intValue())
        .isEqualTo(DURATION_DEFAULT);
    assertThat((String) entity.getProperty(PROPERTY_ROLE)).isEqualTo(ROLE_DEFAULT);
    assertThat((String) entity.getProperty(PROPERTY_PRODUCT_AREA)).isEqualTo(PRODUCT_AREA_DEFAULT);
    assertThat(
            MatchPreference.forIntValue(
                ((Long) entity.getProperty(PROPERTY_MATCH_PREFERENCE)).intValue()))
        .isEqualTo(MATCH_PREFERENCE_DEFAULT);
  }

  @Test
  public void addTwoUsers() throws EntityNotFoundException {
    // Add two users to datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserDatastore userDatastore = new UserDatastore(datastore);
    User userA =
        new User(
            PERSON_A,
            DURATION_DEFAULT,
            ROLE_DEFAULT,
            PRODUCT_AREA_DEFAULT,
            MATCH_PREFERENCE_DEFAULT);
    User userB =
        new User(
            PERSON_B,
            DURATION_DEFAULT,
            ROLE_DEFAULT,
            PRODUCT_AREA_DEFAULT,
            MATCH_PREFERENCE_DEFAULT);

    userDatastore.addUser(userA);
    userDatastore.addUser(userB);

    Key keyA = KeyFactory.createKey(KIND_USER, PERSON_A);
    Key keyB = KeyFactory.createKey(KIND_USER, PERSON_B);
    Entity entityA = datastore.get(keyA);
    Entity entityB = datastore.get(keyB);
    assertThat((String) entityA.getProperty(PROPERTY_USERNAME)).isEqualTo(PERSON_A);
    assertThat(((Long) entityA.getProperty(PROPERTY_DURATION)).intValue())
        .isEqualTo(DURATION_DEFAULT);
    assertThat((String) entityA.getProperty(PROPERTY_ROLE)).isEqualTo(ROLE_DEFAULT);
    assertThat((String) entityA.getProperty(PROPERTY_PRODUCT_AREA)).isEqualTo(PRODUCT_AREA_DEFAULT);
    assertThat(
            MatchPreference.forIntValue(
                ((Long) entityA.getProperty(PROPERTY_MATCH_PREFERENCE)).intValue()))
        .isEqualTo(MATCH_PREFERENCE_DEFAULT);
    assertThat((String) entityB.getProperty(PROPERTY_USERNAME)).isEqualTo(PERSON_B);
    assertThat(((Long) entityB.getProperty(PROPERTY_DURATION)).intValue())
        .isEqualTo(DURATION_DEFAULT);
    assertThat((String) entityB.getProperty(PROPERTY_ROLE)).isEqualTo(ROLE_DEFAULT);
    assertThat((String) entityB.getProperty(PROPERTY_PRODUCT_AREA)).isEqualTo(PRODUCT_AREA_DEFAULT);
    assertThat(
            MatchPreference.forIntValue(
                ((Long) entityB.getProperty(PROPERTY_MATCH_PREFERENCE)).intValue()))
        .isEqualTo(MATCH_PREFERENCE_DEFAULT);
  }

  @Test
  public void addGetOneUser() {
    // Add one user to datastore, return user from username
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserDatastore userDatastore = new UserDatastore(datastore);
    User user =
        new User(
            PERSON_A,
            DURATION_DEFAULT,
            ROLE_DEFAULT,
            PRODUCT_AREA_DEFAULT,
            MATCH_PREFERENCE_DEFAULT);

    userDatastore.addUser(user);

    assertThat(userDatastore.getUserFromUsername(PERSON_A).getUsername()).isEqualTo(PERSON_A);
  }

  @Test
  public void getNonexistentUser() {
    // Try to get user from username that's not in datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserDatastore userDatastore = new UserDatastore(datastore);

    assertThat(userDatastore.getUserFromUsername(PERSON_A)).isNull();
  }
}
