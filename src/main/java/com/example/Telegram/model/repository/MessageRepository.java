package com.example.Telegram.model.repository;

import com.example.Telegram.model.data.Message;
import com.example.Telegram.model.data.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderOrRecipientOrderBySentAtDesc(User sender, User recipient, Pageable pageable);

    Message findTop1ByOrderByIdDesc();

    Page<Message> findAllByOrderByIdDesc(Pageable pageable);

}
