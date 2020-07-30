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
import static org.mockito.Mockito.*;

import com.google.sps.datastore.ParticipantDatastore;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class RemoveParticipantServletTest {

  private RemoveParticipantServletHelper helper;

  @Test
  public void testDoPost_shouldRemoveParticipant() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    ParticipantDatastore participantDatastore = mock(ParticipantDatastore.class);
    helper = new RemoveParticipantServletHelper(participantDatastore);

    // when(removeParticipantServletHelper.getUsername()).thenReturn("user");

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    helper.doPostHelper(request, response);

    verify(participantDatastore, times(1)).removeParticipant("user");
    // verify(participantDatastore, times(1)).removeParticipant(anyString());
    writer.flush();
    assertTrue(stringWriter.toString().contains("Received remove request."));
  }
}
