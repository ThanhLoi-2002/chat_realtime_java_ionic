package com.zalo.modules.conversation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zalo.common.base.BaseEntity;
import com.zalo.common.configuration.json.G;
import com.zalo.common.entity.SystemMetadata;
import com.zalo.common.filter.ConversationFilter;
import com.zalo.common.filter.UserFilter;
import com.zalo.modules.conversation.dto.IsMentionDto;
import com.zalo.modules.conversation.dto.request.CreateGroupRequest;
import com.zalo.modules.media.dtos.requests.MediaRequest;
import com.zalo.modules.media.dtos.responses.MediaResponse;
import com.zalo.modules.media.entities.Media;
import com.zalo.modules.media.entities.MediaType;
import com.zalo.modules.media.service.MediaInterface;
import com.zalo.modules.message.dto.request.CreateSystemMessageRequest;
import com.zalo.modules.conversation.dto.respone.ConversationInfoResponse;
import com.zalo.modules.conversation.dto.respone.ConversationResponse;
import com.zalo.modules.conversation.dto.respone.MemberResponse;
import com.zalo.modules.conversation.entities.ConversationType;
import com.zalo.modules.conversation.entities.MemberRole;
import com.zalo.modules.message.entity.SystemMessageType;
import com.zalo.modules.conversation.entities.Conversation;
import com.zalo.modules.conversation.entities.ConversationMember;
import com.zalo.modules.message.service.SystemMessageInterface;
import com.zalo.modules.user.entities.User;
import com.zalo.modules.user.service.UserRepository;
import com.zalo.modules.conversation.dto.UnreadDto;
import com.zalo.modules.user.service.UserService;
import com.zalo.common.service.WebsocketService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationService implements ConversationInterface {
    private static final Logger logger = LoggerFactory.getLogger(ConversationService.class);
    ConversationRepository conversationRepo;
    ConversationMemberRepository memberRepo;
    UserRepository userRepo;
    UserService userService;
    WebsocketService websocketService;
    MemberService memberService;
    MediaInterface mediaInterface;
    SystemMessageInterface systemMessageInterface;

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
        conv.setOwnerId(creatorId);
        conv.setInviteCode(UUID.randomUUID().toString().substring(0, 8));
        conv.setName(dto.name);
        conv = conversationRepo.save(conv);

        List<ConversationMember> members = new ArrayList<>();
        for (Long id : dto.participantIds) {
            ConversationMember m = new ConversationMember();
            m.setConversationId(conv.getId());
            m.setUserId(id);
            m.setRole(id.equals(creatorId) ? MemberRole.GOLDEN_KEY : MemberRole.MEMBER);
            m.setAddById(creatorId.equals(id) ? null : id);
            members.add(m);
        }
        memberRepo.saveAll(members);

        CreateSystemMessageRequest createSystemMessageRequest = new CreateSystemMessageRequest();
        createSystemMessageRequest.conversationId = conv.getId();
        createSystemMessageRequest.senderId = creatorId;
        createSystemMessageRequest.content = "haveRecentCreateGroup";
        createSystemMessageRequest.systemMessageType = SystemMessageType.CREATE_GROUP;

        systemMessageInterface.createSystemMessage(createSystemMessageRequest);

        createSystemMessageRequest.content = "youHaveRecentAddedToGroup";
        createSystemMessageRequest.systemMessageType = SystemMessageType.ADD_USERS_TO_GROUP;
        createSystemMessageRequest.userIdsAddedToGroup = dto.participantIds;

        systemMessageInterface.createSystemMessage(createSystemMessageRequest);
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
        List<Long> ids = page.getContent().stream().map(BaseEntity::getId).toList();

        Map<Long, Long> mapUnread = getUnreadFromIds(ids, userId);
        Map<Long, Integer> mapIsMention = isMentionFromIds(ids, userId);

        return page.map(c -> {
            ConversationResponse conv = new ConversationResponse(c, "recipient", "lastMessage", "createdBy", "avatar");
            if (c.getType() == ConversationType.GROUP) {
                conv.setMembers(memberService.getMembers(c.getId()));
            }

            conv.setUnread(mapUnread.getOrDefault(c.getId(), 0L));
            conv.setIsMention(mapIsMention.getOrDefault(c.getId(), 0) == 1);

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

        List<ConversationResponse> conversationResponses = conversations.stream().map(e -> new ConversationResponse(e, "avatar")).toList();

        for (ConversationResponse conv : conversationResponses) {
            conv.setMembers(memberService.getMembers(conv.getId()));
        }

        return conversationResponses;
    }

    public Map<Long, Long> getUnreadFromIds(List<Long> ids, Long userId) {
        List<UnreadDto> unread = conversationRepo.countUnread(ids, userId);

        return unread.stream().collect(Collectors.toMap(
                UnreadDto::getConversationId,  // key
                UnreadDto::getUnreadCount            // value
        ));
    }

    @Override
    public Map<Long, Integer> isMentionFromIds(List<Long> ids, Long userId) {
        List<IsMentionDto> mentions = conversationRepo.checkMentionsInUnread(ids, userId);

        return mentions.stream().collect(Collectors.toMap(
                IsMentionDto::getConversationId,  // key
                IsMentionDto::getIsMention            // value
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
        systemMessageInterface.createSystemMessage(dto);
    }

    public void leaveGroup(Long conversationId, Long userId) {
        ConversationMember member = memberRepo.findByConversationIdAndUserId(conversationId, userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));

        memberRepo.delete(member);

        CreateSystemMessageRequest createSystemMessageRequest = new CreateSystemMessageRequest();
        createSystemMessageRequest.conversationId = conversationId;
        createSystemMessageRequest.senderId = userId;
        createSystemMessageRequest.content = "leaveTheGroup";
        createSystemMessageRequest.systemMessageType = SystemMessageType.LEAVE_GROUP;

        systemMessageInterface.createSystemMessage(createSystemMessageRequest);

        websocketService.leaveGroup(conversationId, userId);
    }

    public void disbandGroup(Long conversationId) {
//        memberRepo.deleteManyByConversationId(conversationId);
//        messRepo.


    }

    public ConversationResponse getByInviteCode(String code) {
        Conversation conv = conversationRepo.findByInviteCode(code).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
        ConversationResponse conversationResponse = new ConversationResponse(conv, "owner", "avatar");
        conversationResponse.setMembers(memberService.getMembers(conv.getId()));
        return conversationResponse;
    }

    public void updateAvatar(Long conversationId, Long userId, MediaRequest mediaRequest) {
        mediaRequest.moduleType = MediaType.CONVERSATION;
        mediaRequest.moduleId = conversationId;
        Media media = mediaInterface.save(mediaRequest, userId);

        long updatedRows = conversationRepo.update((root, query, cb) -> {
            // 1. SET các giá trị mới
            query.set(root.get("avatarId"), media.getId());
            query.set(root.get("eu"), userId);

            // 2. WHERE điều kiện lọc
            return cb.equal(root.get("id"), conversationId);
        });

        if (updatedRows > 0) {
            CreateSystemMessageRequest dto = new CreateSystemMessageRequest();
            dto.conversationId = conversationId;
            dto.content = "haveRecentUpdatedGroupAvatar";
            dto.senderId = userId;
            dto.systemMessageType = SystemMessageType.UPDATE_GROUP_AVATAR;
            dto.info = Map.of(
                    "groupAvatar", media
            );

            systemMessageInterface.createSystemMessage(dto);
        }
    }

    public void updateGroupName(Long conversationId, Long userId, String name) {
        long updatedRows = conversationRepo.update((root, query, cb) -> {
            // 1. SET các giá trị mới
            query.set(root.get("name"), name);
            query.set(root.get("eu"), userId);

            // 2. WHERE điều kiện lọc
            return cb.equal(root.get("id"), conversationId);
        });

        // Tùy chọn: Kiểm tra nếu không có bản ghi nào được cập nhật
        if (updatedRows > 0) {
            CreateSystemMessageRequest dto = new CreateSystemMessageRequest();
            dto.conversationId = conversationId;
            dto.content = "haveRecentUpdatedGroupNameTo";
            dto.senderId = userId;
            dto.systemMessageType = SystemMessageType.UPDATE_GROUP_NAME;
            dto.info = Map.of(
                    "groupName", name
            );

            systemMessageInterface.createSystemMessage(dto);
        }
    }

    public Long getReadLastMessageId(Long conversationId, Long userId) {
        ConversationMember data = memberRepo.findByConversationIdAndUserId(conversationId, userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound")
        );

        return data.getLastReadMessageId();
    }
}
