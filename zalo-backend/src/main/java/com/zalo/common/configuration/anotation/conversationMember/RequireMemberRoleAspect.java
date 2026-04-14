package com.zalo.common.configuration.anotation.conversationMember;

import com.zalo.modules.conversation.entities.ConversationMember;
import com.zalo.modules.conversation.entities.MemberRole;
import com.zalo.modules.conversation.service.ConversationMemberRepository;
import com.zalo.modules.user.entities.User;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class RequireMemberRoleAspect {
    private final ConversationMemberRepository repository;

    @Before("@annotation(role)")
    public void checkMemberRole(JoinPoint joinPoint, RequireMemberRole role) {

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

        // 3. Lấy role dựa trên tên trong Annotation
        MemberRole[] rolesArray = role.memberRoles();
        List<MemberRole> rolesList = List.of(rolesArray);

        String idValue = pathVariables.get("conversationId");

        if (idValue == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "URL thiếu tham số conversationId");
        }

        Long conversationId = Long.parseLong(idValue);

        Long userId = null;

        // 👉 Lấy param từ method
        for (int i = 0; i < parameterNames.length; i++) {
            // 2. Tìm User từ tham số (dựa trên kiểu dữ liệu User)
            if (args[i] instanceof User user) {
                userId = user.getId();
            }
        }

        if (userId == null) {
            throw new RuntimeException("Thiếu dữ liệu userId");
        }

        Optional<ConversationMember> member = repository
                .findByConversationIdAndUserId(conversationId, userId);

        if (member.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Bạn không thuộc cuộc trò chuyện này"
            );
        }

        if (!rolesList.contains(member.get().getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện hành động này!");
        }
    }
}
