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

package com.google.sps;

import static org.junit.Assert.assertEquals;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class UserServiceFactoryTest {

  private final LocalServiceTestHelper userServiceHelper =
      new LocalServiceTestHelper(new LocalUserServiceTestConfig())
          .setEnvIsAdmin(true)
          .setEnvIsLoggedIn(true)
          .setEnvAuthDomain("google.com")
          .setEnvEmail("user@google.com");

  @Before
  public void setUp() {
    userServiceHelper.setUp();
  }

  @After
  public void tearDown() {
    userServiceHelper.tearDown();
  }

  @Test
  public void testGetUsername() {
    UserService userService = UserServiceFactory.getUserService();

    String email = userService.getCurrentUser().getEmail();
    String username = email.split("@")[0];

    assertEquals(email, "user@google.com");
    assertEquals(username, "user");
  }
}
