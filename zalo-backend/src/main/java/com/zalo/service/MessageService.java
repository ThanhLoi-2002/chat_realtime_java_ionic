package com.zalo.service;

import com.zalo.dto.request.Message.CreateMessageRequest;
import com.zalo.model.ConversationMember;
import com.zalo.model.Message;
import com.zalo.model.MessageStatus;
import com.zalo.model.enums.DeliveryStatus;
import com.zalo.model.enums.MessageType;
import com.zalo.repository.ConversationMemberRepository;
import com.zalo.repository.MessageRepository;
import com.zalo.repository.MessageStatusRepository;
import com.zalo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepo;
    private final MessageStatusRepository statusRepo;
    private final ConversationMemberRepository memberRepo;
    private final UserRepository userRepo;
    private final SimpMessagingTemplate messagingTemplate;

    public Message sendMessage(Long conversationId, Long senderId, CreateMessageRequest dto) {
        // check sender is member
        ConversationMember cm = memberRepo.findByConversationIdAndUserId(conversationId, senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender is not a member"));

        // optional check replyTo exists
        if (dto.replyToId != null) messageRepo.findById(dto.replyToId).orElseThrow();

        Message m = Message.builder()
                .conversationId(conversationId)
                .senderId(senderId)
                .content(dto.content)
                .contentType(dto.type)
                .replyToMessageId(dto.replyToId)
                .build();
        m = messageRepo.save(m);

        // create statuses for each member in conversation
        List<ConversationMember> members = memberRepo.findByConversationId(conversationId);
        List<MessageStatus> statuses = new ArrayList<>();
        for (ConversationMember member : members) {
            MessageStatus st = MessageStatus.builder()
                    .messageId(m.getId())
                    .userId(member.getUserId())
                    .status(member.getUserId().equals(senderId) ? DeliveryStatus.DELIVERED : DeliveryStatus.SENT)
                    .build();
            statuses.add(st);
        }
        statusRepo.saveAll(statuses);

        // Build a DTO-like payload including sender info for websocket clients
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", m.getId());
        payload.put("conversationId", m.getConversationId());
        payload.put("senderId", m.getSenderId());
        userRepo.findById(m.getSenderId()).ifPresent(u -> {
            payload.put("senderUsername", u.getUsername());
        });
        payload.put("content", m.getContent());
        payload.put("contentType", m.getContentType());
        payload.put("createdAt", m.getCt());
        payload.put("replyToMessageId", m.getReplyToMessageId());

        messagingTemplate.convertAndSend("/topic/conversations." + conversationId, (Object) payload);

        return m;
    }

    public List<Message> fetchMessages(Long conversationId, int limit) {
        return messageRepo.findByConversationIdOrderByCtDesc(conversationId, (Pageable) PageRequest.of(0, limit));
    }

    public void markRead(Long conversationId, Long userId, Long messageId) {
        Optional<MessageStatus> opt = statusRepo.findByMessageIdAndUserId(messageId, userId);
        opt.ifPresent(ms -> {
            ms.setStatus(DeliveryStatus.READ);
            statusRepo.save(ms);
        });

        memberRepo.findByConversationIdAndUserId(conversationId, userId).ifPresent(m -> {
            m.setLastReadMessageId(messageId);
            memberRepo.save(m);
        });
    }
}
