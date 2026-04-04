package com.zalo.controller;

import com.zalo.configuration.anotation.CheckConversationMember;
import com.zalo.configuration.anotation.CurrentUser;
import com.zalo.dto.filter.MessageFilter;
import com.zalo.dto.request.Message.AddReactionRequest;
import com.zalo.dto.request.Message.CreateMessageRequest;
import com.zalo.dto.response.Message.MessageResponse;
import com.zalo.model.Message;
import com.zalo.model.MessageReaction;
import com.zalo.model.User;
import com.zalo.model.enums.MessageType;
import com.zalo.model.enums.ReactionType;
import com.zalo.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/conversations/{conversationId}/messages")
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    @CheckConversationMember
    public void send(@PathVariable Long conversationId, @CurrentUser User user, @ModelAttribute CreateMessageRequest dto) throws IOException {
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
    public void removeAll(@PathVariable Long conversationId, @CurrentUser User user, @ModelAttribute Long messageId) {
        messageService.removeAllReactionByUserId(conversationId, messageId, user.getId());
    }
}
