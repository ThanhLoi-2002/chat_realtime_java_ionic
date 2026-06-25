package com.zalo.modules.admin.system.lang;

import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.common.configuration.anotation.ResponseMessage;
import com.zalo.common.configuration.anotation.permission.RequiresPermission;
import com.zalo.common.filter.LangFilter;
import com.zalo.common.util.PermissionConstant;
import com.zalo.modules.admin.system.lang.dto.request.LangCreationRequest;
import com.zalo.modules.admin.system.lang.dto.request.LangUpdateRequest;
import com.zalo.modules.admin.system.lang.dto.response.LangResponse;
import com.zalo.modules.admin.system.user.dto.response.UserPayload;
import com.zalo.modules.admin.system.lang.service.LangService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/system/languages")
@RequiredArgsConstructor
public class LangController {

    private final LangService langService;

    @GetMapping
    @RequiresPermission(PermissionConstant.ADMIN.Lang.READ)
    public Page<LangResponse> getAll(@ModelAttribute LangFilter filter) {
        return langService.getAll(filter);
    }

    @GetMapping("/{id}")
    @RequiresPermission(PermissionConstant.ADMIN.Lang.READ)
    public LangResponse getById(@PathVariable Long id) {
        return langService.getById(id);
    }

    @GetMapping("/getByLang/{lang}")
    public Map<String, String> getByLang(@PathVariable String lang) {
        return langService.getByLang(lang);
    }

    @PostMapping
    @ResponseMessage("success")
    @RequiresPermission(PermissionConstant.ADMIN.Lang.CREATE)
    public LangResponse create(@RequestBody @Valid LangCreationRequest lang, @CurrentUser UserPayload user) {
        return langService.create(lang, user.getId());
    }

    @PutMapping("/{id}")
    @ResponseMessage("success")
    @RequiresPermission(PermissionConstant.ADMIN.Lang.UPDATE)
    public LangResponse update(
            @PathVariable Long id,
            @RequestBody @Valid LangUpdateRequest lang, @CurrentUser UserPayload user
    ) {
        return langService.update(id, lang, user.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseMessage("success")
    @RequiresPermission(PermissionConstant.ADMIN.Lang.DELETE)
    public void delete(@PathVariable Long id) {
        langService.delete(id);
    }
}