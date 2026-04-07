package com.zalo.service;

import com.zalo.dto.request.Message.CreateSystemMessageRequest;
import com.zalo.dto.response.Conversation.ConversationResponse;
import com.zalo.dto.response.Message.MessageResponse;
import com.zalo.model.Conversation;
import com.zalo.model.ConversationMember;
import com.zalo.model.Message;
import com.zalo.model.enums.MessageType;
import com.zalo.model.enums.SystemMessageType;
import com.zalo.model.metadata.SystemMetadata;
import com.zalo.repository.ConversationMemberRepository;
import com.zalo.repository.ConversationRepository;
import com.zalo.repository.MessageRepository;
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
public class SystemMessageService {
    MessageRepository messageRepo;
    ConversationRepository convRepo;
    EntityManager em;
    WebsocketService websocketService;
    ConversationMemberRepository memberRepo;

    public void createSystemMessage(CreateSystemMessageRequest dto) {
        Message m = new Message();
        m.setContent(dto.content);
        m.setContentType(MessageType.SYSTEM);
        m.setConversationId(dto.conversationId);
        m.setSenderId(dto.senderId);

        if(dto.systemMessageType == SystemMessageType.ADD_USERS_TO_GROUP){
            SystemMetadata metadata = new SystemMetadata();
            metadata.setAddedUsersToGroup(dto.userIdsAddedToGroup);

            m.setSystemMetadata(metadata);
        }

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

        websocketService.sendMessage(new MessageResponse(m, "senderId"), new ConversationResponse(conv, "lastMessage", "createdBy"), members);
    }

    public Conversation findConversationById(Long id) {
        return convRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
    }

    public Conversation findConversationByIdWithRelationShip(Long id) {
        return convRepo.findOneWithRelationShipById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
    }
}
