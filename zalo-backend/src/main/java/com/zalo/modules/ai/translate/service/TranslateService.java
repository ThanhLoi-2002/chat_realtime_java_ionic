package com.zalo.modules.ai.translate.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.zalo.common.dto.ApiResponse;
import com.zalo.common.service.AiService;
import com.zalo.modules.ai.translate.dto.request.TranslateRequest;
import com.zalo.modules.ai.translate.dto.response.TranslateResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TranslateService {
    AiService aiService;

    public TranslateResponse translateMessage(TranslateRequest request) {
        return aiService.post(
                "/translate",
                request,
                new ParameterizedTypeReference<ApiResponse<TranslateResponse>>() {}
        );

//        return aiService.aiWebClient()
//                .post()
//                .uri("/translate")
//                .bodyValue(request)
//                .retrieve()
//                .onStatus(
//                        HttpStatusCode::isError,
//                        res -> res.bodyToMono(new ParameterizedTypeReference<ApiResponse<TranslateResponse>>() {})
//                                .flatMap(error ->
//                                        Mono.error(new ResponseStatusException(
//                                                HttpStatus.BAD_GATEWAY,
//                                                error.getMessage()
//                                        ))
//                                )
//                )
//                .bodyToMono(new ParameterizedTypeReference<ApiResponse<TranslateResponse>>() {})
//                .block().getResult();
    }
}
