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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final LangService langService;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "";

        if (ex instanceof ResponseStatusException e) {
            status = HttpStatus.valueOf(e.getStatusCode().value());
            message = e.getReason();
        }

        if (message == null || message.isBlank()) {
            message = ex.getMessage(); // fallback
        }

        String lang = LangUtil.getLang();

        System.out.println("message = " + ex.getMessage());
        ex.printStackTrace();
        ApiResponse<?> response = ApiResponse.builder()
//                .message(langService.t(message, lang))
                .message(message)
                .result(null)
                .build();

        return ResponseEntity.status(status)
                .body(response);
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse<?>> handleNull(NullPointerException ex) {

        String lang = LangUtil.getLang();

        System.out.println("message = " + ex.getMessage());
        ex.printStackTrace();

        ApiResponse<?> response = ApiResponse.builder()
//                .message(langService.t(ex.getMessage(), lang))
                .message(ex.getMessage())
                .result(null)
                .build();

        return ResponseEntity.badRequest()
                .body(response);
    }
}
