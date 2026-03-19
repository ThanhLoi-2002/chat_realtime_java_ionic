package com.zalo.controller;

import com.zalo.configuration.anotation.CurrentUser;
import com.zalo.dto.request.Conversation.CreateGroupRequest;
import com.zalo.model.Conversation;
import com.zalo.model.ConversationMember;
import com.zalo.model.User;
import com.zalo.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    @PostMapping("/private")
    public ResponseEntity<Conversation> createPrivate(@RequestParam Long me, @RequestParam Long other) {
        Conversation conv = conversationService.createPrivateConversation(me, other);
        return ResponseEntity.ok(conv);
    }

    @PostMapping("/group")
    public ResponseEntity<Conversation> createGroup(@CurrentUser User user, @RequestParam CreateGroupRequest dto) {
        Conversation conv = conversationService.createGroupConversation(user.getId(), dto);
        return ResponseEntity.ok(conv);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conversation> get(@PathVariable Long id) {
        return conversationService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<ConversationMember>> members(@PathVariable Long id) {
        return ResponseEntity.ok(conversationService.getMembers(id));
    }
}
