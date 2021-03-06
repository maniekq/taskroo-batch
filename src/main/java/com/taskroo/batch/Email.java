package com.taskroo.batch;

import org.joda.time.DateTime;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class Email {

    private final JavaMailSender sender;
    private final String recipient;
    private final String content;

    public Email(JavaMailSender sender, String recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    }

    public void send() throws MessagingException {

        MimeMessage message = sender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("dailyroo@taskroo.com");
        helper.setSubject("TaskRoo due date tasks for " + DateTime.now().toString("yyyy-MM-dd"));
        helper.setTo(recipient);
        helper.setText(content);

        System.out.println("Sending email to: " + recipient);
        sender.send(message);

    }
}
