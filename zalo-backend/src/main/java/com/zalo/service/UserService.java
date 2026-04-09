package com.zalo.service;

import com.cloudinary.api.exceptions.NotFound;
import com.zalo.dto.filter.UserFilter;
import com.zalo.model.File;
import com.zalo.model.User;
import com.zalo.modules.media.service.MediaService;
import com.zalo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.hibernate.NonUniqueResultException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MediaService cloudinaryService;

    public User getOneByToken(String token) throws NotFound {
        Claims claims = jwtService.extractAllClaims(token);
        Long id = claims.get("id", Long.class);

        return userRepository.findById(id).orElseThrow(
                () -> new NotFound("notFound")
        );
    }

    public User uploadAvatar(MultipartFile file, User user) throws IOException {
        File avatar = cloudinaryService.uploadFile(file);

        if (Boolean.parseBoolean(String.valueOf(user.getAvatar())) && Boolean.parseBoolean(user.getAvatar().getPublicId())) {
            cloudinaryService.deleteFile(user.getAvatar().getPublicId());
        }
        user.setAvatar(avatar);

        return userRepository.save(user);
    }

    public User uploadCover(MultipartFile file, User user) throws IOException {
        File cover = cloudinaryService.uploadFile(file);

        if (Boolean.parseBoolean(String.valueOf(user.getCover())) && Boolean.parseBoolean(user.getCover().getPublicId())) {
            cloudinaryService.deleteFile(user.getCover().getPublicId());
        }

        user.setCover(cover);

        return userRepository.save(user);
    }

    // Tìm phân trang
    public Page<User> findAll(UserFilter filter) {
        Specification<User> spec = filter.toSpecification();
        Pageable pageable = filter.toPageable();
        return userRepository.findAll(spec, pageable);
    }

    // Tìm một bản ghi (thường dùng khi filter unique như email, phone)
    public Optional<User> findOne(UserFilter filter) {
        Specification<User> spec = filter.toSpecification();
        try {
            return userRepository.findOne(spec);
        } catch (NonUniqueResultException e) {
            return Optional.empty(); // hoặc throw exception tùy nghiệp vụ
        }
    }

    public User findByPhone(String phone) {
        UserFilter filter = new UserFilter();
        filter.setPhone(phone);
        Optional<User> userExisted = findOne(filter);

        if (userExisted.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound");
        }

        return userExisted.get();
    }

    public boolean existedPhone(String phone) {
        UserFilter filter = new UserFilter();
        filter.setPhone(phone);
        Optional<User> userExisted = findOne(filter);
        return userExisted.isPresent();
    }

    // Tìm tất cả không phân trang (nếu cần export, dropdown nhỏ...)
    public List<User> findAllNoPage(UserFilter filter) {
        Specification<User> spec = filter.toSpecification();
        return userRepository.findAll(spec);
    }

    public List<User> findByIdIn(List<Long> ids) {
        return userRepository.findByIdIn(ids);
    }
}
