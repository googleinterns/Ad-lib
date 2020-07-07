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

package main.java.com.google.sps.notifs;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/** Class representing the notification system capable of sending mails to the user. */
public class EmailNotifier extends Notifier {

    private static final String APPLICATION_NAME = "Ad-libs";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    /**
     * Global instance of the scopes required by this quickstart. If modifying these scopes, delete
     * your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM);

    private static final String CREDENTIALS_FILE_PATH = "credentials.json";

    /** Variable representing the email to be notified */
    protected String toEmail;

    /**
     * @param name Name of the recipient of the user
     * @param to Email that this email is going to sent to.
     */
    public EmailNotifier(String name, String to) {
        super(name);
        this.toEmail = to;
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
                new GoogleAuthorizationCodeFlow.Builder(
                                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(
                                new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                        .setAccessType("online")
                        .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8000).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to email address of the receiver
     * @param subject subject of the email
     * @param bodyText body text of the email
     * @return the MimeMessage to be used to send email
     * @throws MessagingException
     */
    public static MimeMessage createEmail(String to, String subject, String bodyText)
            throws MessagingException {

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(to));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    /**
     * Create a message from an email.
     *
     * @param emailContent Email to be set to raw of message
     * @return a message containing a base64url encoded email
     * @throws IOException
     * @throws MessagingException
     */
    public static Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    public static void main(String[] args)
            throws GeneralSecurityException, MessagingException, IOException {
        EmailNotifier emailNotifier = new EmailNotifier("jordan", "grantjustice@google.com");
        emailNotifier.notifyUser();
    }

    /**
     * Getter function that returns the string representing the email recipients name .
     *
     * @return String representing the name of the email recipient.
     */
    @Override
    public String getName() {
        return super.getName();
    }

    /** Function that access its api and using it sends an email */
    //    TODO(): Create a dummy email for ad lib itself to send emails.
    public void notifyUser() throws MessagingException, IOException, GeneralSecurityException {

        MimeMessage email =
                createEmail(
                        getToEmail(),
                        "Ad-Lib Meeting Found",
                        " Hey "
                                + this.getName()
                                + " Please Join your Ad-Lib meeting via the link below : \n"
                                + " http://meet.google.com/new");
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service =
                new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();
        Message messageWithEmail = createMessageWithEmail(email);
        service.users().messages().send("me", messageWithEmail).execute();
    }

    public String getToEmail() {
        return toEmail;
    }
}
