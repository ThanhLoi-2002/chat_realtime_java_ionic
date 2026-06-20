package com.zalo.modules.app.joinGroupRequest.service;

import com.zalo.modules.app.joinGroupRequest.entity.JoinGroupRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JoinGroupRequestRepository extends JpaRepository<JoinGroupRequest, Long> {
    @EntityGraph(attributePaths = {"createdBy"})
    List<JoinGroupRequest> findByConversationId(Long convId);

    Optional<JoinGroupRequest> findByConversationIdAndCu(Long convId, Long userId);
}
