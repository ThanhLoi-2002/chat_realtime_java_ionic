package com.zalo.modules.message.service;

import com.zalo.modules.message.entity.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageStatusRepository extends JpaRepository<MessageStatus, Long> {
    List<MessageStatus> findByMessageId(Long messageId);

    Optional<MessageStatus> findByMessageIdAndUserId(Long messageId, Long userId);
}