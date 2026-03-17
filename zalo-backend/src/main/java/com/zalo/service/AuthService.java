package com.zalo.service;

import com.zalo.dto.request.Auth.LoginRequest;
import com.zalo.dto.request.Auth.RegisterRequest;
import com.zalo.model.User;
import com.zalo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterRequest request) {

        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new RuntimeException("Phone already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());

        userRepository.save(user);

        return jwtService.generateToken(user);
    }

    public String login(LoginRequest request) {

        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalidPassword");
        }

        return jwtService.generateToken(user);
    }

}
