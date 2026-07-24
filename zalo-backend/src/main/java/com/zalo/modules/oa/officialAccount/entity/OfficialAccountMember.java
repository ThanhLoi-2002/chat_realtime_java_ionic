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
@Table(name = "oa_member")
public class OfficialAccountMember extends BaseEntity {

    @Column(nullable = false)
    Long oaId;

    @Column(nullable = false)
    Long userId;

    @Column(nullable = false, length = 20)
    OaRole role;

    @Enumerated(EnumType.STRING)
    OaMember status;

    LocalDateTime joinedAt;
}
