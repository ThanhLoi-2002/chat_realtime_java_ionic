package com.zalo.modules.conversation;

import com.zalo.common.configuration.anotation.conversationMember.CheckConversationMember;
import com.zalo.common.configuration.anotation.conversationMember.RequireMemberRole;
import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.common.filter.ConversationFilter;
import com.zalo.modules.conversation.dto.request.CreateGroupRequest;
import com.zalo.modules.conversation.dto.respone.ConversationInfoResponse;
import com.zalo.modules.conversation.dto.respone.ConversationResponse;
import com.zalo.modules.conversation.entities.Conversation;
import com.zalo.modules.conversation.entities.MemberRole;
import com.zalo.modules.media.dtos.requests.MediaRequest;
import com.zalo.modules.user.entities.User;
import com.zalo.modules.conversation.service.ConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    @PostMapping("/private")
    public ConversationResponse createPrivate(@CurrentUser User user, @RequestParam Long otherId) {
        Conversation conv = conversationService.createPrivateConversation(user.getId(), otherId);
        return new ConversationResponse(conv, "recipient", "lastMessage", "createdBy");
    }

    @PostMapping("/group")
    public void createGroup(@CurrentUser User user, @RequestBody @Valid CreateGroupRequest dto) {
        dto.getParticipantIds().add(user.getId());
        conversationService.createGroupConversation(user.getId(), dto);
    }

    @GetMapping
    public Page<ConversationResponse> searchConversations(
            @CurrentUser User user,
            @ModelAttribute ConversationFilter filter
    ) {
        return conversationService.findAll(user.getId(), filter);
    }

    @GetMapping("/no-pagination")
    public List<ConversationResponse> findAllNoPagination(
            @CurrentUser User user
    ) {
        return conversationService.findAllNoPagination(user.getId());
    }

    @GetMapping("/{id}")
    public Long getReadLastMessageId(@PathVariable Long id, @CurrentUser User user) {
        return conversationService.getReadLastMessageId(id, user.getId());
    }

    @GetMapping("/{id}/info")
    public ConversationInfoResponse getInfo(@CurrentUser User user, @PathVariable Long id) {
        return conversationService.getInfo(id, user.getId());
    }

    @GetMapping("/{id}/get-one")
    public ConversationResponse getOne(@CurrentUser User user, @PathVariable Long id) {
        return conversationService.findByIdWithRelationShipAndMember(id);
    }

    @GetMapping("/get-groups")
    public List<ConversationResponse> getGroups(@CurrentUser User user) {
        return conversationService.getGroups(user.getId());
    }

    @GetMapping("/by-code/{code}")
    public ConversationResponse getByCode(@PathVariable String code) {
        return conversationService.getByInviteCode(code);
    }

    @PostMapping("/get-by-userIds")
    public List<ConversationResponse> findByUserIdsAndTypePrivate(@RequestBody List<Long> userIds, @CurrentUser User user) {
        return conversationService.findByUserIdsAndTypePrivate(userIds, user.getId())
                .stream().map(c -> new ConversationResponse(c, "recipient", "createdBy")).toList();
    }

    @PostMapping("/{conversationId}/add-members")
    @CheckConversationMember
    public void addMembers(@PathVariable Long conversationId, @CurrentUser User user, @RequestBody List<Long> memberIds) {
        conversationService.addMembersToGroups(conversationId, user.getId(), memberIds);
    }

    @DeleteMapping("/{conversationId}/leave-group")
    @CheckConversationMember
    public void leaveGroup(@PathVariable Long conversationId, @CurrentUser User user) {
        conversationService.leaveGroup(conversationId, user.getId());
    }

    @DeleteMapping("/{conversationId}/kick-member/{memberId}")
    @RequireMemberRole(memberRoles = {MemberRole.GOLDEN_KEY, MemberRole.SILVER_KEY})
    public void kickMember(@PathVariable Long conversationId, @CurrentUser User user, @PathVariable Long memberId) {
        conversationService.kickMember(conversationId, user.getId(), memberId);
    }

    @DeleteMapping("/{conversationId}/disband-group")
    @CheckConversationMember
    public void disbandGroup(@PathVariable Long conversationId, @CurrentUser User user) {
        conversationService.disbandGroup(conversationId);
    }

    @PutMapping("/{conversationId}/update-avatar")
    @CheckConversationMember
    public void updateAvatar(@PathVariable Long conversationId, @CurrentUser User user, @RequestBody MediaRequest media) {
        conversationService.updateAvatar(conversationId, user.getId(), media);
    }

    @PutMapping("/{conversationId}/update-name")
    @CheckConversationMember
    public void updateName(@PathVariable Long conversationId, @CurrentUser User user, @RequestBody String name) {
        conversationService.updateGroupName(conversationId, user.getId(), name);
    }

    @PutMapping("/{conversationId}/ordain-silver-key/{memberId}")
    @RequireMemberRole(memberRoles = {MemberRole.GOLDEN_KEY})
    public void ordainSilverKey(@PathVariable Long conversationId, @CurrentUser User user, @PathVariable Long memberId) {
        conversationService.ordainSilverKey(conversationId, user.getId(), memberId);
    }

    @PutMapping("/{conversationId}/revoke-silver-key/{memberId}")
    @RequireMemberRole(memberRoles = {MemberRole.GOLDEN_KEY})
    public void revokeSilverKey(@PathVariable Long conversationId, @CurrentUser User user, @PathVariable Long memberId) {
        conversationService.revokeSilverKey(conversationId, user.getId(), memberId);
    }

    @PutMapping("/{conversationId}/transfer-golden-key/{memberId}")
    @RequireMemberRole(memberRoles = {MemberRole.GOLDEN_KEY})
    public void transferGoldenKey(@PathVariable Long conversationId, @CurrentUser User user, @PathVariable Long memberId) {
        conversationService.transferGoldenKey(conversationId, user.getId(), memberId);
    }
}
