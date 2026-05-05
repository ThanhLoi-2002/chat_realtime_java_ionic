package com.zalo.modules.conversation.service;

import com.zalo.modules.conversation.entities.ConversationPin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationPinRepository extends JpaRepository<ConversationPin, Long> {
    ConversationPin findByConversationIdAndUserId(Long convId, Long userId);
    List<ConversationPin> findAllByConversationIdInAndUserId(List<Long> convId, Long userId);
}
