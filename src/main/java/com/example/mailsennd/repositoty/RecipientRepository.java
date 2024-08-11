package com.example.mailsennd.repositoty;

import com.example.mailsennd.entites.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipientRepository extends JpaRepository<Recipient, Long> {
}
