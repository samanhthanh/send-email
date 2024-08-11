package com.example.mailsennd.schedule;

import com.example.mailsennd.entites.Recipient;
import com.example.mailsennd.entites.ScheduledEmail;
import com.example.mailsennd.repositoty.ScheduledEmailRepository;
import com.example.mailsennd.services.EmailService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class EmailScheduler {
    @Autowired
    private ScheduledEmailRepository scheduledEmailRepository;

    @Autowired
    private EmailService emailService;

    @Value("${default.scheduler.interval.minutes:2}") // Mặc định là 2 phút nếu không có cấu hình
    private int defaultIntervalMinutes;

    private ScheduledFuture<?> scheduledFuture;
    private int intervalMinutes;

    @PostConstruct
    public void init() {
        this.intervalMinutes = defaultIntervalMinutes;
        startScheduler();
    }

    public void startScheduler() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }

        Runnable emailTask = this::sendScheduledEmails;
        scheduledFuture = Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(emailTask, 0, intervalMinutes, TimeUnit.MINUTES);
    }

    public void updateIntervalMinutes(int newIntervalMinutes) {
        this.intervalMinutes = newIntervalMinutes;
        startScheduler(); // Khởi động lại với khoảng thời gian mới
    }

    @Transactional
    public void sendScheduledEmails() {
        Date now = new Date();
        List<ScheduledEmail> emailsToSend = scheduledEmailRepository.findByNextExecutionTimeBefore(now);

        for (ScheduledEmail scheduledEmail : emailsToSend) {
            List<String> emailAddresses = new ArrayList<>();
            for (Recipient recipient : scheduledEmail.getRecipients()) {
                emailAddresses.add(recipient.getEmail());
            }

            emailService.sendEmails(emailAddresses, scheduledEmail.getSubject(), scheduledEmail.getContent());

            // Cập nhật thời gian gửi email lần tiếp theo
            Date nextExecutionTime = new Date(now.getTime() + TimeUnit.MINUTES.toMillis(intervalMinutes));
            scheduledEmail.setNextExecutionTime(nextExecutionTime);
            scheduledEmail.setSent(true);
            scheduledEmailRepository.save(scheduledEmail);
        }
    }
}
