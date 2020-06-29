// Class used to send users emails regarding their status in regards to their Ad-Lib Meeting.

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.*;

import java.io.IOException;



public class EmailNotifier extends Notifier {

    // Member Variables 
    
    public String toEmail ;       
    public String fromEmail;

    public EmailNotifier(String Name, String fromEmail, String toEmail)
    {

        super(Name);
        this.toEmail = toEmail;
        this.fromEmail = fromEmail;
    }
    
    public static void main(Strings[], args) throws IOException {
        Email from = new Email(fromEmail);
        String subject = "Adlib Meeting Notification";
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", "Join your Adlib Meeting with the link below");
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
            throw ex;
    }
  }
}
