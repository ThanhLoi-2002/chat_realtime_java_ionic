package com.zalo.common.configuration.anotation.conversationMember;

import com.zalo.modules.admin.system.user.dto.response.UserPayload;
import com.zalo.modules.app.conversation.service.ConversationMemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
public class ConversationMemberAspect {
    private final ConversationMemberRepository repository;

    @Before("@annotation(check)")
    public void checkMember(JoinPoint joinPoint, CheckConversationMember check) {

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new RuntimeException("Không thể lấy Request từ context hiện tại");
        }

        HttpServletRequest request = attributes.getRequest();

        UserPayload currentUser =
                (UserPayload) request.getAttribute("currentUser");

        if (currentUser == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Unauthorized"
            );
        }

        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables =
                (Map<String, String>) request.getAttribute(
                        HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE
                );

        String paramName = check.conversationIdParam();
        String idValue = pathVariables.get(paramName);

        if (idValue == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "URL thiếu tham số " + paramName
            );
        }

        Long conversationId = Long.parseLong(idValue);

        boolean isMember = repository
                .findByConversationIdAndUserId(
                        conversationId,
                        currentUser.getId()
                )
                .isPresent();

        if (!isMember) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Bạn không thuộc cuộc trò chuyện này"
            );
        }
    }
}
