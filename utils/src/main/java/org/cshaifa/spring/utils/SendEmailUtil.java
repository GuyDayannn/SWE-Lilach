package org.cshaifa.spring.utils;

import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

public class SendEmailUtil {

    public static void sendEmail(String recipientMail, String recipientName, String subject, String content) throws Exception{
        MailerBuilder.buildMailer()
            .sendMail(EmailBuilder.startingBlank()
            .to(recipientName, recipientMail)
            .withSubject(subject)
            .withHTMLText(content)
            .buildEmail());
    }

}
