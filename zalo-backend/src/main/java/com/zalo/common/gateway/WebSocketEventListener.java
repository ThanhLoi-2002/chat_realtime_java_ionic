package com.zalo.common.gateway;

import com.zalo.modules.conversation.entities.ConversationMember;
import com.zalo.modules.conversation.service.ConversationMemberRepository;
import com.zalo.common.service.WebsocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final UserOnlineStorage userOnlineStorage;
    private final ConversationMemberRepository memberRepository;
    private final WebsocketService websocketService;

    public Long getUserIdFromSession(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        return (Long) Objects.requireNonNull(accessor.getSessionAttributes()).get("userId");
    }

    @EventListener
    public void handleConnect(SessionConnectEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        Principal user = accessor.getUser();

        if (user == null) {
            // fallback từ session
            user = (Principal) Objects.requireNonNull(accessor.getSessionAttributes()).get("user");
        }

        if (user == null) {
            System.out.println("User null, skip");
            return;
        }

        String sessionId = accessor.getSessionId();
        Long userId = Long.parseLong(user.getName());

        userOnlineStorage.addSession(userId, sessionId);

        websocketService.broadcastStatus(userId, true);

        System.out.println("User CONNECT: " + userId + " session=" + sessionId);
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = accessor.getSessionId();

        Long userId = userOnlineStorage.getUserIdBySessionId(sessionId); // 🔥 chuẩn nhất

        if (userId != null) {
            userOnlineStorage.removeSession(userId, sessionId);
            websocketService.broadcastStatus(userId, false);
            System.out.println("User DISCONNECT: " + userId + " session=" + sessionId);
        }
    }

    @MessageMapping("/chat.typing")
    public void typing(Map<String, Object> payload) {
        long conversationId = Long.parseLong(payload.get("conversationId").toString());

        List<ConversationMember> members = memberRepository.findByConversationId(
                ((Number) payload.get("conversationId")).longValue()
        );

        for (ConversationMember member : members) {
            Set<String> sessions = userOnlineStorage.getSessions(member.getUserId());

            for (String sessionId : sessions) {

                messagingTemplate.convertAndSendToUser(
                        sessionId,
                        "/queue/chat.typing." + conversationId,
                        payload,
                        userOnlineStorage.createHeaders(sessionId)
                );
            }
        }
        System.out.println("Send queue: /queue/chat.typing." + conversationId);
    }

    @MessageMapping("/check.userOnline")
    public void checkUserOnline(Map<String, Object> payload) {
        long recipientId = Long.parseLong(payload.get("recipientId").toString());

        Set<String> recipientSessions = userOnlineStorage.getSessions(recipientId);
        websocketService.broadcastStatus(recipientId, !recipientSessions.isEmpty());
//
//        messagingTemplate.convertAndSend(
//                "/queue/userOnline." + recipientId,
//                (Object) Map.of(
//                        "isOnline", !recipientSessions.isEmpty()
//                )
//        );
    }
}
