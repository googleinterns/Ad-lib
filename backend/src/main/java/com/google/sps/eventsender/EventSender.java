package com.google.sps.eventsender;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Instance of the class responsible for sending and adding events to user calendars */
public class EventSender {

  private static final String APPLICATION_NAME = "Ad-Lib";
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static final String TOKENS_DIRECTORY_PATH = "tokens";

  /**
   * Global instance of the scopes required by this quickstart. If modifying these scopes, delete
   * your previously saved tokens/ folder.
   */
  private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

  /**
   * Creates an authorized Credential object.
   *
   * @param HTTP_TRANSPORT The network HTTP Transport.
   * @return An authorized Credential object.
   * @throws IOException If the credentials.json file cannot be found.
   */
  private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
      throws IOException {
    // Load client secrets.
    InputStream in = EventSender.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    if (in == null) {
      throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
    }
    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("me");
  }

  /**
   * TODO(): Convert from zonedTimeZone to DateTime
   *
   * @param meetingStartTime Time in which the meeting is designated to start at in the format
   *     "yyyy-mm-dd"
   * @param timezone String representing the Users Time Zone formatted as an IANA Time Zone Database
   *     name, e.g. "Europe/Zurich".
   * @return An EventDateTime with the input meeting time and input time zone.
   */
  public EventDateTime setMeetingStartTime(DateTime meetingStartTime, String timezone) {
    return new EventDateTime().setDateTime(meetingStartTime).setTimeZone(timezone);
  }

  /**
   * TODO(): Convert from zonedTimeZone to DateTime
   *
   * @param meetingEndTime Time in which the meeting is designated to end at in the format
   *     "yyyy-mm-dd"
   * @param timezone String representing the Users Time Zone formatted as an IANA Time Zone Database
   *     name, e.g. "Europe/Zurich".
   * @return An EventDateTime with the input meeting time and input time zone.
   */
  public EventDateTime setMeetingEndTime(DateTime meetingEndTime, String timezone) {
    return new EventDateTime().setDateTime(meetingEndTime).setTimeZone(timezone);
  }

  /**
   * @param participant1 Instance of the participant class representing an user in a successful
   *     match
   * @param participant2 Instance of a participant class representing a user in a successful match
   * @return An array of EventAttendees with both of the participants emails pre set.
   */
  public EventAttendee[] createParticipantsArray(
      com.google.sps.data.Participant participant1, com.google.sps.data.Participant participant2) {
    return new EventAttendee[] {
      new EventAttendee().setEmail(participant1.getUsername() + "@google.com"),
      new EventAttendee().setEmail(participant2.getUsername() + "@google.com"),
    };
  }

  /**
   * Creates a notification trigger within the event so that the user will receive an email reminder
   * in 5 minutes as wel as a push notification to their screen in 1 minute
   *
   * @return Returns these options of notifications to be passed into the event as parameters.
   */
  public Event.Reminders setReminderNotifications() {
    EventReminder[] eventReminders = {
      new EventReminder().setMethod("email").setMinutes(5),
      new EventReminder().setMethod("popup").setMinutes(1)
    };
    return new Event.Reminders().setUseDefault(false).setOverrides(Arrays.asList(eventReminders));
  }

  /**
   * TODO(): Currently assumes that the users will be within the same timezone. Main method of this
   * API, creates an Ad-lib event ideally the moment that a match is found. Upon receiving the two
   * participants as well as the meetingStart and meetingEndTime as well as their time zone
   * configures the event as well as ads hangout link.
   *
   * @param participant1 First Participant that has been matched to someone
   * @param participant2 Second Participant that has been matched to someone
   * @param meetingStartTime Time in which the meeting is designated to start at in the format
   *     "yyyy-mm-dd" *
   * @param meetingEndTime Time in which the meeting is designated to end at in the format *
   *     "yyyy-mm-dd"
   * @param timezone Timezone that they plan to meet in.
   * @return
   */
  public Event createAdLibEvent(
      com.google.sps.data.Participant participant1,
      com.google.sps.data.Participant participant2,
      DateTime meetingStartTime,
      DateTime meetingEndTime,
      String timezone) {
    return new Event()
        .setSummary("Your Ad-lib Session")
        .setDescription("A chance to spontaneously chat with a fellow Googler.")
        .setStart(setMeetingStartTime(meetingStartTime, timezone))
        .setEnd(setMeetingEndTime(meetingEndTime, timezone))
        .setReminders(setReminderNotifications())
        .setConferenceData(createAdLibVideoMeeting())
        .setAttendees(Arrays.asList(createParticipantsArray(participant1, participant2)));
  }

  /**
   * Configures an Hangouts chat Meeting to be added to our Event.
   *
   * @return tHe ConferenceData Object
   */
  public ConferenceData createAdLibVideoMeeting() {
    ConferenceData conferenceData = new ConferenceData();
    CreateConferenceRequest conferenceRequest =
        new CreateConferenceRequest().setRequestId("Ad-lib");
    ConferenceSolution conferenceSolution =
        new ConferenceSolution().setIconUri(null).setKey(new ConferenceSolutionKey());
    EntryPoint entryPoint =
        new EntryPoint()
            .setEntryPointType("video")
            .setLabel("meet.google.com/")
            .setUri("http://meet.google.com/new");
    ConferenceSolutionKey conferenceSolutionKey =
        new ConferenceSolutionKey().setType("hangoutsMeet");
    conferenceRequest
        .setConferenceSolutionKey(conferenceSolutionKey)
        .setStatus(new ConferenceRequestStatus());
    conferenceData
        .setEntryPoints(Collections.singletonList(entryPoint))
        .setConferenceSolution(conferenceSolution)
        .setCreateRequest(conferenceRequest);
    return conferenceData;
  }

  /**
   * @param event Event representing the Ad-lib session to be attended
   * @throws IOException If the credentials folder is wrong
   * @throws GeneralSecurityException In case of any other exceptions
   */
  public void addAdlibEventToCalendar(Event event) throws IOException, GeneralSecurityException {

    // Build a new authorized API client service.
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    Calendar service =
        new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
            .setApplicationName(APPLICATION_NAME)
            .build();

    service
        .events()
        .insert(event.getAttendees().get(1).getEmail(), event)
        .setConferenceDataVersion(1)
        .execute();
  }
}
