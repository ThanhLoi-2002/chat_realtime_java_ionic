package com.zalo.repository;

import com.zalo.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    // Find a private conversation that includes two users.
    // Since we don't have relationships, we search conversation_members table via native query.
    @Query(value = """
        select c.* from conversations c
        where c.type = :type
          and exists (
            select 1 from conversation_members m
            where m.conversationId = c.id and m.userId = ?1
          )
          and exists (
            select 1 from conversation_members m2
            where m2.conversationId = c.id and m2.userId = ?2
          )
        limit 1
        """, nativeQuery = true)
    Optional<Conversation> findPrivateBetween(Long userId1, Long userId2, String type);
}
