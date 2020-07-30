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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.sps.datastore.ParticipantDatastore;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public final class RemoveParticipantServletTest {

  @Mock UsernameService usernameService;

  @Mock HttpServletRequest request;

  @Mock HttpServletResponse response;

  private RemoveParticipantServletHelper helper;

  private static final String USER = "user";
  private static final String EXPECTED_RESPONSE = "Received remove request.";

  @Before
  public void setUp() throws IOException {
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    usernameService = mock(UsernameService.class);
  }

  @Test
  public void testDoPost_shouldRemoveParticipant() throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ParticipantDatastore participantDatastore = new ParticipantDatastore(datastore);

    helper = new RemoveParticipantServletHelper(participantDatastore);

    when(usernameService.getUsername()).thenReturn(USER);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    helper.doPostHelper(request, response);

    verify(participantDatastore, times(1)).removeParticipant(USER);
    // verify(participantDatastore, times(1)).removeParticipant(anyString());
    writer.flush();
    assertTrue(stringWriter.toString().contains(EXPECTED_RESPONSE));
  }
}
