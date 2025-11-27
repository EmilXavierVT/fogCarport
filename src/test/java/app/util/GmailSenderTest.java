package app.util;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GmailSenderTest {
    @Test
    public void testSendEmail(){
      GmailSender sender = new GmailSender();

        String to = "danielhalawi22@gmail.com";  // Erstat med din modtager
        String subject = "Testmail fra Java";
        String body = "Hej! Dette er en simpel testmail sendt med Java og Jakarta Mail.";

        try {
            sender.sendPlainTextEmail(to, subject, body);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}