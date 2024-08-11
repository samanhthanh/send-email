package com.example.mailsennd.controller;

import com.example.mailsennd.dto.ScheduledEmailRequest;
import com.example.mailsennd.entites.Recipient;
import com.example.mailsennd.entites.ScheduledEmail;
import com.example.mailsennd.repositoty.RecipientRepository;
import com.example.mailsennd.repositoty.ScheduledEmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/email")
public class EmailController {
    @Autowired
    private ScheduledEmailRepository scheduledEmailRepository;

    @Autowired
    private RecipientRepository recipientRepository;

    @PostMapping("/schedule")
    @Transactional
    public void scheduleEmail(@RequestBody ScheduledEmailRequest emailRequest) {
        ScheduledEmail scheduledEmail = new ScheduledEmail();
        scheduledEmail.setSubject(emailRequest.getSubject());
        scheduledEmail.setContent(emailRequest.getContent());
        scheduledEmail.setIntervalMinutes(emailRequest.getIntervalMinutes()); // Đảm bảo có phương thức này
        scheduledEmail.setSent(false);
        scheduledEmail.setNextExecutionTime(new Date());

        // Lưu scheduledEmail trước để đảm bảo nó có ID
        scheduledEmail = scheduledEmailRepository.save(scheduledEmail);

        // Kiểm tra recipients không phải là null
        List<String> recipients = emailRequest.getRecipients();
        if (recipients != null) {
            for (String email : recipients) {
                Recipient recipient = new Recipient();
                recipient.setEmail(email);
                recipient.setScheduledEmail(scheduledEmail); // Thiết lập liên kết với scheduledEmail
                recipientRepository.save(recipient); // Lưu recipient
            }
        } else {
            // Xử lý khi không có người nhận
            // Có thể ném lỗi hoặc log thông báo
        }
    }
}
