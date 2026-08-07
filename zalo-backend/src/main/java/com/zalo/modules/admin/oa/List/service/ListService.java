package com.zalo.modules.admin.oa.List.service;

import com.zalo.common.filter.OaFilter;
import com.zalo.modules.oa.officialAccount.entity.OaStatus;
import com.zalo.modules.oa.officialAccount.entity.OfficialAccount;
import com.zalo.modules.oa.officialAccount.repo.OfficialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ListService {
    private final OfficialAccountRepository officialAccountRepository;

    public Page<OfficialAccount> getAll(OaFilter filter) {
        Pageable pageable = filter.toScrollable("ct", Sort.Direction.DESC);
        return officialAccountRepository.findAll(filter.toSpecification(), pageable);
    }

    public OfficialAccount updateStatus (Long id, OaStatus status, Long userId) {
        OfficialAccount oa = officialAccountRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
        oa.setStatus(status);
        oa.setEu(userId);
        return officialAccountRepository.save(oa);
    }
}
