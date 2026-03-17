package com.zalo.exception;

import com.zalo.dto.response.ApiResponse;
import com.zalo.service.LangService;
import com.zalo.util.LangUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final LangService langService;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {

        String lang = LangUtil.getLang();

        ApiResponse<?> response = ApiResponse.builder()
                .message(langService.t(ex.getMessage(), lang))
                .result(null)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse<?>> handleNull(NullPointerException ex){

        String lang = LangUtil.getLang();

        ApiResponse<?> response = ApiResponse.builder()
                .message(langService.t(ex.getMessage(), lang))
                .result(null)
                .build();

        return ResponseEntity.badRequest()
                .body(response);
    }
}
