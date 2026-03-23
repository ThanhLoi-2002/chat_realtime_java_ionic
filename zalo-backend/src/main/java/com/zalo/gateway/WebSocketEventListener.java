package com.zalo.gateway;

import com.zalo.configuration.G;
import com.zalo.dto.response.Message.MessageResponse;
import com.zalo.model.Conversation;
import com.zalo.model.ConversationMember;
import com.zalo.repository.ConversationMemberRepository;
import com.zalo.service.ConversationService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final UserOnlineStorage storage;
    private final ConversationMemberRepository memberRepository;
    private final ConversationService conversationService;

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

        storage.addSession(userId, sessionId);

        System.out.println("User ONLINE: " + userId);
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = accessor.getSessionId();

        Long userId = storage.getUserIdBySessionId(sessionId); // 🔥 chuẩn nhất

        System.out.println("User DISCONNECT: " + userId + " session=" + sessionId);

        if (userId != null) {
            storage.removeSession(userId, sessionId);
        }
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(MessageResponse message) {
        System.out.println("Receive: " + G.toJson(message));

        List<ConversationMember> members = memberRepository.findByConversationId(
                message.getConversationId()
        );

        Conversation conv = conversationService.findByIdWithRelationShip(message.getConversationId());

        Map<String, Object> payload = new HashMap<>();
        payload.put("message", message);
        payload.put("conversation", conv);
        System.out.println("length: " + members.size());
        for (ConversationMember member : members) {
            System.out.println("Send to userId: " + member.getUserId().toString());
            messagingTemplate.convertAndSendToUser(
                    member.getUserId().toString(),
                    "/queue/messages",
                    payload
            );
        }

        // send to user who opening this conversation
//        messagingTemplate.convertAndSend(
//                "/topic/chat.conversation." + message.getConversationId() + ".newMessage",
//                message
//        );
    }

    @MessageMapping("/chat.typing")
    public void typing(Map<String, Object> payload) {
        long conversationId = Long.parseLong(payload.get("conversationId").toString());

        List<ConversationMember> members = memberRepository.findByConversationId(
                ((Number) payload.get("conversationId")).longValue()
        );

        for (ConversationMember member : members) {
            messagingTemplate.convertAndSendToUser(
                    member.getUserId().toString(),
                    "/topic/chat.typing." + conversationId,
                    (Object) payload
            );
        }
        System.out.println("Send topic: /topic/chat.typing." + conversationId);
    }
}
