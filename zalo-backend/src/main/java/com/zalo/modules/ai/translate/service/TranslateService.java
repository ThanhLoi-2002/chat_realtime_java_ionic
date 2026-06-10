package com.zalo.modules.ai.translate.service;

import com.zalo.common.dto.ApiResponse;
import com.zalo.common.service.AiService;
import com.zalo.modules.ai.translate.dto.request.DetectLangRequest;
import com.zalo.modules.ai.translate.dto.request.TranslateRequest;
import com.zalo.modules.ai.translate.dto.response.DetectLangResponse;
import com.zalo.modules.ai.translate.dto.response.TranslateResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    }

    public DetectLangResponse detectLanguage(DetectLangRequest text) {
        return aiService.post(
                "/translate/detect-language",
                text,
                new ParameterizedTypeReference<ApiResponse<DetectLangResponse>>() {}
        );
    }
}
