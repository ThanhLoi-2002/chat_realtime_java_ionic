package com.zalo.controller;

import com.zalo.configuration.anotation.CurrentUser;
import com.zalo.dto.filter.MessageFilter;
import com.zalo.dto.request.Message.CreateMessageRequest;
import com.zalo.dto.response.Message.MessageResponse;
import com.zalo.model.Message;
import com.zalo.model.User;
import com.zalo.model.enums.MessageType;
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
    public void send(@PathVariable Long conversationId,
                                        @CurrentUser User user,
                                        @ModelAttribute CreateMessageRequest dto) throws IOException {
        messageService.sendMessage(conversationId, user.getId(), dto);
    }

    @GetMapping
    public Page<MessageResponse> fetchMessages(@PathVariable Long conversationId,
                                     @ModelAttribute MessageFilter filter) {
        return messageService.fetchMessages(conversationId, filter);
    }

    @PostMapping("/{messageId}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long conversationId,
                                         @PathVariable Long messageId,
                                         @RequestParam Long userId) {
        messageService.markRead(conversationId, userId, messageId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{messageId}")
    public void delete(@PathVariable Long messageId,
                       @CurrentUser User user) {
        messageService.delete(messageId, user.getId());
    }
}
