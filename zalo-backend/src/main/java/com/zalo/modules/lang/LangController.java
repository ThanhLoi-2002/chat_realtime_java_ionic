package com.zalo.modules.lang;

import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.common.configuration.anotation.ResponseMessage;
import com.zalo.common.filter.LangFilter;
import com.zalo.modules.lang.dto.request.LangCreationRequest;
import com.zalo.modules.lang.dto.request.LangUpdateRequest;
import com.zalo.modules.lang.dto.response.LangResponse;
import com.zalo.modules.user.entities.User;
import com.zalo.modules.lang.service.LangService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/languages")
@RequiredArgsConstructor
public class LangController {

    private final LangService langService;

    @GetMapping
    public Page<LangResponse> getAll(@ModelAttribute LangFilter filter) {
        return langService.getAll(filter);
    }

    @GetMapping("/{id}")
    public LangResponse getById(@PathVariable Long id) {
        return langService.getById(id);
    }

    @GetMapping("/getByLang/{lang}")
    public Map<String, String> getByLang(@PathVariable String lang) {
        return langService.getByLang(lang);
    }

    @PostMapping
    @ResponseMessage("success")
    public LangResponse create(@RequestBody @Valid LangCreationRequest lang, @CurrentUser User user) {
        return langService.create(lang, user.getId());
    }

    @PutMapping("/{id}")
    @ResponseMessage("success")
    public LangResponse update(
            @PathVariable Long id,
            @RequestBody @Valid LangUpdateRequest lang, @CurrentUser User user
    ) {
        return langService.update(id, lang, user.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseMessage("success")
    public void delete(@PathVariable Long id) {
        langService.delete(id);
    }
}