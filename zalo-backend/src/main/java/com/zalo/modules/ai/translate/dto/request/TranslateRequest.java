package com.zalo.modules.ai.translate.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TranslateRequest {
    String text;
    String sourceLang;
    String targetLang;
}
