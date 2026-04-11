package com.zalo.modules.conversation;

import com.zalo.common.configuration.anotation.conversationMember.CheckConversationMember;
import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.common.filter.ConversationFilter;
import com.zalo.modules.conversation.dto.request.CreateGroupRequest;
import com.zalo.modules.conversation.dto.respone.ConversationInfoResponse;
import com.zalo.modules.conversation.dto.respone.ConversationResponse;
import com.zalo.modules.conversation.entities.Conversation;
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

    // @GetMapping("/{id}")
    // public ConversationResponse get(@PathVariable Long id) {
    //     return new ConversationResponse(conversationService.findByIdWithRelationShip(id));
    // }

    @GetMapping("/{id}/info")
    public ConversationInfoResponse getInfo(@CurrentUser User user, @PathVariable Long id) {
        return conversationService.getInfo(id, user.getId());
    }

    @GetMapping("/get-groups")
    public List<ConversationResponse> getGroups(@CurrentUser User user) {
        return conversationService.getGroups(user.getId());
    }

    @GetMapping("/by-code/{code}")
    public ConversationResponse getByCode(@PathVariable String code) {
        ConversationResponse conv = new ConversationResponse(conversationService.getByInviteCode(code), "owner", "avatar");
        conversationService.setTop3Members(conv);
        return conv;
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
}
