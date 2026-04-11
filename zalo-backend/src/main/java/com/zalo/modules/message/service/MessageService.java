package com.zalo.modules.message.service;

import com.zalo.common.base.BaseEntity;
import com.zalo.common.configuration.json.G;
import com.zalo.common.service.WebsocketService;
import com.zalo.common.filter.MessageFilter;
import com.zalo.modules.media.dtos.responses.MediaResponse;
import com.zalo.modules.media.entities.Media;
import com.zalo.modules.message.dto.request.AddReactionRequest;
import com.zalo.modules.message.dto.request.CreateMessageRequest;
import com.zalo.modules.message.dto.response.MessageReactionResponse;
import com.zalo.modules.message.dto.response.MessageResponse;
import com.zalo.modules.conversation.dto.respone.ConversationResponse;
import com.zalo.modules.message.dto.response.SystemMetadataResponse;
import com.zalo.modules.message.entity.Message;
import com.zalo.modules.message.entity.MessageReaction;
import com.zalo.modules.message.entity.MessageStatus;
import com.zalo.modules.user.dto.response.UserResponse;
import com.zalo.modules.conversation.entities.ConversationType;
import com.zalo.modules.message.entity.DeliveryStatus;
import com.zalo.modules.message.entity.MessageType;
import com.zalo.modules.message.entity.SystemMessageType;
import com.zalo.modules.conversation.entities.Conversation;
import com.zalo.modules.conversation.entities.ConversationMember;
import com.zalo.modules.conversation.service.ConversationMemberRepository;
import com.zalo.modules.conversation.service.ConversationRepository;
import com.zalo.modules.conversation.service.ConversationService;
import com.zalo.modules.conversation.service.MemberService;
import com.zalo.modules.media.dtos.requests.MediaRequest;
import com.zalo.modules.media.entities.MediaType;
import com.zalo.modules.media.service.MediaInterface;
import com.zalo.modules.user.entities.User;
import com.zalo.modules.user.service.UserService;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    MessageReactionRepository messageReactionRepository;
    UserService userService;
    MemberService memberService;
    MediaInterface mediaInterface;

    public void sendMessage(Long conversationId, Long senderId, CreateMessageRequest dto) throws IOException {
        // optional check replyTo exists
        if (dto.replyToId != null) messageRepo.findById(dto.replyToId).orElseThrow();

        List<MediaRequest> attachments = dto.attachments;
        int fileCount = (attachments != null) ? attachments.size() : 0;

        // TRƯỜNG HỢP 1: Nhiều file (> 1) -> Tách TEXT và MEDIA riêng
        if (fileCount > 1) {
            // Gửi tin nhắn TEXT trước (nếu có nội dung)
            if (dto.content != null && !dto.content.trim().isEmpty()) {
                saveAndNotifyMessage(conversationId, senderId, dto.content, MessageType.TEXT, null, dto.replyToId);
            }
            // Gửi tin nhắn MEDIA chứa toàn bộ list ảnh (content lúc này thường để null hoặc tùy bạn)
            saveAndNotifyMessage(conversationId, senderId, null, MessageType.IMAGE, attachments, null);
        }
        // TRƯỜNG HỢP 2: 1 file hoặc 0 file -> Gộp chung
        else {
            MessageType type = fileCount == 1 ? MessageType.IMAGE : MessageType.TEXT;
            saveAndNotifyMessage(conversationId, senderId, dto.content, type, attachments, dto.replyToId);
        }
    }

    private void saveAndNotifyMessage(Long conversationId, Long senderId, String content,
                                      MessageType type, List<MediaRequest> attachments, Long replyId) {
        if (content == null && attachments.isEmpty()) return;

        // 1. Lưu Message chính
        Message m = Message.builder()
                .conversationId(conversationId)
                .senderId(senderId)
                .content(content)
                .contentType(type)
                .replyToMessageId(replyId)
                .build();
        Message newMess = messageRepo.save(m);

        // 2. Lưu danh sách Media (nếu có)
        if (attachments != null && !attachments.isEmpty()) {
            List<MediaRequest> mediaEntities = attachments.stream().map(f -> {
                MediaRequest media = new MediaRequest();
                media.setModuleId(newMess.getId());
                media.setModuleType(MediaType.MESSAGE); // Enum bạn đã định nghĩa
                media.setSecureUrl(f.getSecureUrl());
                media.setPublicId(f.getPublicId());
                media.setResourceType(f.getResourceType());
                media.setFormat(f.getFormat());
                media.setBytes(f.getBytes());
                media.setHeight(f.height);
                media.setWidth(f.width);
                return media;
            }).collect(Collectors.toList());
            mediaInterface.saveAll(mediaEntities, senderId);
        }

        // 3. Đồng bộ Persistence Context (Flush/Refresh)
        em.flush();
        em.refresh(m);
        Message finalMsg = findByIdWithRelationShip(m.getId(), conversationId);

        // 4. Cập nhật Last Message cho Conversation
        Conversation conv = conversationRepository.findById(conversationId).orElseThrow();
        conv.setLastMessageId(finalMsg.getId());
        conversationRepository.save(conv);

        // 5. Tạo MessageStatus cho các thành viên
        List<ConversationMember> members = memberRepo.findByConversationId(conversationId);
        List<MessageStatus> statuses = members.stream().map(member ->
                MessageStatus.builder()
                        .messageId(finalMsg.getId())
                        .userId(member.getUserId())
                        .status(member.getUserId().equals(senderId) ? DeliveryStatus.DELIVERED : DeliveryStatus.SENT)
                        .build()
        ).collect(Collectors.toList());
        statusRepo.saveAll(statuses);

        // 6. Đánh dấu đã xem cho chính mình
        markRead(conversationId, senderId, finalMsg.getId());

        // 7. Bắn WebSocket
        ConversationResponse convRes = new ConversationResponse(conversationService.findByIdWithRelationShip(conversationId), "recipient", "lastMessage", "createdBy", "avatar");
        if (conv.getType() == ConversationType.GROUP) {
            convRes.setMembers(memberService.getMembers(conv.getId()));
        }
        websocketService.sendMessage(new MessageResponse(finalMsg, "sender"), convRes, members);
    }

    public Page<MessageResponse> fetchMessages(Long conversationId, MessageFilter filter) {
        filter.setConversationId(conversationId);

        Pageable pageable = filter.toScrollable("ct", Sort.Direction.DESC);

        Page<Message> page = messageRepo.findAll(filter.toSpecification(), pageable);

        List<Long> messageIds = page.getContent().stream().map(BaseEntity::getId).toList();

        List<MessageReaction> mrs = messageReactionRepository.findByMessageIdIn(messageIds);
        List<Media> medias = mediaInterface.findByModuleIdInAndModuleType(messageIds, MediaType.MESSAGE);

        Map<Long, List<MessageReactionResponse>> mapReaction = mrs.stream()
                .collect(Collectors.groupingBy(
                        MessageReaction::getMessageId,
                        Collectors.mapping(
                                mr -> new MessageReactionResponse(mr, "createdBy"),
                                Collectors.toList()
                        )
                ));

        Map<Long, List<MediaResponse>> mapAttachment = medias.stream()
                .collect(Collectors.groupingBy(
                        Media::getModuleId,
                        Collectors.mapping(
                                MediaResponse::new,
                                Collectors.toList()
                        )
                ));

        return page.map(e -> {
            MessageResponse m = new MessageResponse(e, "sender", "replyToMessage");

            m.setReactions(mapReaction.getOrDefault(m.getId(), List.of()));
            m.setAttachments(mapAttachment.getOrDefault(m.getId(), List.of()));

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

        if (mr != null) {
            mr.setCount(mr.getCount() + 1);
        } else {
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
}
