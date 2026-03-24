package com.zalo.configuration;

import com.zalo.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService; // bạn tự implement

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // chỉ xử lý lúc CONNECT
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            List<String> authHeaders = accessor.getNativeHeader("Authorization");

            if (authHeaders == null || authHeaders.isEmpty()) {
                throw new IllegalArgumentException("Missing Authorization header");
            }

            String token = authHeaders.get(0).replace("Bearer ", "");

            // 🔥 decode JWT
            Claims claims = jwtService.extractAllClaims(token);
            Long userId = claims.get("id", Long.class);

            Principal principal = userId::toString;

            // 🔥 QUAN TRỌNG
            accessor.setUser(principal);

            // 🔥 FIX: lưu vào session
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            if (sessionAttributes != null) {
                sessionAttributes.put("user", principal);
            }

            System.out.println("👤 CONNECT USER: " + Objects.requireNonNull(accessor.getUser()).getName());
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            System.out.println("📡 SUBSCRIBE: " + accessor.getDestination());
        }

        return message;
    }


}
