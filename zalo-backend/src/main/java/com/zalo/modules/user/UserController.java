package com.zalo.modules.user;

import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.common.configuration.anotation.ResponseMessage;
import com.zalo.common.filter.UserFilter;
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
            @CurrentUser User user
    ) {
        return new UserResponse(user);
    }

    @PostMapping("/upload-avatar")
    @ResponseMessage("success")
    public UserResponse uploadAvatar(
            @RequestParam MultipartFile file,
            @CurrentUser User user
    ) throws Exception {
        user = userService.uploadAvatar(file, user);

        return new UserResponse(user);
    }

    @PostMapping("/upload-cover")
    @ResponseMessage("success")
    public UserResponse uploadCover(
            @RequestParam MultipartFile file,
            @CurrentUser User user
    ) throws Exception {
        user = userService.uploadCover(file, user);

        return new UserResponse(user);
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

