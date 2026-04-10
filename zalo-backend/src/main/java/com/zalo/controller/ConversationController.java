package com.zalo.controller;

import com.zalo.configuration.anotation.CheckConversationMember;
import com.zalo.configuration.anotation.CurrentUser;
import com.zalo.dto.filter.ConversationFilter;
import com.zalo.dto.request.Conversation.CreateGroupRequest;
import com.zalo.dto.response.Conversation.ConversationInfoResponse;
import com.zalo.dto.response.Conversation.ConversationResponse;
import com.zalo.model.Conversation;
import com.zalo.model.User;
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

    @GetMapping("/{id}")
    public ConversationResponse get(@PathVariable Long id) {
        return new ConversationResponse(conversationService.findByIdWithRelationShip(id));
    }

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
        ConversationResponse conv = new ConversationResponse(conversationService.getByInviteCode(code), "owner");
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
}
