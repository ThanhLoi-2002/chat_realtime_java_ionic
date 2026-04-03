package com.zalo.service;

import com.zalo.configuration.G;
import com.zalo.dto.filter.ConversationFilter;
import com.zalo.dto.filter.UserFilter;
import com.zalo.dto.request.Conversation.CreateGroupRequest;
import com.zalo.dto.request.Message.CreateMessageRequest;
import com.zalo.dto.response.Conversation.ConversationInfoResponse;
import com.zalo.dto.response.Conversation.ConversationResponse;
import com.zalo.dto.response.Message.MessageResponse;
import com.zalo.dto.response.User.UserResponse;
import com.zalo.model.*;
import com.zalo.model.enums.ConversationType;
import com.zalo.model.enums.MemberRole;
import com.zalo.model.enums.MessageType;
import com.zalo.repository.ConversationMemberRepository;
import com.zalo.repository.ConversationRepository;
import com.zalo.repository.MessageRepository;
import com.zalo.repository.UserRepository;
import com.zalo.repository.dto.UnreadDto;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

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
            m.setAddById(creatorId);
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
        Message m = Message.builder().conversationId(conversationId).content(dto.content).contentType(dto.contentType).replyToMessageId(dto.replyToId).build();

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

        List<ConversationMember> members = memberRepo.findByConversationId(conversationId);

        websocketService.sendMessage(new MessageResponse(m), new ConversationResponse(conv, "lastMessage", "createdBy"), members);
    }

    public Conversation findByIdWithRelationShip(Long id) {
        return conversationRepo.findOneWithRelationShipById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
    }

    public Conversation findById(Long id) {
        return conversationRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
    }

    public List<User> getMembers(Long conversationId) {
        List<ConversationMember> members = memberRepo.findByConversationIdOrderByIdDesc(conversationId);

        List<Long> userIds = members.stream().map(ConversationMember::getUserId).toList();

        List<User> users = userRepo.findByIdIn(userIds);

        // map user theo id
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));

        // rebuild theo thứ tự members
        return userIds.stream().map(userMap::get).filter(Objects::nonNull).toList();
    }

    public Page<ConversationResponse> findAll(Long userId, ConversationFilter filter) {
        List<Long> conversationIds = memberRepo.findConversationIdsByUserId(userId);

        filter.setIds(conversationIds);
        Pageable pageable = filter.toPageable();

        Page<Conversation> page = conversationRepo.findAllWithRelationShip(conversationIds, filter.getName(), pageable);

        Map<Long, Long> mapUnread = getUnreadFromIds(page.getContent().stream().map(BaseEntity::getId).toList(), userId);

        Page<ConversationResponse> result = page.map(c -> {
            ConversationResponse conv = new ConversationResponse(c, "recipient", "lastMessage", "createdBy");
            if (c.getType() == ConversationType.GROUP) {
                conv.setMembers(getMembers(c.getId()).stream().map(UserResponse::new).toList());
            }

            conv.setUnread(mapUnread.getOrDefault(c.getId(), 0L));

            return conv;
        });

        return result;
    }

    public ConversationInfoResponse getInfo(Long conversationId, Long userId) {
        Conversation c = findById(conversationId);

        ConversationInfoResponse conversationInfo = new ConversationInfoResponse();

        if (c.getType() == ConversationType.PRIVATE) {
            List<Long> conversationIds = memberRepo.findCommonConversationIds(userId, Objects.equals(c.getRecipientId(), userId) ? c.getCu() : c.getRecipientId());
            List<Conversation> conversations = conversationRepo.findByIdInAndType(conversationIds, ConversationType.GROUP);

            List<ConversationResponse> conversationResponses = conversations.stream().map(ConversationResponse::new).toList();

            for (ConversationResponse conv : conversationResponses) {

                if (conv.getAvatar() == null) {

                    List<ConversationMember> members = memberRepo.findTop3ByConversationIdOrderByCtDesc(conv.getId());

                    // map sang user (nếu cần)
                    List<Long> userIds = members.stream().map(ConversationMember::getUserId).toList();

                    List<UserResponse> users = userRepo.findAllById(userIds).stream().map(UserResponse::new).toList();
                    conv.setMembers(users);
                }
            }

            conversationInfo.generalGroup = conversationResponses;
        }

        Pageable limit = PageRequest.of(0, 8);

        List<Message> images = messageRepo.findByConversationIdAndContentTypeAndSttOrderByCtDesc(conversationId, MessageType.IMAGE, 1, limit).getContent();
        conversationInfo.messages = images.stream().map(m -> new MessageResponse(m, "sender")).toList();

        return conversationInfo;
    }

    public List<ConversationResponse> getGroups(Long userId) {
        List<Long> conversationIds = memberRepo.findConversationIdsByUserId(userId);
        List<Conversation> conversations = conversationRepo.findByIdInAndType(conversationIds, ConversationType.GROUP);

        List<ConversationResponse> conversationResponses = conversations.stream().map(ConversationResponse::new).toList();

        for (ConversationResponse conv : conversationResponses) {

            if (conv.getAvatar() == null) {

                List<ConversationMember> members = memberRepo.findTop3ByConversationIdOrderByCtDesc(conv.getId());

                // map sang user (nếu cần)
                List<Long> userIds = members.stream().map(ConversationMember::getUserId).toList();

                List<UserResponse> users = userRepo.findAllById(userIds).stream().map(UserResponse::new).toList();
                conv.setMembers(users);
            }
        }

        return conversationResponses;
    }

    public Map<Long, Long> getUnreadFromIds(List<Long> ids, Long userId){
        List<UnreadDto> unread = conversationRepo.countUnread(ids, userId);

        return unread.stream().collect(Collectors.toMap(
                UnreadDto::getConversationId,  // key
                UnreadDto::getUnreadCount            // value
        ));
    }

//    public Conversation update(Long id) {
//        Conversation conv = findById(id);
//        conversationRepo.save(conv);
//        return conversationRepo.save(conv);
//    }
}
