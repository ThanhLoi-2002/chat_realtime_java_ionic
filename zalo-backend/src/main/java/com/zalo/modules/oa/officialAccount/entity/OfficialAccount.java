package com.zalo.modules.oa.officialAccount.entity;

import com.zalo.common.base.BaseEntity;
import com.zalo.common.covert.FileConverter;
import com.zalo.common.entity.File;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "oa")
public class OfficialAccount extends BaseEntity {

    String code;
    String username;
    String name;
    String phone;
    String website;
    String address;

    @Convert(converter = FileConverter.class)
    @Column(columnDefinition = "LONGTEXT")
    File avatar;

    @Convert(converter = FileConverter.class)
    @Column(columnDefinition = "LONGTEXT")
    File cover;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(nullable = false, length = 100)
    String category; //Lĩnh vực hoạt động (Mua sắm, Dịch vụ,...)

    @Enumerated(EnumType.STRING)
    OaStatus status;

    @Enumerated(EnumType.STRING)
    OaVerified verified;
}
