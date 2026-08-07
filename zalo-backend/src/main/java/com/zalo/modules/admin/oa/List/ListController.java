package com.zalo.modules.admin.oa.List;

import com.zalo.common.configuration.anotation.ResponseMessage;
import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.common.filter.OaFilter;
import com.zalo.modules.admin.oa.List.service.ListService;
import com.zalo.modules.admin.system.user.dto.response.UserPayload;
import com.zalo.modules.oa.officialAccount.dto.response.OaResponse;
import com.zalo.modules.oa.officialAccount.entity.OaStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/oa/list")
public class ListController {
    private final ListService listService;

    @GetMapping
    public Page<OaResponse> getAll(@ModelAttribute OaFilter filter, @CurrentUser UserPayload user) {

        return listService.getAll(filter).map(OaResponse::new);
    }

    @PutMapping("/status/{id}")
    @ResponseMessage("success")
    public OaResponse updateStatus(
            @PathVariable Long id,
            @RequestParam OaStatus status, @CurrentUser UserPayload user
    ) {
        return new OaResponse(listService.updateStatus(id, status, user.getId()));
    }
}
