package com.zalo.repository;

import com.zalo.model.Conversation;
import com.zalo.model.Message;
import com.zalo.model.User;
import com.zalo.model.enums.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {
    @Query("""
                SELECT m
                FROM Message m
                WHERE m.conversationId = :conversationId
                ORDER BY m.ct DESC
            """)
    Page<Message> findAllWithRelationShip(
            @Param("conversationId") Long conversationId,
            Pageable pageable
    );

    @Query("""
                SELECT m
                FROM Message m
                LEFT JOIN FETCH m.sender
                LEFT JOIN FETCH m.replyToMessage
                WHERE m.conversationId = :conversationId AND m.id = :id
            """)
    Optional<Message> findOneWithRelationShip(
            @Param("id") Long id,
            @Param("conversationId") Long conversationId
    );

    Page<Message> findByConversationIdAndContentTypeAndSttOrderByCtDesc(
            Long conversationId,
            MessageType type,
            Integer stt,
            Pageable pageable
    );
}
