package com.zalo.modules.admin.system.user;

import com.zalo.common.filter.UserFilter;
import com.zalo.modules.admin.system.user.dto.response.UserResponse;
import com.zalo.modules.admin.system.user.entities.User;
import com.zalo.modules.admin.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/system/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    public Page<UserResponse> searchUsers(
            @ModelAttribute UserFilter filter
    ) {
        Page<User> users = userService.findAll(filter);
        return users.map(UserResponse::new);
    }

    @GetMapping("/search-phone")
    public UserResponse getOneByPhone(@ModelAttribute UserFilter filter) {
        return new UserResponse(userService.findByPhone(filter.getPhone()));
    }
}

