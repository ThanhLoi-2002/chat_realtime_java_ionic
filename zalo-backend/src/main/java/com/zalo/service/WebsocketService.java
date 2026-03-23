package com.zalo.service;

import com.zalo.dto.response.Message.MessageResponse;
import com.zalo.dto.response.conversation.ConversationResponse;
import com.zalo.model.ConversationMember;
import com.zalo.repository.ConversationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class WebsocketService {
    private final ConversationMemberRepository memberRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final ConversationService conversationService;

    public void sendMessage(MessageResponse message, ConversationResponse conv) {
        List<ConversationMember> members = memberRepo.findByConversationId(
                message.getConversationId()
        );

        Map<String, Object> payload = new HashMap<>();
        payload.put("message", message);
        payload.put("conversation", conv);

        for (ConversationMember member : members) {
            System.out.println("Send to userId: " + member.getUserId().toString());
            messagingTemplate.convertAndSend(
//                    member.getUserId().toString(),
                    "/topic/chat.newMessages",
                    (Object) payload
            );
        }

        // send to user who opening this conversation
//        messagingTemplate.convertAndSend(
//                "/topic/chat.conversation." + message.getConversationId() + ".newMessage",
//                message
//        );
    }
}
