package com.zalo.controller;

import com.cloudinary.api.exceptions.NotFound;
import com.zalo.configuration.anotation.CurrentUser;
import com.zalo.configuration.anotation.ResponseMessage;
import com.zalo.dto.filter.UserFilter;
import com.zalo.dto.response.User.UserResponse;
import com.zalo.mapper.UserMapper;
import com.zalo.model.User;
import com.zalo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public UserResponse getMe(
            @CurrentUser User user
    ) throws NotFound {
        return userMapper.toResponse(user);
    }

    @PostMapping("/upload-avatar")
    @ResponseMessage("success")
    public UserResponse uploadAvatar(
            @RequestParam MultipartFile file,
            @CurrentUser User user
    ) throws Exception {
        user = userService.uploadAvatar(file, user);

        return userMapper.toResponse(user);
    }

    @PostMapping("/upload-cover")
    @ResponseMessage("success")
    public UserResponse uploadCover(
            @RequestParam MultipartFile file,
            @CurrentUser User user
    ) throws Exception {
        user = userService.uploadCover(file, user);

        return userMapper.toResponse(user);
    }

    @GetMapping
    public Page<UserResponse> searchUsers(
            @ModelAttribute UserFilter filter
    ) {
        Page<User> users = userService.findAll(filter);
        return users.map(userMapper::toResponse);
    }

    @GetMapping("/search-phone")
    public UserResponse getOneByPhone(@ModelAttribute UserFilter filter) {
        return userMapper.toResponse(userService.findByPhone(filter.getPhone()));
    }
}

