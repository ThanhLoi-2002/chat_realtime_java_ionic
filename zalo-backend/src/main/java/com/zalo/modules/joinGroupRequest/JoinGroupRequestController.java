package com.zalo.modules.joinGroupRequest;

import com.zalo.common.configuration.anotation.conversationMember.RequireMemberRole;
import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.modules.conversation.entities.MemberRole;
import com.zalo.modules.joinGroupRequest.dto.request.JoinGroupDto;
import com.zalo.modules.joinGroupRequest.dto.response.JoinGroupRequestResponse;
import com.zalo.modules.joinGroupRequest.service.JoinGroupRequestService;
import com.zalo.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/join-group-request")
@RequiredArgsConstructor
public class JoinGroupRequestController {
    private final JoinGroupRequestService joinGroupRequestService;

    @PostMapping("")
    public void joinGroupRequest(@CurrentUser User user, @RequestBody JoinGroupDto dto) {
        joinGroupRequestService.requestToJoinGroup(user.getId(), dto);
    }

    @GetMapping("/{conversationId}")
    @RequireMemberRole(memberRoles = {MemberRole.GOLDEN_KEY, MemberRole.SILVER_KEY})
    public List<JoinGroupRequestResponse> getGroupRequest(@CurrentUser User user, @RequestParam Long conversationId) {
        return joinGroupRequestService.getListByConvId(conversationId).stream().map(e -> new JoinGroupRequestResponse(e, "createdBy")).toList();
    }
}
