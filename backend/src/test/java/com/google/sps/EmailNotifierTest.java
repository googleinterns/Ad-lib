package com.google.sps;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.sps.notifs.EmailNotifier;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class EmailNotifierTest {
  private String testString;
  private String testName;
  private String testEmail;
  private EmailNotifier emailNotifier;
  @Mock private Gmail gmail;
  @Mock private Gmail.Users users;
  @Mock private Gmail.Users.Messages messages;
  @Mock private Gmail.Users.Messages.Send send;

  @Before
  public void setUp() throws IOException {
    testEmail = "jdoe@gmail.com";
    testName = "John";
    testString = "Text Text";
    gmail = mock(Gmail.class);
    // Mock to return list of users
    users = mock(Gmail.Users.class);
    // Mock to return list of messages.
    messages = mock(Gmail.Users.Messages.class);
    send = mock(Gmail.Users.Messages.Send.class);
    // Test Instance of email notifier class.
    emailNotifier = new EmailNotifier(testName, testEmail, gmail);
    // Emulating real behavior due to when method, will return list of users.
    when(gmail.users()).thenReturn(users);
    // Emulating real behaviour due to when method, will return list of messages
    when(users.messages()).thenReturn(messages);
    //    Emulating sending messages, when sent will return send mock.
    when(messages.send(any(), any())).thenReturn(send);
  }

  public MimeMessage convertToMimeMessage(Message msg) throws MessagingException {
    Properties properties = System.getProperties();
    Session session = Session.getDefaultInstance(properties);
    byte[] bytes = msg.decodeRaw();
    InputStream targetStream = new ByteArrayInputStream(bytes);
    return new MimeMessage(session, targetStream);
  }

  @Test
  public void testMessageHasCorrectApplicationName()
      throws MessagingException, IOException, GeneralSecurityException {
    ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);

    emailNotifier.notifyUser();

    verify(messages).send(any(), argument.capture());
    String applicationName = convertToMimeMessage(argument.getValue()).getFrom()[0].toString();
    assertThat(applicationName).isEqualTo("Adlib-Step@gmail.com");
  }

  @Test
  public void testMessageHasCorrectSubject()
      throws MessagingException, IOException, GeneralSecurityException {
    ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);

    emailNotifier.notifyUser();

    verify(messages).send(any(), argument.capture());
    String subjectName = convertToMimeMessage(argument.getValue()).getSubject();
    assertThat(subjectName).isEqualTo("Ad-Lib Meeting Found");
  }

  @Test
  public void testMessageShouldHaveCorrectBodyText()
      throws MessagingException, IOException, GeneralSecurityException {
    ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);

    emailNotifier.notifyUser();

    verify(messages).send(any(), argument.capture());
    String bodyText = convertToMimeMessage(argument.getValue()).getContent().toString();
    assertThat(bodyText)
        .isEqualTo(
            " Hey John Please Join your Ad-Lib meeting via the link below : \n"
                + " http://meet.google.com/new");
  }

  @Test
  public void testMessageShouldHaveIncorrectBodyText()
      throws MessagingException, IOException, GeneralSecurityException {
    ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);

    emailNotifier.notifyUser();

    verify(messages).send(eq("me"), argument.capture());

    String realString = convertToMimeMessage(argument.getValue()).getContent().toString();
    assertThat(realString).isNotEqualTo(testString);
  }

  @Test
  public void testMessageShouldHaveCorrectRecipients()
      throws MessagingException, IOException, GeneralSecurityException {
    ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);

    emailNotifier.notifyUser();

    verify(messages).send(any(), argument.capture());
    Address[] allRecipients = convertToMimeMessage(argument.getValue()).getAllRecipients();
    Address[] correctRecipients =
        new Address[] {
          new InternetAddress(testEmail),
        };
    assertThat(allRecipients).isEqualTo(correctRecipients);
    assertThat(allRecipients).asList().containsExactly(new InternetAddress(testEmail));
  }
}
