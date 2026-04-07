package com.zalo.service;

import com.zalo.configuration.G;
import com.zalo.dto.filter.MessageFilter;
import com.zalo.dto.request.Message.AddReactionRequest;
import com.zalo.dto.request.Message.CreateMessageRequest;
import com.zalo.dto.response.Message.MessageReactionResponse;
import com.zalo.dto.response.Message.MessageResponse;
import com.zalo.dto.response.Conversation.ConversationResponse;
import com.zalo.dto.response.Message.SystemMetadataResponse;
import com.zalo.dto.response.User.UserResponse;
import com.zalo.model.*;
import com.zalo.model.enums.ConversationType;
import com.zalo.model.enums.DeliveryStatus;
import com.zalo.model.enums.MessageType;
import com.zalo.model.enums.SystemMessageType;
import com.zalo.repository.*;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageService {
    MessageRepository messageRepo;
    MessageStatusRepository statusRepo;
    ConversationMemberRepository memberRepo;
    ConversationRepository conversationRepository;
    ConversationService conversationService;
    EntityManager em;
    WebsocketService websocketService;
    CloudinaryService cloudinaryService;
    MessageReactionRepository messageReactionRepository;
    UserService userService;
    MemberService memberService;

    public void sendMessage(Long conversationId, Long senderId, CreateMessageRequest dto) throws IOException {
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

        //mark read my message
        markRead(conversationId, senderId, m.getId());

        ConversationResponse conversationResponse = new ConversationResponse(conv, "recipient", "lastMessage", "createdBy");

        if (conv.getType() == ConversationType.GROUP) {
            conversationResponse.setMembers(memberService.getMembers(conv.getId()));
        }

        websocketService.sendMessage(new MessageResponse(m, "sender"), conversationResponse, members);
    }

    public Page<MessageResponse> fetchMessages(Long conversationId, MessageFilter filter) {
        filter.setConversationId(conversationId);

        Pageable pageable = filter.toScrollable("ct", Sort.Direction.DESC);

        Page<Message> page = messageRepo.findAll(filter.toSpecification(), pageable);

        List<MessageReaction> mrs = messageReactionRepository.findByMessageIdIn(page.getContent().stream().map(BaseEntity::getId).toList());

        Map<Long, List<MessageReactionResponse>> mapReaction = mrs.stream()
                .collect(Collectors.groupingBy(
                        MessageReaction::getMessageId,
                        Collectors.mapping(
                                mr -> new MessageReactionResponse(mr, "createdBy"),
                                Collectors.toList()
                        )
                ));

        return page.map(e -> {
            MessageResponse m = new MessageResponse(e, "sender", "replyToMessage");

            m.setReactions(mapReaction.getOrDefault(m.getId(), List.of()));

            getSystemMetadata(e,m);

//            if(e.getContentType() == MessageType.SYSTEM && e.getSystemMetadata() != null){
//                SystemMetadataResponse metadataResponse = new SystemMetadataResponse();
//
//                metadataResponse.setType(e.getSystemMetadata().getType());
//
//                if(e.getSystemMetadata().getType() == SystemMessageType.ADD_USERS_TO_GROUP){
//                    List<Long> userIds = e.getSystemMetadata().getAddedUsersToGroup();
//                    List<User> users = userService.findByIdIn(userIds);
//
//                    metadataResponse.setAddedUsersToGroup(users.stream().map(UserResponse::new).toList());
//                }
//
//                m.setSystemMetadata(metadataResponse);
//            }

            return m;
        });
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

    public void addReaction(Long conversationId, Long userId, AddReactionRequest dto) {
        MessageReaction mr = messageReactionRepository.findByMessageIdAndCuAndType(dto.messageId, userId, dto.type);

        if(mr != null){
            mr.setCount(mr.getCount() + 1);
        }else {
            mr = new MessageReaction();
            mr.setCu(userId);
            mr.setMessageId(dto.messageId);
            mr.setType(dto.type);
            mr.setCount(1);
        }

        messageReactionRepository.save(mr);

        em.flush();
        em.refresh(mr);

        mr = messageReactionRepository.findById(mr.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));

        websocketService.addReaction(conversationId, mr);
    }

    public void removeAllReactionByUserId(Long conversationId, Long messageId, Long userId) {
        messageReactionRepository.deleteByMessageIdAndCu(
                messageId,
                userId
        );

        List<MessageReaction> mrs = messageReactionRepository.findByMessageId(messageId);
        websocketService.removeAllReactionByUserId(conversationId, messageId, mrs);
    }

    public void getSystemMetadata(Message e, MessageResponse rp) {
        if(e.getContentType() == MessageType.SYSTEM && e.getSystemMetadata() != null){
            SystemMetadataResponse metadataResponse = new SystemMetadataResponse();

            metadataResponse.setType(e.getSystemMetadata().getType());

            if(e.getSystemMetadata().getType() == SystemMessageType.ADD_USERS_TO_GROUP){
                List<Long> userIds = e.getSystemMetadata().getAddedUsersToGroup();
                List<User> users = userService.findByIdIn(userIds);

                metadataResponse.setAddedUsersToGroup(users.stream().map(UserResponse::new).toList());
            }

            rp.setSystemMetadata(metadataResponse);
        }
    }
}
