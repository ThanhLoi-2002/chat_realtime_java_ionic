package com.zalo.modules.conversation.service;

import com.zalo.modules.conversation.dto.IsMentionDto;
import com.zalo.modules.conversation.entities.Conversation;
import com.zalo.modules.conversation.entities.ConversationType;
import com.zalo.modules.conversation.dto.UnreadDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long>, JpaSpecificationExecutor<Conversation> {
    @EntityGraph(attributePaths = {"lastMessage", "lastMessage.sender", "owner", "avatar"})
    Optional<Conversation> findByInviteCode(String inviteCode);

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
                LEFT JOIN FETCH c.avatar a
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
                LEFT JOIN FETCH c.avatar a
                LEFT JOIN FETCH c.recipient r
                LEFT JOIN FETCH m.sender
                LEFT JOIN FETCH c.createdBy cr
                WHERE c.id = :id AND c.stt = 1
            """)
    Optional<Conversation> findOneWithRelationShipById(
            @Param("id") Long id
    );

    @Query("""
                SELECT c.id AS conversationId, COUNT(m.id) AS unreadCount
                FROM Conversation c
                LEFT JOIN ConversationMember cm
                    ON cm.conversationId = c.id AND cm.userId = :userId
                LEFT JOIN Message m
                    ON m.conversationId = c.id AND 
                    m.id > cm.lastReadMessageId AND 
                    m.contentType != 'SYSTEM'
                WHERE (:ids IS NULL OR c.id IN :ids)
                GROUP BY c.id
            """)
    List<UnreadDto> countUnread(
            @Param("ids") List<Long> ids,
            @Param("userId") Long userId
    );

    @Query("""
                SELECT COUNT(m.id) AS unreadCount
                FROM Conversation c
                LEFT JOIN ConversationMember cm
                    ON cm.conversationId = c.id AND cm.userId = :userId
                LEFT JOIN Message m
                    ON m.conversationId = c.id AND 
                    m.id > cm.lastReadMessageId AND 
                    m.contentType != 'SYSTEM'
                WHERE c.id = :id
                GROUP BY c.id
                LIMIT 1
            """)
    Long countUnread(
            @Param("id") Long id,
            @Param("userId") Long userId
    );

    @EntityGraph(attributePaths = {"avatar"})
    List<Conversation> findByIdInAndType(List<Long> ids, ConversationType type);

    @Query(value = """
            SELECT 
                c.id AS conversationId, 
                CASE WHEN EXISTS (
                    SELECT 1 FROM message m 
                    WHERE m.conversation_id = c.id 
                      AND m.id > cm.last_read_message_id 
                      AND m.content_type != 'SYSTEM'
                      AND m.content LIKE CONCAT('%[mention:', :userId, ']%')
                ) THEN true ELSE false END AS isMention
            FROM conversation c
            JOIN conversation_member cm ON cm.conversation_id = c.id
            WHERE cm.user_id = :userId
              AND c.id IN :ids
            """, nativeQuery = true)
    List<IsMentionDto> checkMentionsInUnread(
            @Param("ids") List<Long> ids,
            @Param("userId") Long userId
    );

    @Query(value = """
            select c from Conversation c
            LEFT JOIN FETCH c.recipient r
            LEFT JOIN FETCH c.createdBy cr
            where c.type = :type
              and ((c.recipientId in :userIds and c.cu = :userId) or (c.recipientId = :userId and c.cu in :userIds))
              and c.stt = 1
            """)
    List<Conversation> findByUserIdsAndTypePrivate(
            @Param("userIds") List<Long> userIds,
            @Param("userId") Long userId,
            @Param("type") ConversationType type
    );
}
