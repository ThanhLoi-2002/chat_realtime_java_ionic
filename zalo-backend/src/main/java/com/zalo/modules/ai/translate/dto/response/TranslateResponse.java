package com.zalo.modules.ai.translate.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TranslateResponse {
    String originalText;
    String translatedText;
}
