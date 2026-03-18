package com.zalo.service;

import com.cloudinary.api.exceptions.NotFound;
import com.zalo.model.File;
import com.zalo.model.User;
import com.zalo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CloudinaryService cloudinaryService;

    public User getOneByToken(String token) throws NotFound {
        Claims claims = jwtService.extractAllClaims(token);
        Long id = claims.get("id", Long.class);

        return userRepository.findById(id).orElseThrow(
                () -> new NotFound("notFound")
        );
    }

    public User uploadAvatar(MultipartFile file, User user) throws IOException {
        File avatar = cloudinaryService.uploadFile(file);

        if( Boolean.parseBoolean(String.valueOf(user.getAvatar())) && Boolean.parseBoolean(user.getAvatar().getPublicId())){
            cloudinaryService.deleteFile(user.getAvatar().getPublicId());
        }
        user.setAvatar(avatar);

        return userRepository.save(user);
    }

    public User uploadCover(MultipartFile file, User user) throws IOException {
        File cover = cloudinaryService.uploadFile(file);

        if(Boolean.parseBoolean(String.valueOf(user.getCover())) && Boolean.parseBoolean(user.getCover().getPublicId())){
            cloudinaryService.deleteFile(user.getCover().getPublicId());
        }

        user.setCover(cover);

        return userRepository.save(user);
    }
}
