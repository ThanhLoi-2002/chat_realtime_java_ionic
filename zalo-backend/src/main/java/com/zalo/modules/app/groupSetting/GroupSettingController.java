package com.zalo.modules.app.groupSetting;

import com.zalo.common.configuration.anotation.conversationMember.RequireMemberRole;
import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.modules.app.conversation.entities.MemberRole;
import com.zalo.modules.app.groupSetting.entities.GroupSetting;
import com.zalo.modules.app.groupSetting.service.GroupSettingService;
import com.zalo.modules.admin.system.user.dto.response.UserPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group-settings/{conversationId}")
@RequiredArgsConstructor
public class GroupSettingController {
    private final GroupSettingService service;

    @PostMapping()
    @RequireMemberRole(memberRoles = {MemberRole.GOLDEN_KEY, MemberRole.SILVER_KEY})
    public void saveSettings(@CurrentUser UserPayload user, @PathVariable Long conversationId, @RequestBody GroupSetting dto) {
        service.saveSetting(conversationId, user.getId(), dto);
    }
}
