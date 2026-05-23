package com.zalo.modules.joinGroupRequest.service;

import com.zalo.modules.joinGroupRequest.entity.JoinGroupRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JoinGroupRequestRepository extends JpaRepository<JoinGroupRequest, Long> {
    @EntityGraph(attributePaths = {"createdBy"})
    List<JoinGroupRequest> findByConversationId(Long convId);
}
