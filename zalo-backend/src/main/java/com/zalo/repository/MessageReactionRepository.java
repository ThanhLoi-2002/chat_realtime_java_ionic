package com.zalo.repository;

import com.zalo.model.MessageReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageReactionRepository extends JpaRepository<MessageReaction, Long> {
    void deleteByMessageIdAndCu(Long messageId, Long cu);

    List<MessageReaction> findByMessageId(Long messageId);
    List<MessageReaction> findByMessageIdIn(List<Long> messageIds);
}
