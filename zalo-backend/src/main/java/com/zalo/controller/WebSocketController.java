package com.zalo.controller;

import com.zalo.configuration.G;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(String message) {

        System.out.println("Receive: " + message);

        messagingTemplate.convertAndSend(
                "/topic/chat.newMessage",
                message
        );

    }

    @MessageMapping("/chat.typing")
    public void typing(Map<String, Object> payload) {
        long conversationId = Long.parseLong(payload.get("conversationId").toString());
        System.out.println("Receive: " + conversationId);

        messagingTemplate.convertAndSend(
                "/topic/chat.typing." + conversationId,
                (Object) payload
        );
        System.out.println("Send topic: /topic/chat.typing." + conversationId);
    }
}
