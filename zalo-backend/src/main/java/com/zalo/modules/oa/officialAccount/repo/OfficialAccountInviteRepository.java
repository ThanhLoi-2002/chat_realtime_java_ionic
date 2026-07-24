package com.zalo.modules.oa.officialAccount.repo;

import com.zalo.modules.oa.officialAccount.entity.OfficialAccountInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfficialAccountInviteRepository extends JpaRepository<OfficialAccountInvite, Long> {

    Optional<OfficialAccountInvite> findByToken(String token);

    List<OfficialAccountInvite> findAllByPhoneAndStatus(String phone, int status);
}
