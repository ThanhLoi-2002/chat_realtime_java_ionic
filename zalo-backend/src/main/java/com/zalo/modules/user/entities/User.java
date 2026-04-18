package com.zalo.modules.user.entities;

import com.zalo.common.base.BaseEntity;
import com.zalo.common.entity.File;
import com.zalo.common.covert.FileConverter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user")
public class User extends BaseEntity implements UserDetails {
    String username;

    @Column(nullable = false)
    String password;
    @Column(nullable = false, unique = true)
    String phone;

    @Convert(converter = FileConverter.class)
    @Column(columnDefinition = "LONGTEXT")
    File avatar;

    @Convert(converter = FileConverter.class)
    @Column(columnDefinition = "LONGTEXT")
    File cover;

    @Column(unique = true)
    private String deviceId;    // ID duy nhất của điện thoại (UUID)

    @Column(columnDefinition = "TEXT")
    private String fcmToken;    // Token dài do Firebase cấp

//    List<String> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
//        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
