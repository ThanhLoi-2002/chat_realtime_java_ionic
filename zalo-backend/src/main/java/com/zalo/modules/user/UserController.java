package com.zalo.modules.user;

import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.common.configuration.anotation.ResponseMessage;
import com.zalo.common.filter.UserFilter;
import com.zalo.modules.user.dto.response.UserPayload;
import com.zalo.modules.user.dto.response.UserResponse;
import com.zalo.modules.user.entities.User;
import com.zalo.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getMe(
            @CurrentUser UserPayload user
    ) {
        User u = userService.findById(user.getId());

        UserResponse userResponse = new UserResponse(u);
        userResponse.roles = user.roles;
        userResponse.permissions = user.permissions;

        return new UserResponse(u);
    }

    @PostMapping("/upload-avatar")
    @ResponseMessage("success")
    public UserResponse uploadAvatar(
            @RequestParam MultipartFile file,
            @CurrentUser UserPayload user
    ) throws Exception {
        User u = userService.uploadAvatar(file, user.getId());

        return new UserResponse(u);
    }

    @PostMapping("/upload-cover")
    @ResponseMessage("success")
    public UserResponse uploadCover(
            @RequestParam MultipartFile file,
            @CurrentUser UserPayload user
    ) throws Exception {
        User u = userService.uploadCover(file, user.getId());

        return new UserResponse(u);
    }

    @PutMapping("/toggle-oa")
    @ResponseMessage("success")
    public UserResponse toggleOA(
            @CurrentUser UserPayload user
    ) {
        User u = userService.toggleOA(user.getId());

        return new UserResponse(u);
    }

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

