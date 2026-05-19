package com.zalo.modules.groupSetting;

import com.zalo.common.configuration.anotation.conversationMember.RequireMemberRole;
import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.modules.conversation.entities.MemberRole;
import com.zalo.modules.groupSetting.entities.GroupSetting;
import com.zalo.modules.groupSetting.service.GroupSettingService;
import com.zalo.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group-settings/{conversationId}")
@RequiredArgsConstructor
public class GroupSettingController {
    private final GroupSettingService service;

    @PostMapping()
    @RequireMemberRole(memberRoles = {MemberRole.GOLDEN_KEY, MemberRole.SILVER_KEY})
    public void saveSettings(@CurrentUser User user, @PathVariable Long conversationId, @RequestBody GroupSetting dto) {
        service.saveSetting(conversationId, user.getId(), dto);
    }
}
