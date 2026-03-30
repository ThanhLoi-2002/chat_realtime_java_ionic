package com.zalo.repository;

import com.zalo.model.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageStatusRepository extends JpaRepository<MessageStatus, Long> {
    List<MessageStatus> findByMessageId(Long messageId);

    Optional<MessageStatus> findByMessageIdAndUserId(Long messageId, Long userId);
}