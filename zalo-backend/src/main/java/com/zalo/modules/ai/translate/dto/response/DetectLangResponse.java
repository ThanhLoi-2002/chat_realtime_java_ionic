package com.zalo.modules.ai.translate.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetectLangResponse {
    String language;
    Double confidence;
}
