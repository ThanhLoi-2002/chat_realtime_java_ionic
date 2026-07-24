package com.zalo.modules.oa.officialAccount.repo;

import com.zalo.modules.oa.officialAccount.entity.OaStatus;
import com.zalo.modules.oa.officialAccount.entity.OfficialAccountMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfficialAccountMemberRepository extends JpaRepository<OfficialAccountMember, Long> {
    List<OfficialAccountMember> findAllByUserIdAndStatus(Long userId, OaStatus status);

    List<OfficialAccountMember> findAllByOaIdAndStatus(Long oaId, OaStatus status);

    Optional<OfficialAccountMember> findByOaIdAndUserId(Long oaId, Long userId);

    boolean existsByOaIdAndUserId(Long oaId, Long userId);

    Optional<OfficialAccountMember> findByOaIdAndUserIdAndStatus(Long oaId, Long userId, OaStatus status);

    boolean existsByOaIdAndUserIdAndStatus(Long oaId, Long userId, OaStatus status);

    long countByOaIdAndStatus(Long oaId, OaStatus status);

    void deleteAllByOaId(Long oaId);
}
