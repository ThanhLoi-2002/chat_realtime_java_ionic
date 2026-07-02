package com.zalo.common.configuration;

import com.zalo.common.configuration.anotation.NoResponseBodyWrap;
import com.zalo.common.configuration.anotation.ResponseMessage;
import com.zalo.common.dto.ApiResponse;
import com.zalo.modules.admin.system.lang.service.LangService;
import com.zalo.common.util.LangUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.*;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
@RequiredArgsConstructor
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {
    private final LangService langService;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        if (returnType.getMethodAnnotation(NoResponseBodyWrap.class) != null) {
            return false;
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String uri = request.getRequestURI();

            // Nếu URL chứa v3/api-docs hoặc swagger-ui -> Bỏ qua không wrap
            if (uri.contains("/v3/api-docs") || uri.contains("/swagger-ui")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        // ❗ nếu đã là ApiResponse thì không wrap nữa
        if (body instanceof ApiResponse) {
            return body;
        }

        ResponseMessage annotation =
                returnType.getMethodAnnotation(ResponseMessage.class);

        String message = "success";

        if (annotation != null) {
            message = annotation.value();
        }

        String lang = LangUtil.getLang();

        return ApiResponse.builder()
//                .message(langService.t(message, lang))
                .message(message)
                .result(body)
                .build();
    }
}
