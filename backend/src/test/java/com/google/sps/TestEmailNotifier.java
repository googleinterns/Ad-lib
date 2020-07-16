package com.google.sps;

import com.google.sps.notifs.EmailNotifier;
import org.junit.Assert;
import org.junit.Test;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;
import static org.junit.Assert.*;

public class TestEmailNotifier {

  public MimeMessage createTestMessage() {
    Properties properties = System.getProperties();
    Session session = Session.getDefaultInstance(properties);
    return new MimeMessage(session);
  }

  private String getTextFromMessage(MimeMessage message) throws MessagingException, IOException {
    String result = "";
    result = message.getContent().toString();
    return result;
  }

  @Test
  public void constructorShouldSetNameAndEmailTo() {
    EmailNotifier emailNotifier = new EmailNotifier("John", "jdoe@gmail.com", null);
    assertEquals("John", emailNotifier.getRecipientName());
    assertEquals("jdoe@gmail.com", emailNotifier.getToEmail());
  }

  @Test
  public void testMessageHasCorrectApplicationName() throws MessagingException {
    EmailNotifier emailNotifier = new EmailNotifier("Jordan", "jjgrant@buffalo.edu", null);
    MimeMessage mimeMessage = emailNotifier.createEmail("", "", "");
    MimeMessage message = createTestMessage();
    message.setFrom(new InternetAddress("Adlib-Step@gmail.com"));
    Assert.assertArrayEquals(
        "The Application Name should be the same as the global variable in the Ad-lib class",
        message.getFrom(),
        mimeMessage.getFrom());
  }

  @Test
  public void testMessageHasCorrectSubject() throws MessagingException {
    EmailNotifier emailNotifier = new EmailNotifier("Jordan", "jjgrant@buffalo.edu", null);
    MimeMessage mimeMessage = emailNotifier.createEmail("", "Random Subject", "");
    MimeMessage message = createTestMessage();
    message.setSubject("Random Subject");
    Assert.assertEquals(
        "These two subjects should be the same", message.getSubject(), mimeMessage.getSubject());
  }

  @Test
  public void testMessageShouldHaveCorrectBodyText() throws MessagingException, IOException {
    EmailNotifier emailNotifier = new EmailNotifier("Jordan", "jjgrant@buffalo.edu", null);
    MimeMessage mimeMessage = emailNotifier.createEmail("", "", "Random Text");
    MimeMessage message = createTestMessage();
    message.setText("Random Text");
    Assert.assertEquals(
        "These two strings should not be the same ",
        getTextFromMessage(message),
        getTextFromMessage(mimeMessage));
  }

  @Test
  public void testMessageShouldHaveIncorrectBodyText() throws MessagingException, IOException {
    EmailNotifier emailNotifier = new EmailNotifier("Jordan", "jjgrant@buffalo.edu", null);
    MimeMessage mimeMessage = emailNotifier.createEmail("", "", "Welcome To  Your Ad-lib event");
    MimeMessage message = createTestMessage();
    message.setText("Please Leave Your Ad-lib event");
    Assert.assertNotEquals(
        "These two strings should not be the same",
        getTextFromMessage(message),
        getTextFromMessage(mimeMessage));
  }

  @Test
  public void testMessageShouldHaveCorrectRecipients() throws MessagingException, IOException {
    EmailNotifier emailNotifier = new EmailNotifier("Jordan", "jjgrant@buffalo.edu", null);
    MimeMessage mimeMessage = emailNotifier.createEmail("", "", "");
    MimeMessage message = createTestMessage();
    message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress("jjgrant@buffalo.edu"));
    assertArrayEquals(
        "These two messages should have the same single recipient ",
        message.getAllRecipients(),
        mimeMessage.getAllRecipients());
  }

  @Test
  public void testMessageShouldNotHaveCorrectRecipients() throws MessagingException, IOException {
    EmailNotifier emailNotifier = new EmailNotifier("Jordan", "jjgrant@buffalo.edu", null);
    MimeMessage mimeMessage =
        emailNotifier.createEmail(
            "jjgrant@buffalo.edu", "Random Subject", "Welcome To  Your Ad-lib event");
    MimeMessage message = createTestMessage();
    message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress("jjgrant@buffalo.edu"));
    message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress("jdoe@buffalo.edu"));
    message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress("grantj@buffalo.edu"));

    assertNotEquals(
        "These two messages should have different recipients ",
        message.getAllRecipients(),
        mimeMessage.getAllRecipients());
  }
}
