package com.zalo.modules.conversation.service;

import com.zalo.modules.conversation.dto.respone.MemberResponse;
import com.zalo.modules.user.dto.response.UserResponse;
import com.zalo.modules.conversation.entities.ConversationMember;
import com.zalo.modules.user.entities.User;
import com.zalo.modules.conversation.entities.MemberRole;
import com.zalo.modules.user.service.UserRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MemberService {
    ConversationMemberRepository memberRepo;
    UserRepository userRepo;

    public List<MemberResponse> getMembers(Long conversationId) {
        List<ConversationMember> members = memberRepo.findByConversationIdOrderByIdDesc(conversationId);

        return convertMembersToMemberResponses(members);
    }

    public List<MemberResponse> convertMembersToMemberResponses(List<ConversationMember> members) {
        Map<Long, ConversationMember> memberMap = members.stream()
                .collect(Collectors.toMap(
                        ConversationMember::getUserId,
                        m -> m,
                        (existing, replacement) -> existing
                ));

        // Lấy tất cả addById duy nhất và không null
        List<Long> addByIds = members.stream()
                .map(ConversationMember::getAddById)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // Truy vấn danh sách User là người mời và biến thành Map<Id, User>
        Map<Long, User> addByUserMap = userRepo.findAllById(addByIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 3. Lấy danh sách User từ database dựa trên các keys của Map
        List<User> users = userRepo.findAllById(memberMap.keySet());

        // 4. Map danh sách User sang MemberResponse, gán role lấy từ Map
        return users.stream()
                .map(user -> {
                    ConversationMember memberInfo = memberMap.get(user.getId());

                    return new MemberResponse(
                            user,
                            memberInfo.getRole(),
                            addByUserMap.get(memberInfo.getAddById())
                    );
                }).sorted(Comparator
                        // Điều kiện 1: Sắp xếp theo Role (Admin -> Silverkey -> Member)
                        .comparingInt((MemberResponse m) -> getRolePriority(m.getRole()))
                        // Điều kiện 2: Nếu cùng Role, sắp xếp theo tên (A -> Z)
                        .thenComparing(UserResponse::getUsername, Comparator.nullsLast(String::compareTo))
                )
                .toList();
    }

    private int getRolePriority(MemberRole role) {
        if (role == null) return 3; // Mặc định thấp nhất
        return switch (role) {
            case GOLDEN_KEY -> 1;
            case SILVER_KEY -> 2;
            case MEMBER -> 3;
            default -> 4;
        };
    }
}
