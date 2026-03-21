package com.zalo.service;

import com.zalo.configuration.G;
import com.zalo.dto.filter.MessageFilter;
import com.zalo.dto.request.Message.CreateMessageRequest;
import com.zalo.dto.response.Message.MessageResponse;
import com.zalo.model.Conversation;
import com.zalo.model.ConversationMember;
import com.zalo.model.Message;
import com.zalo.model.MessageStatus;
import com.zalo.model.enums.DeliveryStatus;
import com.zalo.repository.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepo;
    private final MessageStatusRepository statusRepo;
    private final ConversationMemberRepository memberRepo;
    private final UserRepository userRepo;
    private final ConversationRepository conversationRepository;
    private final ConversationService conversationService;
    private final EntityManager em;

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
        // clear persistence context
        em.flush();
        em.refresh(m);

        m = findByIdWithRelationShip(m.getId(), conversationId);

        System.out.println(G.toJson(m.getSender()));

        // update lastMessage for conversation
        Conversation conv = conversationService.findById(conversationId);
        conv.setLastMessageId(m.getId());
        conversationRepository.save(conv);

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
//        Map<String, Object> payload = new HashMap<>();
//        payload.put("id", m.getId());
//        payload.put("conversationId", m.getConversationId());
//        payload.put("senderId", m.getSenderId());
//        userRepo.findById(m.getSenderId()).ifPresent(u -> {
//            payload.put("senderUsername", u.getUsername());
//        });
//        payload.put("content", m.getContent());
//        payload.put("contentType", m.getContentType());
//        payload.put("createdAt", m.getCt());
//        payload.put("replyToMessageId", m.getReplyToMessageId());



        return m;
    }

    public Page<MessageResponse> fetchMessages(Long conversationId, MessageFilter filter) {
        Pageable pageable = filter.toPageable();

        Page<Message> page = messageRepo.findAllWithRelationShip(conversationId, pageable);

        return page.map(MessageResponse::new);
    }

    public Message findByIdWithRelationShip(Long id, Long conversationId) {
        return messageRepo.findOneWithRelationShip(id, conversationId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
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
