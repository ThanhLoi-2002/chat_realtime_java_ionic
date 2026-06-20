package com.zalo.modules.app.message.service;

import com.zalo.modules.app.message.entity.MessagePin;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessagePinRepository extends JpaRepository<MessagePin, Long> {
    @EntityGraph(attributePaths = {"createdBy", "message", "message.sender"})
    List<MessagePin> findByConversationIdAndIsActiveOrderByCtDesc(Long conversationId, int isActive);

    @EntityGraph(attributePaths = {"createdBy", "message", "message.sender"})
    List<MessagePin> findByConversationIdOrderByCtDesc(Long conversationId);

    @Query("""
                   SELECT mp
                   FROM MessagePin mp
                   LEFT JOIN FETCH mp.createdBy
                   LEFT JOIN FETCH mp.message
                   LEFT JOIN FETCH mp.message.sender
                   WHERE mp.id = :id
            """)
//    @EntityGraph(attributePaths = {"createdBy", "message", "message.sender"})
    MessagePin findOneWithRelationshipById(Long id);
}
