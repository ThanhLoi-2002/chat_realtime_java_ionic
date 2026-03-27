package com.zalo.controller;

import com.cloudinary.api.exceptions.NotFound;
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

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public LoginResponse register(
            @RequestBody @Valid RegisterRequest request
    ) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody @Valid LoginRequest request
    ) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public Map refresh(@RequestBody Map<String, String> request) throws NotFound {
        String refreshToken = request.get("refreshToken");

        return Map.of(
                "token", authService.newToken(refreshToken)
        );
    }
}
