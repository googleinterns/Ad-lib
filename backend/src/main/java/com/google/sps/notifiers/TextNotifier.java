// Class used to send users emails regarding their status in regards to their Ad-Lib Meeting.

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;



public class TextNotifier extends Notifier {

    // Member Variables 
    
    public int areaCode ;       
    public String phoneNumber;
    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");

    public TextNotifier(String Name, int areaCode, int phoneNumber)
    {

        super(Name);
        this.areaCode = areaCode;
        this.phoneNumber = phoneNumber;
        
    }
    public static void main(Strings[], args) throws IOException {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber("+12058272910"),
                new com.twilio.type.PhoneNumber("+3478469438"),
                "Test!")
            .create();

        System.out.println(message.getSid());
    }
    }
    

}