package org.cshaifa.spring.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

public class EmailUtils {

    private static List<ScheduledExecutorService> schedulers = new ArrayList<>();

    public static void sendEmailAt(String recipientMail, String recipientName, String subject, String content, Timestamp timestamp) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> sendEmail(recipientMail, recipientName, subject, content),
                                                              timestamp.getTime() - Calendar.getInstance().getTime().getTime(), TimeUnit.MILLISECONDS);
        schedulers.add(scheduler);
    }

    public static void sendEmail(String recipientMail, String recipientName, String subject, String content) {
        MailerBuilder.buildMailer()
            .sendMail(EmailBuilder.startingBlank()
            .to(recipientName, recipientMail)
            .withSubject(subject)
            .withHTMLText(content)
            .buildEmail());
    }

    public static void shutdownEmailSchedulers() {
        schedulers.forEach(ScheduledExecutorService::shutdown);
    }

}
