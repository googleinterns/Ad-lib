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

  // Some usernames
  private static final String PERSON_A = "Person A";
  private static final String PERSON_B = "Person B";

  // Datastore Key/Property constants
  private static final String KIND_USER = "User";
  private static final String PROPERTY_USERNAME = "username";

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
    User userA = new User(ID_DEFAULT, PERSON_A);

    userDatastore.addUser(userA);

    Key keyA = KeyFactory.createKey(KIND_USER, PERSON_A);
    Entity entityA = datastore.get(keyA);
    assertThat((String) entityA.getProperty(PROPERTY_USERNAME)).isEqualTo(PERSON_A);
  }

  @Test
  public void addTwoUsers() throws EntityNotFoundException {
    // Add two users to datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserDatastore userDatastore = new UserDatastore(datastore);
    User userA = new User(ID_DEFAULT, PERSON_A);
    User userB = new User(ID_DEFAULT, PERSON_B);

    userDatastore.addUser(userA);
    userDatastore.addUser(userB);

    Key keyA = KeyFactory.createKey(KIND_USER, PERSON_A);
    Key keyB = KeyFactory.createKey(KIND_USER, PERSON_B);
    Entity entityA = datastore.get(keyA);
    Entity entityB = datastore.get(keyB);
    assertThat((String) entityA.getProperty(PROPERTY_USERNAME)).isEqualTo(PERSON_A);
    assertThat((String) entityB.getProperty(PROPERTY_USERNAME)).isEqualTo(PERSON_B);
  }

  @Test
  public void addGetTwoUsers() {
    // Add two users to datastore, return user from username
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserDatastore userDatastore = new UserDatastore(datastore);
    User userA = new User(ID_DEFAULT, PERSON_A);
    User userB = new User(ID_DEFAULT, PERSON_B);

    userDatastore.addUser(userA);
    userDatastore.addUser(userB);

    assertThat(userDatastore.getUserFromUsername(PERSON_A).getUsername()).isEqualTo(PERSON_A);
    assertThat(userDatastore.getUserFromUsername(PERSON_B).getUsername()).isEqualTo(PERSON_B);
  }

  @Test
  public void getNonexistentUser() {
    // Try to get user from username that's not in datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserDatastore userDatastore = new UserDatastore(datastore);

    assertThat(userDatastore.getUserFromUsername(PERSON_A)).isNull();
  }
}
