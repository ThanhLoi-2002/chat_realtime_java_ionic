package com.zalo.controller;

import com.cloudinary.api.exceptions.NotFound;
import com.zalo.configuration.G;
import com.zalo.configuration.anotation.CurrentUser;
import com.zalo.configuration.anotation.ResponseMessage;
import com.zalo.dto.response.User.UserResponse;
import com.zalo.mapper.UserMapper;
import com.zalo.model.File;
import com.zalo.model.User;
import com.zalo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getMe(
            @CurrentUser User user
    ) throws NotFound {
        return UserMapper.toResponse(user);
    }

    @PostMapping("/upload-avatar")
    @ResponseMessage("success")
    public UserResponse uploadAvatar(
            @RequestParam MultipartFile file,
            @CurrentUser User user
    ) throws Exception {
        user = userService.uploadAvatar(file, user);

        return UserMapper.toResponse(user);
    }

    @PostMapping("/upload-cover")
    @ResponseMessage("success")
    public UserResponse uploadCover(
            @RequestParam MultipartFile file,
            @CurrentUser User user
    ) throws Exception {
        user = userService.uploadCover(file, user);

        return UserMapper.toResponse(user);
    }

//    @GetMapping("/users/search")
//    public User searchByPhone(@RequestParam String phone) {
//        return userService.findByPhone(phone)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//    }
}

