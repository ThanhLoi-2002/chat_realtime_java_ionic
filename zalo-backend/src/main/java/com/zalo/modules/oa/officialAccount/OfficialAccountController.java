package com.zalo.modules.oa.officialAccount;

import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.modules.admin.system.user.dto.response.UserPayload;
import com.zalo.modules.oa.officialAccount.dto.request.CreateOaRequest;
import com.zalo.modules.oa.officialAccount.dto.request.UpdateOaRequest;
import com.zalo.modules.oa.officialAccount.dto.response.OaResponse;
import com.zalo.modules.oa.officialAccount.entity.OfficialAccount;
import com.zalo.modules.oa.officialAccount.service.OfficialAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oa/official-account")
public class OfficialAccountController {

    private final OfficialAccountService officialAccountService;

    @PostMapping
    public OaResponse create(
            @CurrentUser UserPayload user,
            @Valid @RequestBody CreateOaRequest request
    ) {
        OfficialAccount oa = officialAccountService.create(
                user,
                request
        );

        return new OaResponse(oa);
    }

    @PutMapping("/{id}")
    public OaResponse update(
            @CurrentUser UserPayload user,
            @PathVariable Long id,
            @Valid @RequestBody UpdateOaRequest request
    ) {

        OfficialAccount oa = officialAccountService.update(
                user,
                id,
                request
        );

        return new OaResponse(oa);
    }

    @GetMapping("/{id}")
    public OaResponse detail(
            @CurrentUser UserPayload user,
            @PathVariable Long id
    ) {
        officialAccountService.checkMember(id, user.getId() );

        OfficialAccount oa = officialAccountService.getById(id);

        return new OaResponse(oa);
    }

    @GetMapping("/my")
    public List<OaResponse> my(
            @CurrentUser UserPayload user
    ) {
        return officialAccountService
                .getMyOfficialAccounts(user)
                .stream()
                .map(OaResponse::new)
                .toList();
    }

    @DeleteMapping("/{id}")
    public void delete(
            @CurrentUser UserPayload user,
            @PathVariable Long id
    ) {
        officialAccountService.delete(user, id);
    }
}
