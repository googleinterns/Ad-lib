package com.google.sps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.common.truth.Truth;
import com.google.sps.notifs.EmailNotifier;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class EmailNotifierTest {
  private EmailNotifier emailNotifier;
  @Mock private Gmail gmail;
  @Mock private Gmail.Users users;
  @Mock private Gmail.Users.Messages messages;
  @Mock private Gmail.Users.Messages.Send send;
  private Message testMessage;

  @Before
  public void setUp() throws MessagingException, IOException {
    gmail = mock(Gmail.class);
    // Mock to return list of users
    users = mock(Gmail.Users.class);
    // Mock to return list of messages.
    messages = mock(Gmail.Users.Messages.class);
    send = mock(Gmail.Users.Messages.Send.class);
    MimeMessage mimemessage = createTestMessage();
    testMessage = createMessageFromMime(mimemessage);
    // Test Instance of email notifier class.
    emailNotifier = new EmailNotifier("John", "jdoe@gmail.com ", gmail);
    // Emulating real behavior due to when method, will return list of users.
    when(gmail.users()).thenReturn(users);
    // Emulating real behaviour due to when method, will return list of messages
    when(users.messages()).thenReturn(messages);
    //    Emulating sending messages, when sent will return send mock.
    when(messages.send(any(), any())).thenReturn(send);
    //    When send mock is executed will return test message.
    when(send.execute()).thenReturn(testMessage);
  }

  public MimeMessage createTestMessage() throws MessagingException {
    Properties properties = System.getProperties();
    Session session = Session.getDefaultInstance(properties);
    MimeMessage message = new MimeMessage(session);
    Address[] addresses =
        new Address[] {
          new InternetAddress("tcwang@google.com"),
          new InternetAddress("grantjustice@google.com"),
          new InternetAddress("kevinhowald@google.com")
        };
    message.setFrom(new InternetAddress("Adlib-Step@gmail.com"));
    message.setSubject("Test Subject");
    message.setText("Test Text");
    message.setRecipients(javax.mail.Message.RecipientType.TO, addresses);
    return message;
  }

  public Message createMessageFromMime(MimeMessage mimeMessage)
      throws IOException, MessagingException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    mimeMessage.writeTo(buffer);
    byte[] bytes = buffer.toByteArray();
    String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
    Message message = new Message();
    message.setRaw(encodedEmail);
    return message;
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
    Truth.assertWithMessage("These two strings should be the same")
        .that("Adlib-Step@gmail.com")
        .isEqualTo(applicationName);
  }

  @Test
  public void testMessageHasCorrectSubject()
      throws MessagingException, IOException, GeneralSecurityException {
    ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
    emailNotifier.notifyUser();
    verify(messages).send(any(), argument.capture());
    String subjectName = convertToMimeMessage(argument.getValue()).getSubject();
    Truth.assertWithMessage("These Two strings should be the same ")
        .that("Ad-Lib Meeting Found")
        .isEqualTo(subjectName);
  }

  @Test
  public void testMessageShouldHaveCorrectBodyText()
      throws MessagingException, IOException, GeneralSecurityException {
    ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
    emailNotifier.notifyUser();
    verify(messages).send(any(), argument.capture());
    String bodyText = convertToMimeMessage(argument.getValue()).getContent().toString();
    Truth.assertWithMessage("These Two strings should be the same ")
        .that(
            " Hey John Please Join your Ad-Lib meeting via the link below : \n"
                + " http://meet.google.com/new")
        .isEqualTo(bodyText);
  }

  @Test
  public void testMessageShouldHaveIncorrectBodyText()
      throws MessagingException, IOException, GeneralSecurityException {
    ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
    emailNotifier.notifyUser();
    verify(messages).send(eq("me"), argument.capture());
    String realString = convertToMimeMessage(argument.getValue()).getContent().toString();
    Truth.assertWithMessage("These Two strings should not be the same ")
        .that("anyString()")
        .isEqualTo(realString);
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
          new InternetAddress("jdoe@gmail.com "),
        };
    Truth.assertWithMessage("These Two arrays should be the same ")
        .that(correctRecipients)
        .isEqualTo(allRecipients);
    Truth.assertThat(allRecipients[0]).isEqualTo(correctRecipients[0]);
  }

  @Test
  public void testMessageShouldNotHaveCorrectRecipients()
      throws MessagingException, IOException, GeneralSecurityException {
    ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
    emailNotifier.notifyUser();
    verify(messages).send(any(), argument.capture());
    Address[] allRecipients = convertToMimeMessage(argument.getValue()).getAllRecipients();
    Truth.assertWithMessage("These Two arrays should be the same ")
        .that(new InternetAddress[] {new InternetAddress()})
        .isEqualTo(allRecipients);
  }
}
