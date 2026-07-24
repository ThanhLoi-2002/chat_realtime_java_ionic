package com.zalo.modules.oa.officialAccount.service;

import com.zalo.modules.admin.system.user.dto.response.UserPayload;
import com.zalo.modules.admin.system.user.entities.User;
import com.zalo.modules.admin.system.user.service.UserRepository;
import com.zalo.modules.oa.officialAccount.dto.request.CreateOaRequest;
import com.zalo.modules.oa.officialAccount.dto.request.UpdateOaRequest;
import com.zalo.modules.oa.officialAccount.entity.*;
import com.zalo.modules.oa.officialAccount.repo.OfficialAccountMemberRepository;
import com.zalo.modules.oa.officialAccount.repo.OfficialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OfficialAccountService {
    private final OfficialAccountRepository officialAccountRepository;

    private final OfficialAccountMemberRepository memberRepository;

    private final UserRepository userRepository;

    public OfficialAccount getById(Long oaId) {

        return officialAccountRepository
                .findByIdAndStatus(oaId, OaStatus.ACTIVE)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.FORBIDDEN, "Official Account not found"));
    }

    private OfficialAccountMember getMember(Long oaId, Long userId) {
        return memberRepository
                .findByOaIdAndUserIdAndStatus(
                        oaId,
                        userId,
                        OaStatus.ACTIVE
                )
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không thuộc OA này"));
    }

    public void checkMember(Long oaId, Long userId) {
        getMember(oaId, userId);
    }

    private void checkOwner(Long oaId, Long userId) {

        OfficialAccountMember member = getMember(oaId, userId);

        if (!OaRole.OWNER.equals(member.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ Owner được phép thực hiện");
        }
    }

    private void checkAdmin(Long oaId,
                            Long userId) {

        OfficialAccountMember member = getMember(oaId, userId);

        if (!OaRole.OWNER.equals(member.getRole())
                && !OaRole.ADMIN.equals(member.getRole())) {

            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền");
        }
    }

    public OfficialAccount create(UserPayload currentUser, CreateOaRequest request) {

        if (officialAccountRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tên OA đã tồn tại");
        }

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User không tồn tại"));

        OfficialAccount oa = OfficialAccount.builder()
                .name(request.getName())
                .avatar(request.getAvatar())
                .cover(request.getCover())
                .description(request.getDescription())
                .category(request.getCategory())
                .status(OaStatus.ACTIVE)
                .verified(OaVerified.UNVERIFIED)
                .build();
        oa.setCu(user.getId());

        oa = officialAccountRepository.save(oa);

        OfficialAccountMember owner = OfficialAccountMember.builder()
                .oaId(oa.getId())
                .userId(user.getId())
                .role(OaRole.OWNER)
                .status(OaMember.ACTIVE)
                .joinedAt(LocalDateTime.now())
                .build();

        memberRepository.save(owner);

        /*
            đánh dấu user đã có OA
         */

        if (user.getIsOa() == 0) {

            user.setIsOa(1);

            userRepository.save(user);
        }

        return oa;
    }

    public OfficialAccount update(UserPayload currentUser,
                                  Long oaId,
                                  UpdateOaRequest request) {

        checkAdmin(oaId, currentUser.getId());

        OfficialAccount oa = getById(oaId);

        if (officialAccountRepository.existsByNameAndIdNot(
                request.getName(),
                oaId
        )) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tên OA đã tồn tại");
        }

        oa.setName(request.getName());
        oa.setAvatar(request.getAvatar());
        oa.setCover(request.getCover());
        oa.setDescription(request.getDescription());
        oa.setCategory(request.getCategory());

        return officialAccountRepository.save(oa);
    }

    public void delete(UserPayload currentUser,
                       Long oaId) {

        checkOwner(oaId, currentUser.getId());

        OfficialAccount oa = getById(oaId);

        oa.setStatus(OaStatus.DELETED);

        officialAccountRepository.save(oa);

        List<OfficialAccountMember> members =
                memberRepository.findAllByOaIdAndStatus(
                        oaId,
                        OaStatus.ACTIVE
                );

        for (OfficialAccountMember member : members) {

            member.setStatus(OaMember.REMOVED);
        }

        memberRepository.saveAll(members);
    }

    public List<OfficialAccount> getMyOfficialAccounts(
            UserPayload currentUser
    ) {

        List<OfficialAccountMember> members =
                memberRepository.findAllByUserIdAndStatus(
                        currentUser.getId(),
                        OaStatus.ACTIVE
                );

        if (members.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> ids = members.stream()
                .map(OfficialAccountMember::getOaId)
                .toList();

        return officialAccountRepository.findAllByIdInAndStatus(
                ids,
                OaStatus.ACTIVE
        );
    }

    public boolean hasPermission(Long oaId,
                                 Long userId) {

        return memberRepository.existsByOaIdAndUserIdAndStatus(
                oaId,
                userId,
                OaStatus.ACTIVE
        );
    }

    public boolean isOwner(Long oaId,
                           Long userId) {

        return memberRepository
                .findByOaIdAndUserIdAndStatus(
                        oaId,
                        userId,
                        OaStatus.ACTIVE
                )
                .map(e -> OaRole.OWNER.equals(e.getRole()))
                .orElse(false);
    }

    public boolean isAdmin(Long oaId,
                           Long userId) {

        return memberRepository
                .findByOaIdAndUserIdAndStatus(
                        oaId,
                        userId,
                        OaStatus.ACTIVE
                )
                .map(e ->

                        OaRole.OWNER.equals(e.getRole())
                                || OaRole.ADMIN.equals(e.getRole())

                )
                .orElse(false);
    }
}
