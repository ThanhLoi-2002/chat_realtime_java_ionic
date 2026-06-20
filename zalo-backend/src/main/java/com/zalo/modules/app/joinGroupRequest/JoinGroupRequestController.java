package com.zalo.modules.app.joinGroupRequest;

import com.zalo.common.configuration.anotation.conversationMember.RequireMemberRole;
import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.modules.app.conversation.entities.MemberRole;
import com.zalo.modules.app.joinGroupRequest.dto.request.JoinGroupDto;
import com.zalo.modules.app.joinGroupRequest.dto.response.JoinGroupRequestResponse;
import com.zalo.modules.app.joinGroupRequest.service.JoinGroupRequestService;
import com.zalo.modules.app.user.dto.response.UserPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/join-group-request")
@RequiredArgsConstructor
public class JoinGroupRequestController {
    private final JoinGroupRequestService joinGroupRequestService;

    @PostMapping("")
    public void joinGroupRequest(@CurrentUser UserPayload user, @RequestBody JoinGroupDto dto) {
        joinGroupRequestService.requestToJoinGroup(user.getId(), dto);
    }

    @GetMapping("/{conversationId}")
    @RequireMemberRole(memberRoles = {MemberRole.GOLDEN_KEY, MemberRole.SILVER_KEY})
    public List<JoinGroupRequestResponse> getGroupRequest(@CurrentUser UserPayload user, @PathVariable Long conversationId) {
        return joinGroupRequestService.getListByConvId(conversationId).stream().map(e -> new JoinGroupRequestResponse(e, "createdBy")).toList();
    }

    @PostMapping("/{conversationId}/approve")
    @RequireMemberRole(memberRoles = {MemberRole.GOLDEN_KEY, MemberRole.SILVER_KEY})
    public void approveRequest(@CurrentUser UserPayload user, @RequestBody List<Long> ids, @PathVariable Long conversationId) {
        joinGroupRequestService.approveRequests(ids, conversationId, user.getId());
    }

    @PostMapping("/{conversationId}/remove")
    @RequireMemberRole(memberRoles = {MemberRole.GOLDEN_KEY, MemberRole.SILVER_KEY})
    public void removeRequest(@CurrentUser UserPayload user, @RequestBody List<Long> ids, @PathVariable Long conversationId) {
        joinGroupRequestService.removeRequests(ids);
    }
}
