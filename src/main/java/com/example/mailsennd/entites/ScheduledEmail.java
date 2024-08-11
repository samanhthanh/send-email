package com.example.mailsennd.entites;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "ScheduledEmail2")
public class ScheduledEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isSent;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextExecutionTime;

    @Column
    private Integer intervalMinutes;

    @OneToMany(mappedBy = "scheduledEmail", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recipient> recipients;
}
