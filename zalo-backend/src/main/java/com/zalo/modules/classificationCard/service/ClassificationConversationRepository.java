package com.zalo.modules.classificationCard.service;

import com.zalo.modules.classificationCard.entity.ClassificationConversation;
import com.zalo.modules.classificationCard.entity.ClassificationConversationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClassificationConversationRepository extends JpaRepository<ClassificationConversation, ClassificationConversationId> {
    @Query("SELECT c.conversationId FROM ClassificationConversation c WHERE c.classificationId = :cardId")
    List<Long> findConversationIdsByCardId(Long cardId);

    void deleteByClassificationIdAndConversationIdIn(Long cardId, List<Long> convIds);

    void deleteByConversationIdInAndUserId(List<Long> convIds, Long userId);

    List<ClassificationConversation> findByClassificationIdIn(List<Long> cardIds);

    Optional<ClassificationConversation> findByClassificationIdAndConversationId(Long classificationId, Long conversationId);
}
