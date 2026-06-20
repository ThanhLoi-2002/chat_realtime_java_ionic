package com.zalo.modules.app.message.service;

import com.zalo.common.service.WebsocketService;
import com.zalo.modules.app.media.dtos.responses.MediaResponse;
import com.zalo.modules.app.media.entities.Media;
import com.zalo.modules.app.media.entities.MediaType;
import com.zalo.modules.app.media.service.MediaRepository;
import com.zalo.modules.app.message.dto.request.CreateSystemMessageRequest;
import com.zalo.modules.app.conversation.dto.respone.ConversationResponse;
import com.zalo.modules.app.message.dto.response.MessageResponse;
import com.zalo.modules.app.message.dto.response.SystemMetadataResponse;
import com.zalo.modules.app.user.dto.response.UserResponse;
import com.zalo.modules.app.conversation.entities.Conversation;
import com.zalo.modules.app.conversation.entities.ConversationMember;
import com.zalo.modules.app.message.entity.Message;
import com.zalo.modules.app.user.entities.User;
import com.zalo.modules.app.message.entity.MessageType;
import com.zalo.modules.app.message.entity.SystemMessageType;
import com.zalo.common.entity.SystemMetadata;
import com.zalo.modules.app.conversation.service.MemberService;
import com.zalo.modules.app.conversation.service.ConversationMemberRepository;
import com.zalo.modules.app.conversation.service.ConversationRepository;
import com.zalo.modules.app.user.service.UserService;
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
    MediaRepository mediaRepository;

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

            case REMOVE_MEMBER, ORDAIN_SILVER_KEY, REVOKE_SILVER_KEY, TRANSFER_GOLDEN_KEY ->
                    metadata.setUserId((Long) dto.info.get("userId"));

            case PIN_MESSAGE, REMOVE_PIN_MESSAGE ->
                    metadata.setMessageId((Long) dto.info.get("messageId"));

//            default -> throw new IllegalArgumentException("Unexpected value: " + dto.systemMessageType);
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

    @Override
    public void getSystemMetadata(Message e, MessageResponse rp) {
        if (e.getContentType() == MessageType.SYSTEM && e.getSystemMetadata() != null) {
            SystemMetadataResponse metadataResponse = new SystemMetadataResponse();

            metadataResponse.setType(e.getSystemMetadata().getType());
            metadataResponse.setGroupName(e.getSystemMetadata().getGroupName());

            if (e.getSystemMetadata().getGroupAvatar() != null) {
                metadataResponse.setGroupAvatar(new MediaResponse(e.getSystemMetadata().getGroupAvatar()));
            }

            if (e.getSystemMetadata().getUserId() != null) {
                List<User> users = userService.findByIdIn(List.of(e.getSystemMetadata().getUserId()));
                metadataResponse.setUser(new UserResponse(users.get(0)));
            }

            if (e.getSystemMetadata().getType() == SystemMessageType.ADD_USERS_TO_GROUP) {
                List<Long> userIds = e.getSystemMetadata().getAddedUsersToGroup();
                List<User> users = userService.findByIdIn(userIds);

                metadataResponse.setAddedUsersToGroup(users.stream().map(UserResponse::new).toList());
            }

            if (e.getSystemMetadata().getType() == SystemMessageType.PIN_MESSAGE) {
                Long messageId = e.getSystemMetadata().getMessageId();
//                System.out.println("messageId: " + messageId + ", SystemMessageType.PIN_MESSAGE: " + e.getId());
                MessageResponse mr = findOneMessWithMedia(messageId);

                metadataResponse.setMessage(mr);
            }

            if (e.getSystemMetadata().getType() == SystemMessageType.REMOVE_PIN_MESSAGE) {
                Long messageId = e.getSystemMetadata().getMessageId();
//                System.out.println("messageId: " + messageId + ", SystemMessageType.REMOVE_PIN_MESSAGE");
                MessageResponse mr = findOneMessWithMedia(messageId);

                metadataResponse.setMessage(mr);
            }

            rp.setSystemMetadata(metadataResponse);
        }
    }

    public MessageResponse findOneMessWithMedia(Long messId) {
        Message m = messageRepo.findById(messId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
        List<Media> medias = mediaRepository.findByModuleIdAndModuleType(m.getId(), MediaType.MESSAGE);
        MessageResponse mr = new MessageResponse(m);
        mr.setAttachments(medias.stream().map(MediaResponse::new).toList());
        return mr;
    }
}
