package com.zalo.modules.message.service;

import com.zalo.modules.message.entity.Message;
import com.zalo.modules.message.entity.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {
    @EntityGraph(attributePaths = {"sender", "replyToMessage"})
    Page<Message> findAll(Specification<Message> spec, Pageable pageable);

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
