package com.zalo.configuration;

import com.zalo.configuration.anotation.CheckConversationMember;
import com.zalo.model.ConversationMember;
import com.zalo.model.User;
import com.zalo.repository.ConversationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class ConversationMemberAspect {

    private final ConversationMemberRepository repository;

    @Before("@annotation(check)")
    public void checkMember(JoinPoint joinPoint, CheckConversationMember check) {

        Object[] args = joinPoint.getArgs();
        String paramName = check.conversationIdParam();

        Long conversationId = null;
        Long userId = null;

        // 👉 Lấy param từ method
        for (Object arg : args) {

            // lấy conversationId
            if (arg instanceof Long && conversationId == null) {
                conversationId = (Long) arg;
            }

            // lấy user từ @CurrentUser
            if (arg instanceof User user) {
                userId = user.getId();
            }
        }

        if (conversationId == null || userId == null) {
            throw new RuntimeException("Thiếu dữ liệu check");
        }

        Optional<ConversationMember> isMember = repository
                .findByConversationIdAndUserId(conversationId, userId);

        if (isMember.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Bạn không thuộc cuộc trò chuyện này"
            );
        }
    }
}
