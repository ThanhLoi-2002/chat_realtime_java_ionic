package com.zalo.modules.app.conversation.service;

import com.zalo.modules.app.conversation.entities.ConversationPin;
import com.zalo.modules.app.conversation.entities.ConversationPinId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationPinRepository extends JpaRepository<ConversationPin, ConversationPinId> {
    ConversationPin findByConversationIdAndUserId(Long convId, Long userId);
    List<ConversationPin> findAllByConversationIdInAndUserId(List<Long> convId, Long userId);
}
