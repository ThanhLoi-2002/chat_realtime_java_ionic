package com.zalo.modules.joinGroupRequest;

import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.modules.joinGroupRequest.dto.request.JoinGroupDto;
import com.zalo.modules.joinGroupRequest.service.JoinGroupRequestService;
import com.zalo.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/join-group-request")
@RequiredArgsConstructor
public class JoinGroupRequestController {
    JoinGroupRequestService joinGroupRequestService;

    @PostMapping("")
    public void joinGroupRequest(@CurrentUser User user, @RequestBody JoinGroupDto dto) {
        joinGroupRequestService.requestToJoinGroup(user.getId(), dto);
    }
}
