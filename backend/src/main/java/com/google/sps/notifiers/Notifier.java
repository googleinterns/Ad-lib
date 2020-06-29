// Class used to send users emails regarding their status in regards to their Ad-Lib Meeting.

import java.lang.*;
import java.io.File;
import com.sendgrid.*;


public class Notifier {

    // Member Variables 
    
    String Name;        /* String represenintg the user who is to be notified's name */

    public Notifier(String Name, String fromEmail, String, toEmail)
    {
        this.Name = Name;
    }
}