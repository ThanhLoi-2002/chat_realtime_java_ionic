package com.zalo.modules.oa.officialAccount.entity;

import com.zalo.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "oa_invite")
public class OfficialAccountInvite extends BaseEntity {

    @Column(nullable = false)
    Long oaId;

    @Column(nullable = false)
    String phone;

    @Column(nullable = false)
    String role;

    @Column(nullable = false, unique = true)
    String token;

    @Enumerated(EnumType.STRING)
    OaInvite status;

    LocalDateTime expiredAt;
}
