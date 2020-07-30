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

import com.google.sps.datastore.ParticipantDatastore;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public final class RemoveParticipantHelperTest {

  @Mock UsernameService usernameService = mock(UsernameService.class);
  @Mock HttpServletRequest request = mock(HttpServletRequest.class);
  @Mock HttpServletResponse response = mock(HttpServletResponse.class);
  @Mock ParticipantDatastore participantDatastore = mock(ParticipantDatastore.class);

  private RemoveParticipantHelper helper =
      new RemoveParticipantHelper(participantDatastore, usernameService);
  private StringWriter stringWriter = new StringWriter();

  private static final String USER = "user";
  private static final String EXPECTED_RESPONSE = "Received remove request.";

  @Test
  public void testDoPost_shouldRemoveParticipant() throws IOException {
    when(usernameService.getUsername()).thenReturn(USER);
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    helper.doPost(request, response);

    writer.flush();
    verify(participantDatastore, times(1)).removeParticipant(USER);
    assertTrue(stringWriter.toString().contains(EXPECTED_RESPONSE));
  }
}
