package com.zalo.modules.message.service;

import com.zalo.common.service.WebsocketService;
import com.zalo.modules.media.entities.Media;
import com.zalo.modules.message.dto.request.CreateSystemMessageRequest;
import com.zalo.modules.conversation.dto.respone.ConversationResponse;
import com.zalo.modules.message.dto.response.MessageResponse;
import com.zalo.modules.message.dto.response.SystemMetadataResponse;
import com.zalo.modules.user.dto.response.UserResponse;
import com.zalo.modules.conversation.entities.Conversation;
import com.zalo.modules.conversation.entities.ConversationMember;
import com.zalo.modules.message.entity.Message;
import com.zalo.modules.user.entities.User;
import com.zalo.modules.message.entity.MessageType;
import com.zalo.modules.message.entity.SystemMessageType;
import com.zalo.common.entity.SystemMetadata;
import com.zalo.modules.conversation.service.MemberService;
import com.zalo.modules.conversation.service.ConversationMemberRepository;
import com.zalo.modules.conversation.service.ConversationRepository;
import com.zalo.modules.user.service.UserService;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SystemMessageService implements SystemMessageInterface {
    MessageRepository messageRepo;
    ConversationRepository convRepo;
    EntityManager em;
    WebsocketService websocketService;
    ConversationMemberRepository memberRepo;
    MemberService memberService;
    UserService userService;

    @Override
    public void createSystemMessage(CreateSystemMessageRequest dto) {
        Message m = new Message();
        m.setContent(dto.content);
        m.setContentType(MessageType.SYSTEM);
        m.setConversationId(dto.conversationId);
        m.setSenderId(dto.senderId);

        SystemMetadata metadata = new SystemMetadata();
        metadata.setType(dto.systemMessageType);

        switch (dto.systemMessageType) {
            case ADD_USERS_TO_GROUP -> metadata.setAddedUsersToGroup(dto.userIdsAddedToGroup);

            case UPDATE_GROUP_NAME -> metadata.setGroupName((String) dto.info.get("groupName"));

            case UPDATE_GROUP_AVATAR -> metadata.setGroupAvatar((Media) dto.info.get("groupAvatar"));

//            default ->
//                    throw new IllegalArgumentException("Unsupported system message type: " + dto.systemMessageType);
        }

        m.setSystemMetadata(metadata);

        m = messageRepo.save(m);

        // clear persistence context
        em.flush();
        em.refresh(m);

        m = messageRepo.findOneWithRelationShip(m.getId(), dto.conversationId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));

        // update lastMessage for conversation
        Conversation conv = findConversationById(dto.conversationId);
        conv.setLastMessageId(m.getId());
        convRepo.save(conv);

        em.flush();
        em.refresh(conv);

        conv = findConversationByIdWithRelationShip(dto.conversationId);

        List<ConversationMember> members = memberRepo.findByConversationId(dto.conversationId);

        ConversationResponse convResponse = new ConversationResponse(conv, "recipient", "lastMessage", "createdBy", "avatar");
        convResponse.setMembers(memberService.getMembers(conv.getId()));

        MessageResponse messageResponse = new MessageResponse(m, "sender");
        getSystemMetadata(m, messageResponse);

        websocketService.sendMessage(messageResponse, convResponse, members);
    }

    public Conversation findConversationById(Long id) {
        return convRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
    }

    public Conversation findConversationByIdWithRelationShip(Long id) {
        return convRepo.findOneWithRelationShipById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
    }

    public void getSystemMetadata(Message e, MessageResponse rp) {
        if (e.getContentType() == MessageType.SYSTEM && e.getSystemMetadata() != null) {
            SystemMetadataResponse metadataResponse = new SystemMetadataResponse();

            metadataResponse.setType(e.getSystemMetadata().getType());

            if (e.getSystemMetadata().getType() == SystemMessageType.ADD_USERS_TO_GROUP) {
                List<Long> userIds = e.getSystemMetadata().getAddedUsersToGroup();
                List<User> users = userService.findByIdIn(userIds);

                metadataResponse.setAddedUsersToGroup(users.stream().map(UserResponse::new).toList());
            }

            rp.setSystemMetadata(metadataResponse);
        }
    }
}
