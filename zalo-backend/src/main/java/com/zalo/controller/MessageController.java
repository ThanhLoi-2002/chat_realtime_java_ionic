package com.zalo.controller;

import com.zalo.configuration.anotation.CurrentUser;
import com.zalo.dto.request.Message.CreateMessageRequest;
import com.zalo.model.Message;
import com.zalo.model.User;
import com.zalo.model.enums.MessageType;
import com.zalo.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/conversations/{conversationId}/messages")
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<Message> send(@PathVariable Long conversationId,
                                        @CurrentUser User user,
                                        @RequestParam CreateMessageRequest dto) {
        Message m = messageService.sendMessage(conversationId, user.getId(), dto);
        return ResponseEntity.ok(m);
    }

    @GetMapping
    public ResponseEntity<List<Message>> list(@PathVariable Long conversationId,
                                              @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(messageService.fetchMessages(conversationId, limit));
    }

    @PostMapping("/{messageId}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long conversationId,
                                         @PathVariable Long messageId,
                                         @RequestParam Long userId) {
        messageService.markRead(conversationId, userId, messageId);
        return ResponseEntity.ok().build();
    }
}
