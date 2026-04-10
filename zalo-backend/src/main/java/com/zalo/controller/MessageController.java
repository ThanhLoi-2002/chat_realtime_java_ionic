package com.zalo.controller;

import com.zalo.common.configuration.anotation.CheckConversationMember;
import com.zalo.common.configuration.anotation.CurrentUser;
import com.zalo.common.filter.MessageFilter;
import com.zalo.dto.request.Message.AddReactionRequest;
import com.zalo.dto.request.Message.CreateMessageRequest;
import com.zalo.dto.response.Message.MessageResponse;
import com.zalo.modules.user.entities.User;
import com.zalo.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/conversations/{conversationId}/messages")
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    @CheckConversationMember
    public void send(@PathVariable Long conversationId, @CurrentUser User user, @RequestBody CreateMessageRequest dto) throws IOException {
        messageService.sendMessage(conversationId, user.getId(), dto);
    }

    @GetMapping
    @CheckConversationMember
    public Page<MessageResponse> fetchMessages(@PathVariable Long conversationId, @ModelAttribute MessageFilter filter, @CurrentUser User user) {
        return messageService.fetchMessages(conversationId, filter);
    }

    @PostMapping("/{messageId}/read")
    @CheckConversationMember
    public void markRead(@PathVariable Long conversationId, @PathVariable Long messageId, @CurrentUser User user) {
        messageService.markRead(conversationId, user.getId(), messageId);
    }

    @DeleteMapping("/{messageId}")
    @CheckConversationMember
    public void delete(@PathVariable Long messageId, @CurrentUser User user) {
        messageService.delete(messageId, user.getId());
    }

    @PostMapping("/reaction/add")
    @CheckConversationMember
    public void addReaction(@PathVariable Long conversationId, @CurrentUser User user, @RequestBody AddReactionRequest dto) {
        messageService.addReaction(conversationId, user.getId(), dto);
    }

    @DeleteMapping("/reaction/remove-all")
    @CheckConversationMember
    public void removeAll(@PathVariable Long conversationId, @CurrentUser User user, @RequestParam Long messageId) {
        messageService.removeAllReactionByUserId(conversationId, messageId, user.getId());
    }
}
