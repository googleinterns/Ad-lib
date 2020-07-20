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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.common.collect.ImmutableList;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.codec.binary.Base64;

/** Class representing the notification system capable of sending mail to the user. */
public class EmailNotifier {

  private static final String APPLICATION_NAME = "Ad-lib";
  private static final String APPLICATION_EMAIL = "Adlib-Step@gmail.com";

  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static final String TOKENS_DIRECTORY_PATH = "tokens";
  /**
   * Global instance of the scopes required by this quickstart. If modifying these scopes, delete
   * your previously saved tokens/ folder.
   */
  private static final List<String> SCOPES = ImmutableList.of(GmailScopes.MAIL_GOOGLE_COM);

  private static final String CREDENTIALS_FILE_PATH = "backend/credentials.json";
  /** The email to be notified */
  private final String recipientEmail;
  /** The notification recipient */
  private final String recipientName;
  /** The gmail service */
  private final Gmail service;

  /**
   * @param recipientName Name of the recipient of the user.
   * @param recipientEmail The email address that this email is going to be sent to.
   * @param service Gmail service dependency.
   */
  public EmailNotifier(String recipientName, String recipientEmail, Gmail service) {
    this.recipientName = recipientName;
    this.recipientEmail = recipientEmail;
    this.service = service;
  }

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
    InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8000).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
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
  private MimeMessage createEmail(String subject, String bodyText) throws MessagingException {

    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, /* authenticator= */ null);

    MimeMessage email = new MimeMessage(session);

    email.setFrom(new InternetAddress(APPLICATION_EMAIL));
    email.addRecipient(RecipientType.TO, new InternetAddress(recipientEmail));
    email.setSubject(subject);
    email.setText(bodyText);
    return email;
  }

  /** Function that access its api and using it sends an email */
  //   TODO(#35): Create a dummy email for ad lib itself to send emails.
  //   TODO(#36): Replace body to send real link to user instead of generic.
  public void notifyUser() throws MessagingException, IOException, GeneralSecurityException {
    NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    MimeMessage email =
        createEmail(
            "Ad-Lib Meeting Found",
            " Hey "
                + recipientName
                + " Please Join your Ad-Lib meeting via the link below : \n"
                + " http://meet.google.com/new");
    Message messageWithEmail = createMessageWithEmail(email);
    service.users().messages().send("me", messageWithEmail).execute();
  }
}
