package com.zalo.modules.message.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.zalo.common.base.BaseEntity;
import com.zalo.common.configuration.json.G;
import com.zalo.common.gateway.UserOnlineStorage;
import com.zalo.common.service.PushNotificationService;
import com.zalo.common.service.WebsocketService;
import com.zalo.common.filter.MessageFilter;
import com.zalo.modules.conversation.service.*;
import com.zalo.modules.media.dtos.responses.MediaResponse;
import com.zalo.modules.media.entities.Media;
import com.zalo.modules.message.dto.request.AddReactionRequest;
import com.zalo.modules.message.dto.request.CreateMessageRequest;
import com.zalo.modules.message.dto.request.ShareMessageRequest;
import com.zalo.modules.message.dto.response.LinkPreviewResponse;
import com.zalo.modules.message.dto.response.MessageReactionResponse;
import com.zalo.modules.message.dto.response.MessageResponse;
import com.zalo.modules.conversation.dto.respone.ConversationResponse;
import com.zalo.modules.message.entity.Message;
import com.zalo.modules.message.entity.MessageReaction;
import com.zalo.modules.message.entity.MessageStatus;
import com.zalo.modules.conversation.entities.ConversationType;
import com.zalo.modules.message.entity.DeliveryStatus;
import com.zalo.modules.message.entity.MessageType;
import com.zalo.modules.conversation.entities.Conversation;
import com.zalo.modules.conversation.entities.ConversationMember;
import com.zalo.modules.media.dtos.requests.MediaRequest;
import com.zalo.modules.media.entities.MediaType;
import com.zalo.modules.media.service.MediaInterface;
import com.zalo.modules.user.entities.User;
import com.zalo.modules.user.service.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    MemberService memberService;
    MediaInterface mediaInterface;
    SystemMessageInterface systemMessageInterface;
    UserOnlineStorage userOnlineStorage;
    PushNotificationService pushNotificationService;
    UserRepository userRepository;

    public void sendMessage(Long conversationId, Long senderId, CreateMessageRequest dto) throws IOException {
        // optional check replyTo exists
        if (dto.replyToId != null) messageRepo.findById(dto.replyToId).orElseThrow();

        List<MediaRequest> attachments = dto.attachments;
        int fileCount = (attachments != null) ? attachments.size() : 0;

        if (dto.contentType == MessageType.TEXT || dto.contentType == MessageType.IMAGE) {
            // TRƯỜNG HỢP 1: Nhiều file (> 1) -> Tách TEXT và MEDIA riêng
            if (fileCount > 1) {
                // Gửi tin nhắn TEXT trước (nếu có nội dung)
                if (dto.content != null && !dto.content.trim().isEmpty()) {
                    saveAndNotifyMessage(conversationId, senderId, dto.content, MessageType.TEXT, null, dto.replyToId, dto.linkMetadata);
                }
                // Gửi tin nhắn MEDIA chứa toàn bộ list ảnh (content lúc này thường để null hoặc tùy bạn)
                saveAndNotifyMessage(conversationId, senderId, null, MessageType.IMAGE, attachments, null, null);
            }
            // TRƯỜNG HỢP 2: 1 file hoặc 0 file -> Gộp chung
            else {
                MessageType type = fileCount == 1 ? MessageType.IMAGE : MessageType.TEXT;
                saveAndNotifyMessage(conversationId, senderId, dto.content, type, attachments, dto.replyToId, dto.linkMetadata);
            }
        } else {
            saveAndNotifyMessage(conversationId, senderId, dto.content, MessageType.FILE, attachments, dto.replyToId, null);
        }
    }

    private void saveAndNotifyMessage(Long conversationId, Long senderId, String content,
                                      MessageType type, List<MediaRequest> attachments, Long replyId, LinkPreviewResponse linkMetadata) {
        if (content == null && attachments.isEmpty()) return;

        // 1. Lưu Message chính
        Message m = Message.builder()
                .conversationId(conversationId)
                .senderId(senderId)
                .content(content)
                .contentType(type)
                .linkMetadata(linkMetadata)
                .replyToMessageId(replyId)
                .build();
        Message newMess = messageRepo.save(m);

        // 2. Lưu danh sách Media (nếu có)
        if (attachments != null && !attachments.isEmpty()) {
            List<MediaRequest> mediaEntities = attachments.stream().map(f -> {
                MediaRequest media = new MediaRequest();
                media.setName(f.name);
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

        MessageResponse messageResponse = new MessageResponse(finalMsg, "sender", "replyToMessage");
        if (finalMsg.getContentType() == MessageType.FILE || finalMsg.getContentType() == MessageType.IMAGE) {
            List<Media> medias = mediaInterface.findByModuleIdInAndModuleType(List.of(finalMsg.getId()), MediaType.MESSAGE);
            Map<Long, List<MediaResponse>> mapAttachment = medias.stream()
                    .collect(Collectors.groupingBy(
                            Media::getModuleId,
                            Collectors.mapping(
                                    media -> new MediaResponse(media, "createdBy"),
                                    Collectors.toList()
                            )
                    ));

            messageResponse.setAttachments(mapAttachment.getOrDefault(m.getId(), List.of()));
        }

        websocketService.sendMessage(messageResponse, convRes, members);

        if (content != null) {
            if (convRes.getAvatar() != null) {
                processMentionAndSendNotify(content, convRes.getName(), convRes.getAvatar().getSecureUrl(), conversationId);
            } else {
                processMentionAndSendNotify(content, convRes.getName(), "", conversationId);
            }
        }

        String avatar = "";
        if (convRes.getType() == ConversationType.PRIVATE) {
            if (convRes.cu == senderId) {
                avatar = convRes.getRecipient().getAvatar() != null ? convRes.getRecipient().getAvatar().getUrl() : "";
            } else {
                avatar = convRes.getCreatedBy().getAvatar() != null ? convRes.getCreatedBy().getAvatar().getUrl() : "";
            }
        } else {
            avatar = convRes.getAvatar() != null ? convRes.getAvatar().getSecureUrl() : "";
        }
        processBubbleNotify(messageResponse, avatar, conversationId, members.stream()
                .map(ConversationMember::getUserId) // Hoặc .map(member -> member.getUserId())
                .collect(Collectors.toList()));
    }

    public void processMentionAndSendNotify(String rawContent, String groupName, String imageUrl, Long conversationId) {
        // 1. Lấy danh sách ID từ chuỗi [mention:ID]
        Set<Long> mentionedIds = extractMentionedIds(rawContent);

        List<Long> offlineIds = mentionedIds.stream()
                .filter(id -> !userOnlineStorage.isOnline(id))
                .toList();

        if (!offlineIds.isEmpty()) {
            // 2. Truy vấn tất cả User offline trong 1 câu lệnh SQL duy nhất
            List<User> offlineUsers = userRepository.findAllById(offlineIds);

            // 3. Duyệt qua danh sách User để gửi thông báo
            offlineUsers.forEach(user -> {
                String token = user.getFcmToken();
                if (token != null && !token.isEmpty()) {
                    pushNotificationService.sendAdvancedNotification(
                            token,
                            groupName,
                            "Bạn vừa được nhắc đến trong nhóm",
                            conversationId,
                            imageUrl,
                            "tagMessage"
                    );
                }
            });
        }
    }

    private Set<Long> extractMentionedIds(String content) {
        Set<Long> ids = new HashSet<>();
        //(\d+): Group 1 để lấy ID (chỉ lấy số).
        String regex = "\\[mention:(\\d+)\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            ids.add(Long.parseLong(matcher.group(1)));
        }

        return ids;
    }

    public void processBubbleNotify(MessageResponse mess, String imageUrl, Long conversationId, List<Long> memberIds) {
        List<Long> offlineIds = memberIds.stream()
                .filter(id -> !userOnlineStorage.isOnline(id))
                .toList();

        if (!offlineIds.isEmpty()) {
            // 2. Truy vấn tất cả User offline trong 1 câu lệnh SQL duy nhất
            List<User> offlineUsers = userRepository.findAllById(offlineIds);

            // 3. Duyệt qua danh sách User để gửi thông báo
            offlineUsers.forEach(user -> {
                String token = user.getFcmToken();
                if (token != null && !token.isEmpty()) {
                    pushNotificationService.sendBubbleNotification(
                            token,
                            mess.getSender().getUsername(),
                            mess.getSender().getUsername() + ": " + mess.getContent(),
                            conversationId,
                            imageUrl
                    );
                }
            });
        }
    }

    public Page<MessageResponse> fetchMessages(Long conversationId, MessageFilter filter) {
        filter.setConversationId(conversationId);

        if (filter.getContentType() == MessageType.FILE || filter.getContentType() == MessageType.IMAGE || filter.getContentType() == MessageType.LINK) {
            filter.setStt(1);
        }

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

            systemMessageInterface.getSystemMetadata(e, m);

            return m;
        });
    }

    public Message findByIdWithRelationShip(Long id, Long conversationId) {
        return messageRepo.findOneWithRelationShip(id, conversationId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
    }

    public Long markRead(Long conversationId, Long userId, Long messageId) {
        Optional<MessageStatus> opt = statusRepo.findByMessageIdAndUserId(messageId, userId);
        opt.ifPresent(ms -> {
            ms.setStatus(DeliveryStatus.READ);
            statusRepo.save(ms);
        });

        memberRepo.findByConversationIdAndUserId(conversationId, userId).ifPresent(m -> {
            m.setLastReadMessageId(messageId);
            memberRepo.save(m);
        });

        return conversationRepository.countUnread(conversationId, userId);
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

    public LinkPreviewResponse getLinkPreview(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(5000)
                    .followRedirects(true)
                    .get();
            return LinkPreviewResponse.builder()
                    .title(getMetaTag(doc, "og:title") != null ? getMetaTag(doc, "og:title") : doc.title())
                    .description(getMetaTag(doc, "og:description"))
                    .image(getMetaTag(doc, "og:image"))
                    .url(url)
                    .build();
        } catch (IOException e) {
            return null;
        }
    }

    private String getMetaTag(Document doc, String attr) {
        Element element = doc.select("meta[property=" + attr + "]").first();
        return element != null ? element.attr("content") : null;
    }

    public void shareMessage(Long senderId, ShareMessageRequest dto) {
        // 1. Tìm tin nhắn gốc
        Message originalMsg = findByIdWithRelationShip(dto.messageId, dto.conversationId);

        // 2. Lấy danh sách media của tin nhắn gốc (nếu có)
        List<Media> originalMedias;
        if (originalMsg.getContentType() == MessageType.IMAGE || originalMsg.getContentType() == MessageType.FILE) {
            originalMedias = mediaInterface.findByModuleIdInAndModuleType(
                    Collections.singletonList(originalMsg.getId()),
                    MediaType.MESSAGE
            );
        } else {
            originalMedias = new ArrayList<>();
        }

        //create message dto
        CreateMessageRequest createMessageRequest = new CreateMessageRequest();
        createMessageRequest.contentType = originalMsg.getContentType();
        createMessageRequest.linkMetadata = originalMsg.getLinkMetadata();
        createMessageRequest.attachments = originalMedias.stream().map(m -> {
            MediaRequest req = new MediaRequest();
            req.secureUrl = m.getSecureUrl();
            req.publicId = m.getPublicId();
            req.moduleId = m.getModuleId();
            req.moduleType = m.getModuleType();
            req.name = m.getName();
            req.bytes = m.getBytes();
            req.format = m.getFormat();
            req.width = m.getWidth();
            req.height = m.getHeight();
            req.resourceType = m.getResourceType();
            return req;
        }).toList();

        // if be imaged -> isAttachDesc must equal true
        if(dto.isAttachDesc && originalMsg.getContentType() == MessageType.IMAGE){
            createMessageRequest.content = originalMsg.getContent();
        }

        if(originalMsg.getContentType() != MessageType.IMAGE){
            createMessageRequest.content = originalMsg.getContent();
        }

        CreateMessageRequest createMessageRequest2 = new CreateMessageRequest();
        if(!dto.content.isEmpty()){
            createMessageRequest2.content = dto.content;
            createMessageRequest2.contentType = MessageType.TEXT;
        }

        // 3. Lặp qua danh sách các hội thoại được chia sẻ đến
        dto.conversationIds.forEach(targetConvId -> {
            try {
                sendMessage(targetConvId, senderId, createMessageRequest);
                if(!dto.content.isEmpty()){
                    sendMessage(targetConvId, senderId, createMessageRequest2);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
