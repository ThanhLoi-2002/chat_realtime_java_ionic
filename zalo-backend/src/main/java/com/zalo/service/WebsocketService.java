package com.zalo.service;

import com.zalo.dto.response.Message.MessageReactionResponse;
import com.zalo.dto.response.Message.MessageResponse;
import com.zalo.dto.response.Conversation.ConversationResponse;
import com.zalo.gateway.UserOnlineStorage;
import com.zalo.model.ConversationMember;
import com.zalo.model.Message;
import com.zalo.model.MessageReaction;
import com.zalo.repository.ConversationMemberRepository;
import com.zalo.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class WebsocketService {
    private final ConversationMemberRepository memberRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserOnlineStorage userOnlineStorage;
    public final ConversationRepository conversationRepository;

    public void sendMessage(MessageResponse message, ConversationResponse conv, List<ConversationMember> members) {
        for (ConversationMember member : members) {
            Set<String> sessions = userOnlineStorage.getSessions(member.getUserId());

            Long countUnread = conversationRepository.countUnread(conv.getId(), member.getUserId());

            conv.setUnread(countUnread);

            Map<String, Object> payload = new HashMap<>();
            payload.put("message", message);
            payload.put("conversation", conv);

            for (String sessionId : sessions) {
                messagingTemplate.convertAndSendToUser(sessionId, "/queue/chat.newMessages", payload, userOnlineStorage.createHeaders(sessionId));
            }
        }
    }

    public void updateMessage(Message message) {
        List<ConversationMember> members = memberRepo.findByConversationId(message.getConversationId());

        Map<String, Object> payload = new HashMap<>();
        payload.put("message", new MessageResponse(message, "sender", "replyToMessage"));

        for (ConversationMember member : members) {
            Set<String> sessions = userOnlineStorage.getSessions(member.getUserId());

            for (String sessionId : sessions) {

                messagingTemplate.convertAndSendToUser(sessionId, "/queue/chat.updateMessages", payload, userOnlineStorage.createHeaders(sessionId));
            }
        }
    }

    public void addReaction(Long conversationId, MessageReaction mr) {
        List<ConversationMember> members = memberRepo.findByConversationId(conversationId);

        Map<String, Object> payload = new HashMap<>();
        payload.put("reaction", new MessageReactionResponse(mr, "createdBy"));

        for (ConversationMember member : members) {
            Set<String> sessions = userOnlineStorage.getSessions(member.getUserId());

            for (String sessionId : sessions) {

                messagingTemplate.convertAndSendToUser(sessionId, "/queue/chat.conversation." + conversationId + ".addReaction", payload, userOnlineStorage.createHeaders(sessionId));
                System.out.println("/queue/chat.conversation." + conversationId + ".addReaction");
            }
        }
    }

    public void removeAllReactionByUserId(Long conversationId, Long messageId, List<MessageReaction> mrs) {
        List<ConversationMember> members = memberRepo.findByConversationId(conversationId);

        List<MessageReactionResponse> list = mrs.stream().map(m -> new MessageReactionResponse(m, "createdBy")).toList();
        Map<String, Object> payload = new HashMap<>();
        payload.put("reactions", list);
        payload.put("messageId", messageId);

        for (ConversationMember member : members) {
            Set<String> sessions = userOnlineStorage.getSessions(member.getUserId());

            for (String sessionId : sessions) {

                messagingTemplate.convertAndSendToUser(sessionId, "/queue/chat.conversation." + conversationId + ".reactions", payload, userOnlineStorage.createHeaders(sessionId));
            }
        }
    }

    public void broadcastStatus(Long userId, boolean online) {
        messagingTemplate.convertAndSend("/topic/user.status", (Object) Map.of("userId", userId, "online", online));
    }
}
