package com.zalo.repository;

import com.zalo.model.ConversationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationMemberRepository extends JpaRepository<ConversationMember, Long> {
    List<ConversationMember> findByUserId(Long userId);
    List<ConversationMember> findByConversationId(Long conversationId);
    Optional<ConversationMember> findByConversationIdAndUserId(Long conversationId, Long userId);

    @Query("SELECT cm.conversationId FROM ConversationMember cm WHERE cm.userId = :userId")
    List<Long> findConversationIdsByUserId(@Param("userId") Long userId);
}
