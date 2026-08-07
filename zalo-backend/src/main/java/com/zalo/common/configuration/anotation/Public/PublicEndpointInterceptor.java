package com.zalo.common.configuration.anotation.Public;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class PublicEndpointInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Nếu request không phải trỏ vào Method của Controller (ví dụ static resources) thì cho qua
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // Kiểm tra xem Method hoặc Class Controller có gắn @Public không
        boolean isPublicMethod = handlerMethod.hasMethodAnnotation(Public.class);
        boolean isPublicClass = handlerMethod.getBeanType().isAnnotationPresent(Public.class);

        // 1. Nếu có @Public -> Cho qua luôn, không cần check login
        if (isPublicMethod || isPublicClass) {
            return true;
        }

        // 2. Nếu KHÔNG có @Public -> Bắt buộc phải có Authentication hợp lệ trong SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return true; // Đã đăng nhập hợp lệ
        }

        // 3. Chưa đăng nhập mà truy cập endpoint không có @Public -> Trả về lỗi 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Token không hợp lệ hoặc thiếu Token\"}");
        return false;
    }
}
