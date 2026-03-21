package com.zalo.repository;

import com.zalo.model.Conversation;
import com.zalo.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("""
    SELECT m
    FROM Message m
    LEFT JOIN FETCH m.sender
    LEFT JOIN FETCH m.replyToMessage
    WHERE m.conversationId = :conversationId AND m.stt = 1
    ORDER BY m.ct ASC
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
    WHERE m.conversationId = :conversationId AND m.id = :id AND m.stt = 1
""")
    Optional<Message> findOneWithRelationShip(
            @Param("id") Long id,
            @Param("conversationId") Long conversationId
    );
}
