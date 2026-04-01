package com.zalo.controller;

import com.zalo.configuration.G;
import com.zalo.configuration.anotation.CurrentUser;
import com.zalo.dto.filter.ConversationFilter;
import com.zalo.dto.request.Conversation.CreateGroupRequest;
import com.zalo.dto.response.Conversation.ConversationInfoResponse;
import com.zalo.dto.response.Conversation.ConversationResponse;
import com.zalo.dto.response.User.UserResponse;
import com.zalo.mapper.ConversationMapper;
import com.zalo.model.Conversation;
import com.zalo.model.ConversationMember;
import com.zalo.model.User;
import com.zalo.service.ConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;
    private final ConversationMapper conversationMapper;

    @PostMapping("/private")
    public ConversationResponse createPrivate(@CurrentUser User user, @RequestParam Long otherId) {
        Conversation conv = conversationService.createPrivateConversation(user.getId(), otherId);
        return new ConversationResponse(conv, "recipient", "lastMessage", "createdBy");
    }

    @PostMapping("/group")
    public void createGroup(@CurrentUser User user, @RequestBody @Valid CreateGroupRequest dto) {
        dto.getParticipantIds().add(user.getId());
        System.out.println(G.toJson(dto));
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

    @GetMapping("/{id}/members")
    public List<UserResponse> members(@PathVariable Long id) {
        return conversationService.getMembers(id).stream().map(UserResponse::new).toList();
    }

    @GetMapping("/{id}/info")
    public ConversationInfoResponse getInfo(@CurrentUser User user, @PathVariable Long id) {
        return conversationService.getInfo(id, user.getId());
    }
}
