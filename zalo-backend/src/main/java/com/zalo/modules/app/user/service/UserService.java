package com.zalo.modules.app.user.service;

import com.cloudinary.api.exceptions.NotFound;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zalo.common.filter.UserFilter;
import com.zalo.common.entity.File;
import com.zalo.modules.app.user.dto.response.UserPayload;
import com.zalo.modules.app.user.entities.User;
import com.zalo.modules.app.media.service.MediaService;
import com.zalo.common.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MediaService cloudinaryService;

    public UserPayload getOneByToken(String token) throws NotFound {
        Claims claims = jwtService.extractAllClaims(token);

        ObjectMapper mapper = new ObjectMapper();

        return mapper.convertValue(
                claims.get("payload"),
                UserPayload.class
        );
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
    }

    public User uploadAvatar(MultipartFile file, Long userId) throws IOException {
        File avatar = cloudinaryService.uploadFile(file);

        User user = findById(userId);
        user.setAvatar(avatar);

        return userRepository.save(user);
    }

    public User uploadCover(MultipartFile file, Long userId) throws IOException {
        File cover = cloudinaryService.uploadFile(file);

        User user = findById(userId);
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

    public User toggleOA(Long userId) {
        UserFilter filter = new UserFilter();
        filter.setId(userId);
        Optional<User> userExisted = findOne(filter);

        if (userExisted.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound");
        }

        if(userExisted.get().getIsOa() == 1)
        {
            return userExisted.get();
        }

        userExisted.get().setIsOa(1);
        userExisted.get().generateOaId();
        userRepository.save(userExisted.get());

        return userExisted.get();
    }
}
