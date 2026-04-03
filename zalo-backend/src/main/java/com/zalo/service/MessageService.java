package com.zalo.service;

import com.zalo.configuration.G;
import com.zalo.dto.filter.MessageFilter;
import com.zalo.dto.request.Message.CreateMessageRequest;
import com.zalo.dto.response.Message.MessageResponse;
import com.zalo.dto.response.Conversation.ConversationResponse;
import com.zalo.dto.response.User.UserResponse;
import com.zalo.model.*;
import com.zalo.model.enums.ConversationType;
import com.zalo.model.enums.DeliveryStatus;
import com.zalo.model.enums.MessageType;
import com.zalo.repository.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepo;
    private final MessageStatusRepository statusRepo;
    private final ConversationMemberRepository memberRepo;
    private final ConversationRepository conversationRepository;
    private final ConversationService conversationService;
    private final EntityManager em;
    private final WebsocketService websocketService;
    private final CloudinaryService cloudinaryService;

    public void sendMessage(Long conversationId, Long senderId, CreateMessageRequest dto) throws IOException {
        // check sender is member
        ConversationMember cm = memberRepo.findByConversationIdAndUserId(conversationId, senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender is not a member"));

        // optional check replyTo exists
        if (dto.replyToId != null) messageRepo.findById(dto.replyToId).orElseThrow();

        Message m = Message.builder()
                .conversationId(conversationId)
                .senderId(senderId)
                .content(dto.content)
                .contentType(dto.contentType)
                .replyToMessageId(dto.replyToId)
                .build();

        if (dto.contentType == MessageType.IMAGE) {
            File file = cloudinaryService.uploadFile(dto.file);
            m.setFile(file);
        }

        m = messageRepo.save(m);
        // clear persistence context
        em.flush();
        em.refresh(m);

        m = findByIdWithRelationShip(m.getId(), conversationId);

        // update lastMessage for conversation
        Conversation conv = conversationService.findById(conversationId);
        conv.setLastMessageId(m.getId());
        conversationRepository.save(conv);

        em.flush();
        em.refresh(conv);

        conv = conversationService.findByIdWithRelationShip(conversationId);

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

        ConversationResponse conversationResponse = new ConversationResponse(conv, "recipient", "lastMessage", "createdBy");

        if (conv.getType() == ConversationType.GROUP) {
            conversationResponse.setMembers(conversationService.getMembers(conv.getId()).stream().map(UserResponse::new).toList());
        }

        websocketService.sendMessage(new MessageResponse(m, "sender"), conversationResponse, members);
    }

    public Page<MessageResponse> fetchMessages(Long conversationId, MessageFilter filter) {
        Pageable pageable = filter.toPageable();

        Page<Message> page = messageRepo.findAllWithRelationShip(conversationId, pageable);

        return page.map(e -> new MessageResponse(e, "sender"));
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

    public void delete(Long id, Long userId) {
        Optional<Message> m = messageRepo.findById(id);
        if (m.isPresent()) {
            if (Objects.equals(m.get().getSenderId(), userId)) {
                m.get().setStt(-1);
                messageRepo.save(m.get());

                Message mess = findByIdWithRelationShip(m.get().getId(), m.get().getConversationId());

                websocketService.updateMessage(mess);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "noRightPerform");
            }

        }
    }

    public void createSystemMessage(Long conversationId, CreateMessageRequest dto) {
        Message m = Message.builder()
                .conversationId(conversationId)
                .content(dto.content)
                .contentType(dto.contentType)
                .replyToMessageId(dto.replyToId)
                .build();

        m = messageRepo.save(m);

        // clear persistence context
        em.flush();
        em.refresh(m);

        m = findByIdWithRelationShip(m.getId(), conversationId);

        // update lastMessage for conversation
        Conversation conv = conversationService.findById(conversationId);
        conv.setLastMessageId(m.getId());
        conversationRepository.save(conv);

        em.flush();
        em.refresh(conv);

        conv = conversationService.findByIdWithRelationShip(conversationId);

        // create statuses for each member in conversation
        List<ConversationMember> members = memberRepo.findByConversationId(conversationId);

        websocketService.sendMessage(new MessageResponse(m), new ConversationResponse(conv, "recipient", "lastMessage", "createdBy"), members);
    }
}
