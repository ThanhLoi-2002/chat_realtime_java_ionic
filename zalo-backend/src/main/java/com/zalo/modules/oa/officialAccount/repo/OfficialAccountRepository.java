package com.zalo.modules.oa.officialAccount.repo;

import com.zalo.modules.oa.officialAccount.entity.OaStatus;
import com.zalo.modules.oa.officialAccount.entity.OfficialAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface OfficialAccountRepository extends JpaRepository<OfficialAccount, Long>, JpaSpecificationExecutor<OfficialAccount> {
    Optional<OfficialAccount> findByIdAndStatus(Long id, OaStatus status);
    Optional<OfficialAccount> findByCodeAndStatus(String code, OaStatus status);

    boolean existsByName(String name);

    boolean existsByNameAndIdNotAndCuNot(String name, Long id, Long cu);

    List<OfficialAccount> findAllByIdInAndStatus(
            List<Long> ids,
            OaStatus status
    );
}
