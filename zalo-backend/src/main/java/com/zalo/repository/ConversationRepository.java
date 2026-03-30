package com.zalo.repository;

import com.zalo.model.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long>, JpaSpecificationExecutor<Conversation> {
    // Find a private conversation that includes two users.
    // Since we don't have relationships, we search conversation_members table via native query.
    @Query(value = """
            select c.* from conversation c
            where c.type = :type
              and exists (
                select 1 from conversation_member m
                where m.conversation_id = c.id and m.user_id = ?1
              )
              and exists (
                select 1 from conversation_member m2
                where m2.conversation_id = c.id and m2.user_id = ?2
              )
              and c.stt = 1
            limit 1
            """, nativeQuery = true)
    Optional<Conversation> findPrivateBetween(Long userId1, Long userId2, String type);

    @Query("""
                SELECT c
                FROM Conversation c
                LEFT JOIN FETCH c.lastMessage m
                LEFT JOIN FETCH c.recipient r
                LEFT JOIN FETCH m.sender
                LEFT JOIN FETCH c.createdBy cr
                WHERE (:ids IS NULL OR c.id IN :ids)
                AND (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
                AND c.stt = 1 AND c.lastMessageId IS NOT NULL
                ORDER BY m.ct DESC
            """)
    Page<Conversation> findAllWithRelationShip(
            @Param("ids") List<Long> ids,
            @Param("name") String name,
            Pageable pageable
    );

    @Query("""
                SELECT c
                FROM Conversation c
                LEFT JOIN FETCH c.lastMessage m
                LEFT JOIN FETCH c.recipient r
                LEFT JOIN FETCH m.sender
                LEFT JOIN FETCH c.createdBy cr
                WHERE c.id = :id AND c.stt = 1
            """)
    Optional<Conversation> findOneWithRelationShipById(
            @Param("id") Long id
    );
}
