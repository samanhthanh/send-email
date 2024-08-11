package com.example.mailsennd.repositoty;

import com.example.mailsennd.entites.ScheduledEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ScheduledEmailRepository extends JpaRepository<ScheduledEmail, Long> {
    List<ScheduledEmail> findByIsSentFalse();

    List<ScheduledEmail> findByNextExecutionTimeBefore(Date date);
}
