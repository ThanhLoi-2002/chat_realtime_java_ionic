package com.zalo.service;

import com.zalo.dto.response.Message.MessageResponse;
import com.zalo.dto.response.conversation.ConversationResponse;
import com.zalo.gateway.UserOnlineStorage;
import com.zalo.model.ConversationMember;
import com.zalo.repository.ConversationMemberRepository;
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
    private  final UserOnlineStorage userOnlineStorage;

    public void sendMessage(MessageResponse message, ConversationResponse conv) {
        List<ConversationMember> members = memberRepo.findByConversationId(
                message.getConversationId()
        );

        Map<String, Object> payload = new HashMap<>();
        payload.put("message", message);
        payload.put("conversation", conv);

        for (ConversationMember member : members) {
            Set<String> sessions = userOnlineStorage.getSessions(member.getUserId());

            for (String sessionId : sessions) {

                messagingTemplate.convertAndSendToUser(
                        sessionId,
                        "/queue/chat.newMessages",
                        payload,
                        userOnlineStorage.createHeaders(sessionId)
                );
            }
        }
    }

    public void broadcastStatus(Long userId, boolean online) {
        messagingTemplate.convertAndSend(
                "/topic/user.status",
                (Object) Map.of(
                        "userId", userId,
                        "online", online
                )
        );
    }
}
