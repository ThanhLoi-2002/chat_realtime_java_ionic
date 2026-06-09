package com.zalo.modules.ai.translate;

import com.zalo.modules.ai.translate.dto.request.TranslateRequest;
import com.zalo.modules.ai.translate.dto.response.TranslateResponse;
import com.zalo.modules.ai.translate.service.TranslateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/translate")
@RequiredArgsConstructor
public class TranslateController {
    private final TranslateService translateService;

    @PostMapping("")
    public TranslateResponse translateMessage( @RequestBody TranslateRequest dto) {
        return translateService.translateMessage(dto);
    }
}
