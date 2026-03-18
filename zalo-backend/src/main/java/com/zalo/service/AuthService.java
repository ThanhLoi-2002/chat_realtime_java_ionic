package com.zalo.service;

import com.zalo.dto.filter.UserFilter;
import com.zalo.dto.request.Auth.LoginRequest;
import com.zalo.dto.request.Auth.RegisterRequest;
import com.zalo.model.User;
import com.zalo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterRequest request) {
        Optional<User> userExisted = userService.findByPhone(request.getPhone());

        if(userExisted.isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "phoneExisted");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());

        userRepository.save(user);

        return jwtService.generateToken(user);
    }

    public String login(LoginRequest request) {
        Optional<User> user = userService.findByPhone(request.getPhone());

        if(user.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "phoneNotFound");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.get().getPassword()
        )) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalidPassword");
        }

        return jwtService.generateToken(user.get());
    }

}
