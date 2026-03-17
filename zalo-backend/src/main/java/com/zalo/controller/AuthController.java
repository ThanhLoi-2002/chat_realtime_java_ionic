package com.zalo.controller;

import com.zalo.configuration.anotation.ResponseMessage;
import com.zalo.dto.request.Auth.LoginRequest;
import com.zalo.dto.request.Auth.RegisterRequest;
import com.zalo.dto.response.Auth.LoginResponse;
import com.zalo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseMessage("success")
    public LoginResponse register(
            @RequestBody @Valid RegisterRequest request
    ) {
        return new LoginResponse(authService.register(request));
    }

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody @Valid LoginRequest request
    ) {
        return new LoginResponse(authService.login(request));
    }

}
