package com.zalo.common.configuration.anotation.currentUser;

import com.zalo.common.configuration.json.G;
import com.zalo.modules.admin.system.user.dto.response.UserPayload;
import com.zalo.modules.admin.system.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && parameter.getParameterType().equals(UserPayload.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        // 1. Lấy thông tin Authentication hiện tại từ kho lưu trữ bảo mật của Spring
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Kiểm tra nếu chưa được xác thực (chưa qua JwtFilter hoặc token lỗi)
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        // 3. Lấy Principal ra (Nơi bạn đã nhét Object 'user' vào ở JwtFilter)
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserPayload user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user principal type");
        }

        log.info("Lấy thành công user từ SecurityContextHolder: user={}", G.toJson(user));

        // 4. Trả về trực tiếp cho tham số @CurrentUser ở Controller, không cần setAttribute thủ công nữa
        return user;
    }
}
