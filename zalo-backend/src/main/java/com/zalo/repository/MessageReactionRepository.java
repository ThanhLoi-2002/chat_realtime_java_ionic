package com.zalo.repository;

import com.zalo.model.MessageReaction;
import com.zalo.model.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageReactionRepository extends JpaRepository<MessageReaction, Long> {
    void deleteByMessageIdAndCu(Long messageId, Long cu);

    MessageReaction findByMessageIdAndCuAndType(Long messageId, Long cu, ReactionType type);

    List<MessageReaction> findByMessageId(Long messageId);
    List<MessageReaction> findByMessageIdIn(List<Long> messageIds);
}
