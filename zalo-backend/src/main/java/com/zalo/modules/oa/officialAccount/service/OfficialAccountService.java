package com.zalo.modules.oa.officialAccount.service;

import com.zalo.common.service.CodeGeneratorService;
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
    private final CodeGeneratorService codeGeneratorService;

    public OfficialAccount getById(Long oaId) {
        return officialAccountRepository
                .findByIdAndStatus(oaId, OaStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Official Account not found"));
    }

    private OfficialAccountMember getMember(Long oaId, Long userId) {
        return memberRepository
                .findByOaIdAndUserIdAndStatus(oaId, userId, OaMember.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không thuộc OA này"));
    }

    public void checkMember(Long oaId, Long userId) {
        getMember(oaId, userId);
    }

    private void checkOwner(Long oaId, Long userId) {
        if (!isOwner(oaId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ Owner được phép thực hiện");
        }
    }

    private void checkAdmin(Long oaId, Long userId) {
        if (!isAdmin(oaId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền");
        }
    }

    public OfficialAccount create(Long userId, CreateOaRequest request) {
        if (officialAccountRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tên OA đã tồn tại");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User không tồn tại"));

        OfficialAccount oa = OfficialAccount.builder()
                .name(request.getName())
                .code(codeGeneratorService.generate())
                .avatar(request.getAvatar())
                .cover(request.getCover())
                .description(request.getDescription())
                .category(request.getCategory())
                .status(OaStatus.ACTIVE)
                .verified(OaVerified.UNVERIFIED)
                .build();
        oa.setCu(user.getId());
        oa = officialAccountRepository.save(oa);

        memberRepository.save(OfficialAccountMember.builder()
                .oaId(oa.getId())
                .userId(user.getId())
                .role(OaRole.OWNER)
                .status(OaMember.ACTIVE)
                .joinedAt(LocalDateTime.now())
                .build());

        if (user.getIsOa() == 0) {
            user.setIsOa(1);
            userRepository.save(user);
        }

        return oa;
    }

    public OfficialAccount update(UserPayload currentUser, Long oaId, UpdateOaRequest request) {
        checkAdmin(oaId, currentUser.getId());

        OfficialAccount oa = getById(oaId);

        if (officialAccountRepository.existsByNameAndIdNot(request.getName(), oaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tên OA đã tồn tại");
        }

        oa.setName(request.getName());
        oa.setAvatar(request.getAvatar());
        oa.setCover(request.getCover());
        oa.setDescription(request.getDescription());
        oa.setCategory(request.getCategory());

        return officialAccountRepository.save(oa);
    }

    public void delete(UserPayload currentUser, Long oaId) {
        checkOwner(currentUser.getId(), oaId); // Hoặc checkOwner(oaId, currentUser.getId()) tùy theo thứ tự param cũ

        OfficialAccount oa = getById(oaId);
        oa.setStatus(OaStatus.DELETED);
        officialAccountRepository.save(oa);

        List<OfficialAccountMember> members = memberRepository.findAllByOaIdAndStatus(oaId, OaStatus.ACTIVE);
        members.forEach(member -> member.setStatus(OaMember.REMOVED));
        memberRepository.saveAll(members);
    }

    public List<OfficialAccount> getMyOasActive(UserPayload currentUser) {
        List<OfficialAccountMember> members = memberRepository.findAllByUserIdAndStatus(currentUser.getId(), OaMember.ACTIVE);

        if (members.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> ids = members.stream()
                .map(OfficialAccountMember::getOaId)
                .toList();

        return officialAccountRepository.findAllByIdInAndStatus(ids, OaStatus.ACTIVE);
    }

    public List<OfficialAccount> getMyOas(UserPayload currentUser) {
        List<OfficialAccountMember> members = memberRepository.findAllByUserIdAndStatus(currentUser.getId(), OaMember.ACTIVE);

        if (members.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> ids = members.stream()
                .map(OfficialAccountMember::getOaId)
                .toList();

        return officialAccountRepository.findAllById(ids);
    }

    public boolean hasPermission(Long oaId, Long userId) {
        return memberRepository.existsByOaIdAndUserIdAndStatus(oaId, userId, OaStatus.ACTIVE);
    }

    public boolean isOwner(Long oaId, Long userId) {
        return memberRepository.findByOaIdAndUserIdAndStatus(oaId, userId, OaMember.ACTIVE)
                .map(e -> OaRole.OWNER.equals(e.getRole()))
                .orElse(false);
    }

    public boolean isAdmin(Long oaId, Long userId) {
        return memberRepository.findByOaIdAndUserIdAndStatus(oaId, userId, OaMember.ACTIVE)
                .map(e -> OaRole.OWNER.equals(e.getRole()) || OaRole.ADMIN.equals(e.getRole()))
                .orElse(false);
    }

//    public User toggleOA(Long userId) {
//        UserFilter filter = new UserFilter();
//        filter.setId(userId);
//        Optional<User> userExisted = findOne(filter);
//
//        if (userExisted.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound");
//        }
//
//        if(userExisted.get().getIsOa() == 1)
//        {
//            return userExisted.get();
//        }
//
//        userExisted.get().setIsOa(1);
//        userRepository.save(userExisted.get());
//
//        // tạo oa đầu tiên
//
//        return userExisted.get();
//    }
}