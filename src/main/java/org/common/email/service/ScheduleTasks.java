package org.common.email.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;

@Component
public class ScheduleTasks {
    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 * * * * ?")
    public void scheduleTaskWithCronExpression() throws MessagingException {
        emailService.sendMail("mohdihabibi@gmail.com", "Test Subject", "TestMessage");
    }
}
