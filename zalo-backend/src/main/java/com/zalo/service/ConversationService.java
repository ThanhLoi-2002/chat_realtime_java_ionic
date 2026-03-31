package com.zalo.service;

import com.zalo.dto.filter.ConversationFilter;
import com.zalo.dto.filter.UserFilter;
import com.zalo.dto.request.Conversation.CreateGroupRequest;
import com.zalo.dto.request.Message.CreateMessageRequest;
import com.zalo.dto.response.Conversation.ConversationResponse;
import com.zalo.dto.response.Message.MessageResponse;
import com.zalo.model.*;
import com.zalo.model.enums.ConversationType;
import com.zalo.model.enums.DeliveryStatus;
import com.zalo.model.enums.MemberRole;
import com.zalo.model.enums.MessageType;
import com.zalo.repository.ConversationMemberRepository;
import com.zalo.repository.ConversationRepository;
import com.zalo.repository.MessageRepository;
import com.zalo.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@Transactional
@AllArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepo;
    private final MessageRepository messageRepo;
    private final ConversationMemberRepository memberRepo;
    private final UserRepository userRepo;
    private final UserService userService;
    private final EntityManager em;
    private final WebsocketService websocketService;

    public Conversation createPrivateConversation(Long creatorId, Long otherUserId) {
        // try find existing
        Optional<Conversation> existing = conversationRepo.findPrivateBetween(creatorId, otherUserId, ConversationType.PRIVATE.name());
        if (existing.isPresent()) return existing.get();

        Optional<User> creator = userService.findOne(UserFilter.builder().id(creatorId).build());
        Optional<User> otherUser = userService.findOne(UserFilter.builder().id(otherUserId).build());

        if (creator.isEmpty() || otherUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound");
        }

        Conversation conv = new Conversation();
        conv.setType(ConversationType.PRIVATE);
        conv.setRecipientId(otherUserId);
        conv.setCu(creatorId);
        conv = conversationRepo.save(conv);

        ConversationMember m1 = new ConversationMember();
        m1.setConversationId(conv.getId());
        m1.setUserId(creatorId);
        m1.setRole(MemberRole.MEMBER);

        ConversationMember m2 = new ConversationMember();
        m2.setConversationId(conv.getId());
        m2.setUserId(otherUserId);
        m2.setRole(MemberRole.MEMBER);

        memberRepo.saveAll(Arrays.asList(m1, m2));

        return findByIdWithRelationShip(conv.getId());
    }

    public void createGroupConversation(Long creatorId, CreateGroupRequest dto) {
        userRepo.findById(creatorId).orElseThrow();
        for (Long id : dto.participantIds) userRepo.findById(id).orElseThrow();

        Conversation conv = new Conversation();
        conv.setType(ConversationType.GROUP);
        conv.setCu(creatorId);
        conv.setName(dto.name);
        conv = conversationRepo.save(conv);

        List<ConversationMember> members = new ArrayList<>();
        for (Long id : dto.participantIds) {
            ConversationMember m = new ConversationMember();
            m.setConversationId(conv.getId());
            m.setUserId(id);
            m.setRole(id.equals(creatorId) ? MemberRole.ADMIN : MemberRole.MEMBER);
            members.add(m);
        }
        memberRepo.saveAll(members);

        // create system message
        CreateMessageRequest message = new CreateMessageRequest();
        message.setContentType(MessageType.SYSTEM);
        message.setContent("Bạn vừa được thêm vào nhóm");

        createSystemMessage(conv.getId(), message);
//        message = messageRepo.save(message);
//
//        conv.setLastMessageId(message.getId());
//        conversationRepo.save(conv);

//        return conv;
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

        m = messageRepo.findOneWithRelationShip(m.getId(), conversationId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));

        // update lastMessage for conversation
        Conversation conv = findById(conversationId);
        conv.setLastMessageId(m.getId());
        conversationRepo.save(conv);

        em.flush();
        em.refresh(conv);

        conv = findByIdWithRelationShip(conversationId);

        websocketService.sendMessage(new MessageResponse(m), new ConversationResponse(conv, "lastMessage", "createdBy"));
    }

    public Conversation findByIdWithRelationShip(Long id) {
        return conversationRepo.findOneWithRelationShipById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
    }

    public Conversation findById(Long id) {
        return conversationRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
    }

    public List<ConversationMember> getMembers(Long conversationId) {
        return memberRepo.findByConversationId(conversationId);
    }

    public Page<Conversation> findAll(Long userId, ConversationFilter filter) {
        List<Long> conversationIds = memberRepo.findConversationIdsByUserId(userId);

        filter.setIds(conversationIds);

//        Specification<Conversation> spec = filter.toSpecification();
        Pageable pageable = filter.toPageable();

        return conversationRepo.findAllWithRelationShip(conversationIds, filter.getName(), pageable);

//        return page.map(ConversationResponse::new);
    }

//    public Conversation update(Long id) {
//        Conversation conv = findById(id);
//        conversationRepo.save(conv);
//        return conversationRepo.save(conv);
//    }
}
