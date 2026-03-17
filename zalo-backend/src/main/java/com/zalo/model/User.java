package com.zalo.model;

import com.zalo.model.covert.FileConverter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
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

//    List<String> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
//        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
