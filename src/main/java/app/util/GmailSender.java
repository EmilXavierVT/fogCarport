package app.util;

import io.github.cdimascio.dotenv.Dotenv;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class GmailSender
{
   String email;
   String password;

   public GmailSender()
   {
       Dotenv dotenv = Dotenv.load();
       this.email = dotenv.get("EMAIL");
       this.password = dotenv.get("PASSWORD");

       if (email == null || password == null)
       {
           throw new IllegalStateException("MAIL_USERNAME and MAIL_PASSWORD environment variables must be set.");
       }
   }

    public void sendPlainTextEmail(String to, String subject, String body) throws MessagingException
    {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // TLS
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props, new Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(email, password);
            }
        });
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(email));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body); // Plain text only
        Transport.send(message);
        System.out.println("Email sent successfully to " + to);
    }
}
