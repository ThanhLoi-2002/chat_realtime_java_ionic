package com.zalo.configuration;

import com.zalo.configuration.anotation.ResponseMessage;
import com.zalo.dto.response.ApiResponse;
import com.zalo.modules.lang.service.LangService;
import com.zalo.util.LangUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.*;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
@RequiredArgsConstructor
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {
    private final LangService langService;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
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
