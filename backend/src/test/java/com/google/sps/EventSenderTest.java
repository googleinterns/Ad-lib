package test.java.com.google.sps;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.sps.data.Participant;
import com.google.sps.eventsender.EventSender;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class EventSenderTest {

  private int testConferenceVersion = 1;
  private String testName = "Test Name";
  private String testEmail = "Test Email";
  private List<EventAttendee> testAtt = Collections.singletonList(createTestAttendee());
  private String testSummary = " Test Summary";
  private String testDescription = " Test Description";
  private Event testEvent =
      new Event().setSummary(testSummary).setDescription(testDescription).setAttendees(testAtt);

  private com.google.sps.eventsender.EventSender eventSender;
  @Mock private Event event;
  @Mock private Calendar calendar;
  @Mock private Calendar.Events events;
  @Mock private Calendar.Events.Insert insert;

  @Mock private Participant mockParticipant1;
  @Mock private Participant mockParticipant2;
  @Mock private DateTime mockDateTime1;
  @Mock private DateTime mockDateTime2;

  private EventAttendee createTestAttendee() {
    return new EventAttendee().setEmail(testEmail).setDisplayName(testName);
  }

  @Before
  public void setUp() throws IOException {

    mockDateTime1 = mock(DateTime.class);
    mockDateTime2 = mock(DateTime.class);
    mockParticipant1 = mock(Participant.class);
    mockParticipant2 = mock(Participant.class);

    event = mock(Event.class);
    when(event.setSummary(testSummary)).thenReturn(event);
    when(event.getSummary()).thenReturn(testSummary);

    when(event.setAttendees(testAtt)).thenReturn(event);
    when(event.getAttendees()).thenReturn(testAtt);

    when(event.setDescription(testDescription)).thenReturn(event);
    when(event.getDescription()).thenReturn(testDescription);

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
    when(insert.getConferenceDataVersion()).thenReturn(testConferenceVersion);
    when(insert.execute()).thenReturn(event);

    eventSender = new EventSender(calendar);
  }

  @Test
  public void SetIncorrectAndHasIncorrect() throws IOException, GeneralSecurityException {
    ArgumentCaptor<Event> argument = ArgumentCaptor.forClass(Event.class);

    eventSender.addEventToCalendar(testEvent);
    verify(events).insert(eq(testEmail), argument.capture());

    Event event = argument.getValue();

    assertThat(event.getSummary()).isNotEqualTo("testString");
  }

  @Test
  public void setCorrectAndHasCorrect() throws IOException, GeneralSecurityException {
    ArgumentCaptor<Event> argument = ArgumentCaptor.forClass(Event.class);

    eventSender.addEventToCalendar(testEvent);
    verify(events).insert(eq(testEmail), argument.capture());

    Event event = argument.getValue();

    assertThat(event.getSummary()).isEqualTo(testSummary);
  }

  @Test
  public void testConferenceVersion() throws IOException, GeneralSecurityException {

    ArgumentCaptor<Integer> argument = ArgumentCaptor.forClass(Integer.class);

    eventSender.addEventToCalendar(testEvent);

    verify(insert).setConferenceDataVersion(argument.capture());
    int conferenceVersion = argument.getValue();
    assertThat(conferenceVersion).isEqualTo(insert.getConferenceDataVersion());
  }
}
