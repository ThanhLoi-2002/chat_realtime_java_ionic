package com.zalo.modules.message.service;

import com.zalo.modules.message.entity.MessageStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageStatusRepository extends JpaRepository<MessageStatus, Long> {
    List<MessageStatus> findByMessageId(Long messageId);

    Optional<MessageStatus> findByMessageIdAndUserId(Long messageId, Long userId);

    // Lấy danh sách trạng thái kèm thông tin User (Join thủ công bằng JPQL)
    @EntityGraph(attributePaths = {"user"})
    List<MessageStatus> findStatusByMessageId(Long messageId);
}