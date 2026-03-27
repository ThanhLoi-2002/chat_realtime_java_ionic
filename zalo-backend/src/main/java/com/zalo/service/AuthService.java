package com.zalo.service;

import com.cloudinary.api.exceptions.NotFound;
import com.zalo.dto.request.Auth.LoginRequest;
import com.zalo.dto.request.Auth.RegisterRequest;
import com.zalo.dto.response.Auth.LoginResponse;
import com.zalo.model.User;
import com.zalo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse register(RegisterRequest request) {
         boolean existed = userService.existedPhone(request.getPhone());

        if(existed){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "existed");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());

        userRepository.save(user);

        String token = jwtService.generateToken(user, jwtService.tokenTime);
        String refreshToken = jwtService.generateToken(user, jwtService.refreshTokenTime);
        return LoginResponse.builder().token(token).refreshToken(refreshToken).build();
    }

    public LoginResponse login(LoginRequest request) {
        User user = userService.findByPhone(request.getPhone());

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalidPassword");
        }

        String token = jwtService.generateToken(user, jwtService.tokenTime);
        String refreshToken = jwtService.generateToken(user, jwtService.refreshTokenTime);
        return LoginResponse.builder().token(token).refreshToken(refreshToken).build();
    }

    public String newToken(String refreshToken) throws NotFound {
        Claims claims = jwtService.extractAllClaims(refreshToken);
        Long userId = claims.get("id", Long.class);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFound("notFound")
        );
        return jwtService.generateToken(user, jwtService.tokenTime);
    }
}
