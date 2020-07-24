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
import com.google.api.services.gmail.model.Message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.codec.binary.Base64;

/** Class representing the notification system capable of sending mail to the user. */
public class EmailNotifier {

  //   TODO(#35): Create a dummy email account for ad lib itself to send emails.
  private static final String APPLICATION_EMAIL = "Adlib-Step@gmail.com";

  /**
   * The user's email address. The special value me can be used to indicate the authenticated user.
   */
  private static final String AUTH_USERNAME = "me";

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
   * @param recipientEmail email of user to whom is to be notified
   * @param subjectText subject of the email
   * @param bodyText body text of the email
   * @return the MimeMessage to be used to send email
   * @throws MessagingException if there was a problem accessing the Store
   */
  private MimeMessage createEmail(String recipientEmail, String subjectText, String bodyText)
      throws MessagingException {

    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, /* authenticator= */ null);

    MimeMessage email = new MimeMessage(session);

    email.setFrom(new InternetAddress(APPLICATION_EMAIL));
    email.addRecipient(RecipientType.TO, new InternetAddress(recipientEmail));
    email.setSubject(subjectText);
    email.setText(bodyText);
    return email;
  }

  /** Function that access its api and using it sends an email */
  //   TODO(#36): Replace body to send real link to user instead of generic.
  public void sendMatchEmail(String recipientName, String recipientEmail)
      throws MessagingException, IOException {
    MimeMessage email =
        createEmail(
            recipientEmail,
            "Ad-lib: We found you a match!",
            String.format(
                " We found you a match with %s "
                    + " Check your calendar for your meeting event,"
                    + " and feel free to join the Meet call now!\n",
                recipientName));
    Message messageWithEmail = createMessageWithEmail(email);
    service.users().messages().send("me", messageWithEmail).execute();
  }

  /** Function that access its api and using it sends an email */
  public void sendExpiredEmail(String expiredRecipientUsername, String expiredRecipientEmail)
      throws MessagingException, IOException {
    MimeMessage email =
        createEmail(
            expiredRecipientEmail,
            "Ad-lib: Sorry, we couldn't find you a match!",
            String.format(
                " Sorry %s We couldn't find a match for you this time, but we encourage you to "
                    + " please try again later! \n "
                    + " Best, \n"
                    + " The Ad-lib team \n",
                expiredRecipientUsername));
    Message messageWithEmail = createMessageWithEmail(email);
    service.users().messages().send(AUTH_USERNAME, messageWithEmail).execute();
  }
}
