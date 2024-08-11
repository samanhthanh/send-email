package com.example.mailsennd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MailSenndApplication {

    public static void main(String[] args) {
        SpringApplication.run(MailSenndApplication.class, args);
    }

}
