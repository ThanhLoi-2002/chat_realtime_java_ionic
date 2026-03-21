package com.zalo.controller;

import com.zalo.configuration.anotation.CurrentUser;
import com.zalo.dto.filter.ConversationFilter;
import com.zalo.dto.filter.UserFilter;
import com.zalo.dto.request.Conversation.CreateGroupRequest;
import com.zalo.dto.response.User.UserResponse;
import com.zalo.dto.response.conversation.ConversationResponse;
import com.zalo.dto.response.conversation.ConversationViewResponse;
import com.zalo.mapper.UserMapper;
import com.zalo.model.Conversation;
import com.zalo.model.ConversationMember;
import com.zalo.model.User;
import com.zalo.service.ConversationService;
import com.zalo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    @PostMapping("/private")
    public ConversationResponse createPrivate(@CurrentUser User user, @RequestParam Long otherId) {
        Conversation conv = conversationService.createPrivateConversation(user.getId(), otherId);
        return new ConversationResponse(conv);
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
        Page<ConversationResponse> conversations = conversationService.findAll(user.getId(), filter);
        return conversations;
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
