package com.zalo.modules.conversation.service;

import com.zalo.modules.conversation.entities.ConversationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ConversationMemberRepository extends JpaRepository<ConversationMember, Long> {
    List<ConversationMember> findByUserId(Long userId);

    List<ConversationMember> findByConversationId(Long conversationId);

    List<ConversationMember> findByConversationIdOrderByIdDesc(Long conversationId);

    List<ConversationMember> findByConversationIdAndUserIdInOrderByIdDesc(Long conversationId, List<Long> memberIds);

    Optional<ConversationMember> findByConversationIdAndUserId(Long conversationId, Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ConversationMember cm WHERE cm.conversationId = :conversationId AND cm.userId IN :userIds")
    void deleteMembersInConversation(Long conversationId, List<Long> userIds);

    @Modifying
    @Transactional
    @Query("DELETE FROM ConversationMember cm WHERE cm.conversationId = :conversationId")
    void deleteManyByConversationId(@Param("conversationId") Long conversationId);

    @Query("SELECT cm.conversationId FROM ConversationMember cm WHERE cm.userId = :userId")
    List<Long> findConversationIdsByUserId(@Param("userId") Long userId);

    @Query("""
                SELECT cm.conversationId
                FROM ConversationMember cm
                WHERE cm.userId IN (:userId, :targetUserId)
                GROUP BY cm.conversationId
                HAVING COUNT(DISTINCT cm.userId) = 2
            """)
    List<Long> findCommonConversationIds(Long userId, Long targetUserId);

    List<ConversationMember> findTop3ByConversationIdOrderByCtDesc(Long conversationId);
}
