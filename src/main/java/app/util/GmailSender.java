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

       this.email = "FindTeamICE@gmail.com";
       this.password = "blsn rlce itag rszu";

       if (email == null || password == null)
       {
           throw new IllegalStateException("MAIL_USERNAME and MAIL_PASSWORD environment variables must be set.");
       }
   }

//    public void sendPlainTextEmail(String to, String subject, String body) throws MessagingException
//    {
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true"); // TLS
//        props.put("mail.smtp.host", "smtp.gmail.com");
//        props.put("mail.smtp.port", "587");
//        Session session = Session.getInstance(props, new Authenticator()
//        {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication()
//            {
//                return new PasswordAuthentication(email, password);
//            }
//        });
//        Message message = new MimeMessage(session);
//        message.setFrom(new InternetAddress(email));
//        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
//        message.setSubject(subject);
//        message.setText(body); // Plain text only
//        Transport.send(message);
//        System.out.println("Email sent successfully to " + to);
//    }

    public void sendPlainTextEmail(String to, String subject, String body) throws MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.sendgrid.net");
        props.put("mail.smtp.port", "2525");

        // Optional but helpful during setup
        // props.put("mail.debug", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {

                Dotenv dotenv = Dotenv.load();
                return new PasswordAuthentication( dotenv.get("SENDGRID_USER"), dotenv.get("SENDGRID_PASSWORD"));

            }
        });

        Message message = new MimeMessage(session);

        // Must EXACTLY match the verified Single Sender
        message.setFrom(new InternetAddress("findteamice@gmail.com"));

        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(to)
        );

        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);

        System.out.println("Email sent successfully to " + to);
    }

}
