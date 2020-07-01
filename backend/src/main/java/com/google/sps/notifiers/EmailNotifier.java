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

package main.java.com.google.sps.notifiers;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.*;

import java.io.IOException;

/** An instance of the email notifier class is used to send emails to participants. */
public class EmailNotifier extends Notifier {
  private String toEmail;

  /**
   * @param name Name of the recipient of the user
   * @param toEmail Email that this email is going to sent to.
   */
  public EmailNotifier(String name, String toEmail) {
    super(name);
    this.toEmail = toEmail;
  }

  /** Function that access the Twilio api and using it sends an email */
  //    TODO(): Create a dummy email for ad lib itself to send emails.
  public void sendEmail() {
    Email from = new Email("jordangrant46@gmail.com");
    String subject = "Ad-lib: Match Found";
    Email to = new Email(toEmail);
    Content content =
        new Content(
            "text/plain", "Hey " + getName() + " Join your Ad lib Meeting with the link below");
    Mail mail = new Mail(from, subject, to, content);
    SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
    Request request = new Request();
    try {
      request.setMethod(Method.POST);
      request.setEndpoint("mail/send");
      request.setBody(mail.build());
      Response response = sg.api(request);

      System.out.println(response.getStatusCode());

      System.out.println(response.getBody());

      System.out.println(response.getHeaders());
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void setToEmail(String toEmail) {
    this.toEmail = toEmail;
  }
}
