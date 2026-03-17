package com.zalo.controller;

import com.zalo.configuration.anotation.ResponseMessage;
import com.zalo.dto.request.Lang.LangCreationRequest;
import com.zalo.dto.request.Lang.LangUpdateRequest;
import com.zalo.dto.response.Lang.LangResponse;
import com.zalo.model.Lang;
import com.zalo.service.LangService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/languages")
@RequiredArgsConstructor
public class LangController {

    private final LangService langService;

    @GetMapping
    public List<LangResponse> getAll() {
        return langService.getAll();
    }

    @GetMapping("/{id}")
    public LangResponse getById(@PathVariable Long id) {
        return langService.getById(id);
    }

    @GetMapping("/getByLang/{lang}")
    public Map<String,String> getByLang(@PathVariable String lang) {
        return langService.getByLang(lang);
    }

    @PostMapping
    @ResponseMessage("success")
    public LangResponse create(@RequestBody @Valid LangCreationRequest lang) {
        return langService.create(lang);
    }

    @PutMapping("/{id}")
    @ResponseMessage("success")
    public LangResponse update(
            @PathVariable Long id,
            @RequestBody @Valid LangUpdateRequest lang
    ) {
        return langService.update(id, lang);
    }

    @DeleteMapping("/{id}")
    @ResponseMessage("success")
    public void delete(@PathVariable Long id) {
        langService.delete(id);
    }
}