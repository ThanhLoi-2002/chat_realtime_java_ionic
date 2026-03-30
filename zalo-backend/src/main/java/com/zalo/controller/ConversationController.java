package com.zalo.controller;

import com.zalo.configuration.G;
import com.zalo.configuration.anotation.CurrentUser;
import com.zalo.dto.filter.ConversationFilter;
import com.zalo.dto.request.Conversation.CreateGroupRequest;
import com.zalo.dto.response.Conversation.ConversationResponse;
import com.zalo.mapper.ConversationMapper;
import com.zalo.model.Conversation;
import com.zalo.model.ConversationMember;
import com.zalo.model.User;
import com.zalo.service.ConversationService;
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
    public Conversation createGroup(@CurrentUser User user, @RequestParam CreateGroupRequest dto) {
        Conversation conv = conversationService.createGroupConversation(user.getId(), dto);
        return conv;
    }

    @GetMapping
    public Page<ConversationResponse> searchConversations(
            @CurrentUser User user,
            @ModelAttribute ConversationFilter filter
    ) {
        Page<Conversation> conversations = conversationService.findAll(user.getId(), filter);

        return conversations.map(e -> new ConversationResponse(e, "recipient", "lastMessage", "createdBy"));
    }

    @GetMapping("/{id}")
    public ConversationResponse get(@PathVariable Long id) {
        return new ConversationResponse(conversationService.findByIdWithRelationShip(id));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<ConversationMember>> members(@PathVariable Long id) {
        return ResponseEntity.ok(conversationService.getMembers(id));
    }
}
