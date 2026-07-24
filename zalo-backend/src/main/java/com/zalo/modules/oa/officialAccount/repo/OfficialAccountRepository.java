package com.zalo.modules.oa.officialAccount.repo;

import com.zalo.modules.oa.officialAccount.entity.OaStatus;
import com.zalo.modules.oa.officialAccount.entity.OfficialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfficialAccountRepository extends JpaRepository<OfficialAccount, Long> {
    Optional<OfficialAccount> findByIdAndStatus(Long id, OaStatus status);

    List<OfficialAccount> findAllByStatus(int status);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    boolean existsByNameAndStatus(String name, int status);

    boolean existsByNameAndStatusAndIdNot(String name, int status, Long id);

    List<OfficialAccount> findAllByIdInAndStatus(
            List<Long> ids,
            OaStatus status
    );
}
