package com.example.mailsennd.services;

import com.example.mailsennd.entites.Recipient;
import com.example.mailsennd.entites.ScheduledEmail;
import com.example.mailsennd.repositoty.ScheduledEmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ScheduledEmailRepository scheduledEmailRepository;
    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledFuture;
    private String cronExpression = "0 * * * * ?"; // Biểu thức cron mặc định

    public EmailService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
        startScheduler();
    }

    public void startScheduler() {
        Runnable emailTask = this::sendScheduledEmails;

        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }

        scheduledFuture = taskScheduler.schedule(emailTask, new CronTrigger(cronExpression));
    }

    public void updateCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
        startScheduler(); // Khởi động lại với biểu thức cron mới
    }

    @Transactional
    public void sendEmails(List<String> recipients, String subject, String content) {
        for (String recipient : recipients) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipient);
            message.setSubject(subject);
            message.setText(content);

            try {
                mailSender.send(message);
                logger.info("Email sent to: {}", recipient);
            } catch (MailSendException e) {
                logger.error("Failed to send email to: {}", recipient, e);
                // Có thể thêm xử lý lỗi hoặc thêm email vào hàng đợi để gửi lại sau
            }
        }
    }

    @Transactional
    public void sendScheduledEmails() {
        List<ScheduledEmail> scheduledEmails = scheduledEmailRepository.findByIsSentFalse();

        for (ScheduledEmail scheduledEmail : scheduledEmails) {
            List<String> emailAddresses = new ArrayList<>();
            for (Recipient recipient : scheduledEmail.getRecipients()) {
                emailAddresses.add(recipient.getEmail());
            }

            if (emailAddresses.isEmpty()) {
                logger.warn("No recipients found for scheduled email ID: {}", scheduledEmail.getId());
                continue;
            }

            sendEmails(emailAddresses, scheduledEmail.getSubject(), scheduledEmail.getContent());
            scheduledEmail.setSent(true); // Đánh dấu là đã gửi
            scheduledEmailRepository.save(scheduledEmail);
        }
    }
}
