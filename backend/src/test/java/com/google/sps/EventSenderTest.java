package test.java.com.google.sps;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.common.truth.Truth;
import com.google.sps.eventsender.EventSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(JUnit4.class)
public class EventSenderTest {

  private int testConferenceVersion = 1;
  private String testName = "Test Name";
  private String testEmail = "Test Email";
  private String testSummary = " Test Summary";
  private String testDescription = " Test Description";

  private List<EventAttendee> testAtt = Collections.singletonList(createTestAttendee());
  private com.google.sps.eventsender.EventSender eventSender;
  private Event event;
  @Mock private Event mockEvent;
  @Mock private Calendar calendar;
  @Mock private Calendar.Events events;
  @Mock private Calendar.Events.Insert insert;

  private EventAttendee createTestAttendee() {
    return new EventAttendee().setEmail(testEmail).setDisplayName(testName);
  }

  @Before
  public void setUp() throws IOException {

    mockEvent = mock(Event.class);
    when(mockEvent.setSummary(testSummary)).thenReturn(mockEvent);
    when(mockEvent.getSummary()).thenReturn(testSummary);

    when(mockEvent.setAttendees(testAtt)).thenReturn(mockEvent);
    when(mockEvent.getAttendees()).thenReturn(testAtt);

    when(mockEvent.setDescription(testDescription)).thenReturn(mockEvent);
    when(mockEvent.getDescription()).thenReturn(testDescription);

    calendar = mock(Calendar.class);
    // Mock to return list of users
    events = mock(Calendar.Events.class);
    // Mock to return list of messages.
    insert = mock(Calendar.Events.Insert.class);
    // Emulating real behavior due to when method, will return list of users.
    when(calendar.events()).thenReturn(events);
    // Emulating real behaviour due to when method, will return list of messages
    when(events.insert(any(), any())).thenReturn(insert);
    //    Emulating sending messages, when sent will return send mock.
    when(insert.setConferenceDataVersion(testConferenceVersion)).thenReturn(insert);
    when(insert.execute()).thenReturn(event);

    eventSender = new EventSender(calendar);
  }

  @Test
  public void eventIsCreated() {
    ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

    eventSender.createEvent(any(), any(), any(), any(), any());

    verify(mockEvent.setSummary(argument.capture()));

    String summary = argument.getValue();
    Truth.assertThat(summary).isEqualTo(testSummary);

  }
}
