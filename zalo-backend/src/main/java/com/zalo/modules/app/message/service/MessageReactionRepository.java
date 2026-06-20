package com.zalo.modules.app.message.service;

import com.zalo.modules.app.message.entity.MessageReaction;
import com.zalo.modules.app.message.entity.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageReactionRepository extends JpaRepository<MessageReaction, Long> {
    void deleteByMessageIdAndCu(Long messageId, Long cu);

    MessageReaction findByMessageIdAndCuAndType(Long messageId, Long cu, ReactionType type);

    List<MessageReaction> findByMessageId(Long messageId);
    List<MessageReaction> findByMessageIdIn(List<Long> messageIds);
}
