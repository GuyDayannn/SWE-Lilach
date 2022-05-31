package org.cshaifa.spring.utils;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.awt.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SendEmailUtil {
    public static void sendEmail(String recipient) throws Exception{
        System.out.println("preparing to send email");
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", 587);

        String myAccountEmail = "swe.lilach@gmail.com";
        String password = "lilach123";


        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("swe.lilach@gmail.com", "lilach123");
            }
        });

        Message message = prepareMessage(session,myAccountEmail,recipient);

        Transport.send(message);
        System.out.println("Message sent successfully");

    }

    private static Message prepareMessage(Session session,String myAccountEmail,String recipient) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress((myAccountEmail)));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject("LiLach");
            MimeMultipart multipart = new MimeMultipart();

            MimeBodyPart attachment = new MimeBodyPart();
            //attachment.attachFile("lilach-client/src/main/resources/org.cshaifa.spring.client/images/LiLachLogo.png");

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent("<h1>Just a test</h1>", "text/html");

            multipart.addBodyPart(messageBodyPart);
            //multipart.addBodyPart(attachment);

            message.setContent(multipart);
            return message;

        } catch (Exception ex) {
            Logger.getLogger(SendEmailUtil.class.getName()).log(Level.SEVERE,null, ex);
        }
        return null;


    }

}

