package com.google.sps;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

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
    when(messages.send(anyString(), any(Message.class))).thenReturn(send);
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
  public void constructorShouldSetNameAndEmail() {
    assertEquals("John", emailNotifier.recipientName);
    assertEquals("jdoe@gmail.com ", emailNotifier.recipientEmail);
  }

  @Test
  public void testMessageHasCorrectApplicationName()
      throws MessagingException, IOException, GeneralSecurityException {
    ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
    emailNotifier.notifyUser();
    verify(messages).send(anyString(), argument.capture());
    String applicationName = convertToMimeMessage(argument.getValue()).getFrom()[0].toString();
    Assert.assertEquals(
        "These two strings should be the same", "Adlib-Step@gmail.com", applicationName);
  }

  @Test
  public void testMessageHasCorrectSubject()
      throws MessagingException, IOException, GeneralSecurityException {
    ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
    emailNotifier.notifyUser();
    verify(messages).send(anyString(), argument.capture());
    String subjectName = convertToMimeMessage(argument.getValue()).getSubject();
    Assert.assertEquals(
        "These Two strings should be the same ", "Ad-Lib Meeting Found", subjectName);
  }

  @Test
  public void testMessageShouldHaveCorrectBodyText()
      throws MessagingException, IOException, GeneralSecurityException {
    ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
    emailNotifier.notifyUser();
    verify(messages).send(anyString(), argument.capture());
    String bodyText = convertToMimeMessage(argument.getValue()).getContent().toString();
    Assert.assertEquals(
        "These Two strings should be the same ",
        " Hey John Please Join your Ad-Lib meeting via the link below : \n"
            + " http://meet.google.com/new",
        bodyText);
  }

  @Test
  public void testMessageShouldHaveIncorrectBodyText()
      throws MessagingException, IOException, GeneralSecurityException {
    ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
    emailNotifier.notifyUser();
    verify(messages).send(eq("me"), argument.capture());
    String realString = convertToMimeMessage(argument.getValue()).getContent().toString();
    Assert.assertNotEquals("These Two strings should not be the same ", "anyString()", realString);
  }

  @Test
  public void testMessageShouldHaveCorrectRecipients()
      throws MessagingException, IOException, GeneralSecurityException {
    ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
    emailNotifier.notifyUser();
    verify(messages).send(anyString(), argument.capture());
    Address[] allRecipients = convertToMimeMessage(argument.getValue()).getAllRecipients();
    Address[] correctRecipients =
        new Address[] {
          new InternetAddress("jdoe@gmail.com "),
        };
    Assert.assertArrayEquals(
        "These Two arrays should be the same ", correctRecipients, allRecipients);
    Assert.assertEquals(allRecipients[0], correctRecipients[0]);
  }

  @Test
  public void testMessageShouldNotHaveCorrectRecipients()
      throws MessagingException, IOException, GeneralSecurityException {
    ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
    emailNotifier.notifyUser();
    verify(messages).send(anyString(), argument.capture());
    Address[] allRecipients = convertToMimeMessage(argument.getValue()).getAllRecipients();
    Assert.assertNotEquals(
        "These Two arrays should be the same ",
        new InternetAddress[] {new InternetAddress()},
        allRecipients);
  }
}
