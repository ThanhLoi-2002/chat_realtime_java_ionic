package com.zalo.service;

import com.zalo.configuration.G;
import com.zalo.dto.filter.ConversationFilter;
import com.zalo.dto.filter.UserFilter;
import com.zalo.dto.request.Conversation.CreateGroupRequest;
import com.zalo.dto.request.Message.CreateMessageRequest;
import com.zalo.dto.request.Message.CreateSystemMessageRequest;
import com.zalo.dto.response.Conversation.ConversationInfoResponse;
import com.zalo.dto.response.Conversation.ConversationResponse;
import com.zalo.dto.response.Conversation.MemberResponse;
import com.zalo.dto.response.Message.MessageResponse;
import com.zalo.dto.response.User.UserResponse;
import com.zalo.model.*;
import com.zalo.model.enums.ConversationType;
import com.zalo.model.enums.MemberRole;
import com.zalo.model.enums.MessageType;
import com.zalo.model.enums.SystemMessageType;
import com.zalo.model.metadata.SystemMetadata;
import com.zalo.repository.ConversationMemberRepository;
import com.zalo.repository.ConversationRepository;
import com.zalo.repository.MessageRepository;
import com.zalo.repository.UserRepository;
import com.zalo.repository.dto.UnreadDto;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Member;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationService {
    ConversationRepository conversationRepo;
    ConversationMemberRepository memberRepo;
    UserRepository userRepo;
    UserService userService;
    SystemMessageService systemMessageService;
    WebsocketService websocketService;
    MemberService memberService;

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
        conv.setLastMessageId(0L);
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
        conv.setOwnerId(creatorId);
        conv.setInviteCode(UUID.randomUUID().toString().substring(0, 8));
        conv.setName(dto.name);
        conv.setLastMessageId(0L);
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

        CreateSystemMessageRequest createSystemMessageRequest = new CreateSystemMessageRequest();
        createSystemMessageRequest.conversationId = conv.getId();
        createSystemMessageRequest.senderId = creatorId;
        createSystemMessageRequest.content = "haveRecentCreateGroup";
        createSystemMessageRequest.systemMessageType = SystemMessageType.CREATE_GROUP;

        systemMessageService.createSystemMessage(createSystemMessageRequest);

        createSystemMessageRequest.content = "youHaveRecentAddedToGroup";
        createSystemMessageRequest.systemMessageType = SystemMessageType.ADD_USERS_TO_GROUP;
        createSystemMessageRequest.userIdsAddedToGroup = dto.participantIds;

        systemMessageService.createSystemMessage(createSystemMessageRequest);
    }

    public Conversation findByIdWithRelationShip(Long id) {
        return conversationRepo.findOneWithRelationShipById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
    }

    public Conversation findById(Long id) {
        return conversationRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
    }

    public Page<ConversationResponse> findAll(Long userId, ConversationFilter filter) {
        List<Long> conversationIds = memberRepo.findConversationIdsByUserId(userId);

        filter.setIds(conversationIds);
        Pageable pageable = filter.toPageable();

        Page<Conversation> page = conversationRepo.findAllWithRelationShip(conversationIds, filter.getName(), pageable);

        Map<Long, Long> mapUnread = getUnreadFromIds(page.getContent().stream().map(BaseEntity::getId).toList(), userId);

        return page.map(c -> {
            ConversationResponse conv = new ConversationResponse(c, "recipient", "lastMessage", "createdBy");
            if (c.getType() == ConversationType.GROUP) {
                conv.setMembers(memberService.getMembers(c.getId()));
            }

            conv.setUnread(mapUnread.getOrDefault(c.getId(), 0L));

            return conv;
        });
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

                    List<MemberResponse> memberResponses = memberService.convertMembersToMemberResponses(members);
                    conv.setMembers(memberResponses);
                }
            }

            conversationInfo.generalGroup = conversationResponses;
        }

        return conversationInfo;
    }

    public List<ConversationResponse> getGroups(Long userId) {
        List<Long> conversationIds = memberRepo.findConversationIdsByUserId(userId);
        List<Conversation> conversations = conversationRepo.findByIdInAndType(conversationIds, ConversationType.GROUP);

        List<ConversationResponse> conversationResponses = conversations.stream().map(ConversationResponse::new).toList();

        for (ConversationResponse conv : conversationResponses) {

            if (conv.getAvatar() == null) {

                List<ConversationMember> members = memberRepo.findTop3ByConversationIdOrderByCtDesc(conv.getId());
                conv.setMembers(memberService.convertMembersToMemberResponses(members));
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

    public void addMembersToGroups(Long conversationId, Long inviterId, List<Long> participantIds) {
        List<ConversationMember> members = new ArrayList<>();
        for (Long id : participantIds) {
            ConversationMember m = new ConversationMember();
            m.setConversationId(conversationId);
            m.setUserId(id);
            m.setRole(MemberRole.MEMBER);
            m.setAddById(inviterId);
            members.add(m);
        }
        memberRepo.saveAll(members);

        CreateSystemMessageRequest dto = new CreateSystemMessageRequest();
        dto.conversationId = conversationId;
        dto.senderId = inviterId;
        dto.systemMessageType = SystemMessageType.ADD_USERS_TO_GROUP;
        dto.userIdsAddedToGroup = participantIds;
        dto.content = "youHaveRecentAddedToGroup";

        websocketService.addMembers(conversationId, memberService.getMembers(conversationId));
        systemMessageService.createSystemMessage(dto);
    }

    public void leaveGroup(Long conversationId, Long userId) {
        ConversationMember member = memberRepo.findByConversationIdAndUserId(conversationId, userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));

        memberRepo.delete(member);

        CreateSystemMessageRequest createSystemMessageRequest = new CreateSystemMessageRequest();
        createSystemMessageRequest.conversationId = conversationId;
        createSystemMessageRequest.senderId = userId;
        createSystemMessageRequest.content = "leaveTheGroup";
        createSystemMessageRequest.systemMessageType = SystemMessageType.LEAVE_GROUP;

        systemMessageService.createSystemMessage(createSystemMessageRequest);

        websocketService.leaveGroup(conversationId, userId);
    }

//    public Conversation update(Long id) {
//        Conversation conv = findById(id);
//        conversationRepo.save(conv);
//        return conversationRepo.save(conv);
//    }
}
