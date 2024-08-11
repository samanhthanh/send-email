package com.example.mailsennd.entites;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Recipient2")
public class Recipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduled_email_id")
    private ScheduledEmail scheduledEmail;
}