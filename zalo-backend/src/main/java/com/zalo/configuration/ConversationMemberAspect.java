package com.zalo.configuration;

import com.zalo.configuration.anotation.CheckConversationMember;
import com.zalo.modules.conversation.entities.ConversationMember;
import com.zalo.modules.user.entities.User;
import com.zalo.modules.conversation.service.ConversationMemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class ConversationMemberAspect {
    private final ConversationMemberRepository repository;

    @Before("@annotation(check)")
    public void checkMember(JoinPoint joinPoint, CheckConversationMember check) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("Không thể lấy Request từ context hiện tại");
        }
        HttpServletRequest request = attributes.getRequest();

        // 2. Lấy các biến Path Variable từ URL
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        // 3. Lấy conversationId dựa trên tên trong Annotation
        String paramName = check.conversationIdParam(); // mặc định là "conversationId"
        String idValue = pathVariables.get(paramName);

        if (idValue == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "URL thiếu tham số " + paramName);
        }

        Long conversationId = Long.parseLong(idValue);

        Long userId = null;
//        System.out.println("targetParamName " + targetParamName + " parameterNames: " + Arrays.toString(parameterNames));

        // 👉 Lấy param từ method
        for (int i = 0; i < parameterNames.length; i++) {
            // 1. Tìm conversationId theo tên đã cấu hình
//            if (parameterNames[i].equals(targetParamName)) {
//                if (args[i] instanceof Long) {
//                    conversationId = (Long) args[i];
//                }
//            }

            // 2. Tìm User từ tham số (dựa trên kiểu dữ liệu User)
            if (args[i] instanceof User user) {
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
