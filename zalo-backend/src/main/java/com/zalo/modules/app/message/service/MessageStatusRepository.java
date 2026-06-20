package com.zalo.modules.app.message.service;

import com.zalo.modules.app.message.entity.MessageStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageStatusRepository extends JpaRepository<MessageStatus, Long> {
    List<MessageStatus> findByMessageId(Long messageId);

    Optional<MessageStatus> findByMessageIdAndUserId(Long messageId, Long userId);

    // Lấy danh sách trạng thái kèm thông tin User (Join thủ công bằng JPQL)
    @EntityGraph(attributePaths = {"user"})
    List<MessageStatus> findStatusByMessageId(Long messageId);
}