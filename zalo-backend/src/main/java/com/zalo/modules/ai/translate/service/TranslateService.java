package com.zalo.modules.ai.translate.service;

import com.zalo.common.dto.ApiResponse;
import com.zalo.common.service.AiService;
import com.zalo.modules.ai.translate.dto.request.DetectLangRequest;
import com.zalo.modules.ai.translate.dto.request.TranslateRequest;
import com.zalo.modules.ai.translate.dto.response.DetectLangResponse;
import com.zalo.modules.ai.translate.dto.response.LanguageResponse;
import com.zalo.modules.ai.translate.dto.response.TranslateResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ibm.icu.util.ULocale;

import java.util.List;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TranslateService {
    AiService aiService;

    private static final List<String> POPULAR_LANGUAGES = List.of(
            "vi",
            "en",
            "zh",
            "ja",
            "ko",
            "th",
            "fr",
            "de",
            "es",
            "ru",
            "ar",
            "id"
    );

    public TranslateResponse translateMessage(TranslateRequest request) {
        return aiService.post(
                "/translate",
                request,
                new ParameterizedTypeReference<ApiResponse<TranslateResponse>>() {
                }
        );
    }

    public DetectLangResponse detectLanguage(DetectLangRequest text) {
        return aiService.post(
                "/translate/detect-language",
                text,
                new ParameterizedTypeReference<ApiResponse<DetectLangResponse>>() {
                }
        );
    }

    public List<LanguageResponse> getLanguages(String lang) {

        ULocale displayLocale = new ULocale(lang);

//        return Arrays.stream(ULocale.getISOLanguages())
//                .distinct()
//                .map(code -> {
//                    ULocale locale = new ULocale(code);
//
//                    return new LanguageResponse(
//                            code,
//                            locale.getDisplayLanguage(displayLocale)
//                    );
//                })
//                .filter(item -> item.getName() != null
//                        && !item.getName().isBlank())
//                .sorted(Comparator.comparing(LanguageResponse::getName))
//                .toList();

        return POPULAR_LANGUAGES.stream()
                .map(code -> new LanguageResponse(
                        code,
                        new ULocale(code)
                                .getDisplayLanguage(displayLocale)
                ))
                .toList();
    }
}
