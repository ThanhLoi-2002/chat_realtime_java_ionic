package com.zalo.modules.joinGroupRequest.service;

import com.zalo.modules.joinGroupRequest.entity.JoinGroupRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JoinGroupRequestRepository extends JpaRepository<JoinGroupRequest, Long> {
}
