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

package com.google.sps.notifs;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.binary.Base64;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;

/** Class representing the notification system capable of sending mail to the user. */
public class EmailNotifier {

  //   TODO(#35): Create a dummy email for ad lib itself to send emails.
  private static final String APPLICATION_EMAIL = "Adlib-Step@gmail.com";

  /**
   * Global instance of the scopes required by this quickstart. If modifying these scopes, delete
   * your previously saved tokens/ folder.
   */
  private static final List<String> SCOPES = ImmutableList.of(GmailScopes.MAIL_GOOGLE_COM);

  /** The gmail service */
  private final Gmail service;

  /** @param service Gmail service dependency. */
  public EmailNotifier(Gmail service) {
    this.service = service;
  }

  /**
   * Create a message from an email.
   *
   * @param emailContent Email to be sent to raw of message
   * @return a message containing a base64url encoded email
   * @throws IOException if an error occurs writing to the stream
   * @throws MessagingException for other failures
   */
  private static Message createMessageWithEmail(MimeMessage emailContent)
      throws MessagingException, IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    emailContent.writeTo(buffer);
    byte[] bytes = buffer.toByteArray();
    String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
    Message message = new Message();
    message.setRaw(encodedEmail);
    return message;
  }

  /**
   * Create a MimeMessage using the parameters provided.
   *
   * @param subject subject of the email
   * @param bodyText body text of the email
   * @return the MimeMessage to be used to send email
   * @throws MessagingException if there was a problem accessing the Store
   */
  private MimeMessage createEmailWithSingleRecipient(
      String recipientUsername, String subject, String bodyText) throws MessagingException {

    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, /* authenticator= */ null);

    MimeMessage email = new MimeMessage(session);

    email.setFrom(new InternetAddress(APPLICATION_EMAIL));
    email.addRecipient(RecipientType.TO, new InternetAddress(recipientUsername + "@google.com"));
    email.setSubject(subject);
    email.setText(bodyText);
    return email;
  }

  /**
   * Create a MimeMessage using the parameters provided.
   *
   * @param bodyText body text of the email
   * @return the MimeMessage to be used to send email
   * @throws MessagingException if there was a problem accessing the Store
   */
  private MimeMessage createEmailWithMultiRecipients(
      String firstRecipientUsername, String secondRecipientUsername, String bodyText)
      throws MessagingException {

    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, /* authenticator= */ null);

    MimeMessage email = new MimeMessage(session);

    email.setFrom(new InternetAddress(APPLICATION_EMAIL));
    email.addRecipients(
        RecipientType.TO,
        new InternetAddress[] {
          new InternetAddress(firstRecipientUsername + "@google.com"),
          new InternetAddress(secondRecipientUsername + "@google.com")
        });
    email.setSubject("Ad-lib Match Found");
    email.setText(bodyText);
    return email;
  }

  /** Function that access its api and using it sends an email */
  //   TODO(#36): Replace body to send real link to user instead of generic.
  public void sendMatchEmail(String firstMatchRecipientName, String secondMatchRecipientName)
      throws MessagingException, IOException, GeneralSecurityException {
    MimeMessage email =
        createEmailWithMultiRecipients(
            firstMatchRecipientName,
            secondMatchRecipientName,
            "Congratulations"
                + " Please Join your Ad-Lib meeting via the link below : \n"
                + " http://meet.google.com/new");
    Message messageWithEmail = createMessageWithEmail(email);
    service.users().messages().send("me", messageWithEmail).execute();
  }

  /** Function that access its api and using it sends an email */
  //   TODO(#36): Replace body to send real link to user instead of generic.
  public void sendExpiredEmail(String expiredRecipientName) throws MessagingException, IOException {
    MimeMessage email =
        createEmailWithSingleRecipient(
            expiredRecipientName,
            "Ad-Lib Meeting Found",
            " We apologize "
                + expiredRecipientName
                + "\n"
                + " You're search for a match took longer than anticipated ! \n"
                + " Please try again at a later time ! : \n");
    Message messageWithEmail = createMessageWithEmail(email);
    service.users().messages().send("me", messageWithEmail).execute();
  }

  /** Function that access its api and using it sends an email */
  //   TODO(#36): Replace body to send real link to user instead of generic.
  public void sendNoMatchEmail(String noMatchParticipantName)
      throws MessagingException, IOException {
    MimeMessage noMatchEmail =
        createEmailWithSingleRecipient(
            noMatchParticipantName,
            "Ad-Lib Not Meeting Found",
            " Sorry "
                + noMatchParticipantName
                + " We could'nt find you a Match :( \n"
                + " Please try again later.");
    Message messageWithEmail = createMessageWithEmail(noMatchEmail);
    service.users().messages().send("me", messageWithEmail).execute();
  }
}
