package com.zalo.gateway;

import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserOnlineStorage {

    // userId -> set of sessionIds
    private final Map<Long, Set<String>> userSessions = new ConcurrentHashMap<>();

    // 🔥 NEW: sessionId -> userId
    private final Map<String, Long> sessionUserMap = new ConcurrentHashMap<>();

    public void addSession(Long userId, String sessionId) {
        userSessions
                .computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet())
                .add(sessionId);

        // 🔥 lưu ngược
        sessionUserMap.put(sessionId, userId);
    }

    public void removeSession(Long userId, String sessionId) {
        Set<String> sessions = userSessions.get(userId);

        if (sessions != null) {
            sessions.remove(sessionId);

            if (sessions.isEmpty()) {
                userSessions.remove(userId);
            }
        }

        // 🔥 remove map ngược
        sessionUserMap.remove(sessionId);
    }

    // 🔥 NEW: lấy userId từ sessionId
    public Long getUserIdBySessionId(String sessionId) {
        return sessionUserMap.get(sessionId);
    }

    public boolean isOnline(Long userId) {
        return userSessions.containsKey(userId);
    }

    public Set<String> getSessions(Long userId) {
        return userSessions.getOrDefault(userId, Collections.emptySet());
    }

    public Set<Long> getOnlineUsers() {
        return userSessions.keySet();
    }

    public MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor =
                SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);

        headerAccessor.setSessionId(sessionId);   // 🔥 QUAN TRỌNG
        headerAccessor.setLeaveMutable(true);

        return headerAccessor.getMessageHeaders();
    }
}