package com.zalo.modules.oa.officialAccount.entity;

import com.zalo.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
    String name;
    String phone;
    String website;
    String address;
    String avatar;

    String cover;

    int province;
    int district;
    String startHour;
    String endHour;

    @Column(name = "is_whole_day")
    Boolean isWholeDay;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(nullable = false, length = 100)
    String category; //Lĩnh vực hoạt động (Mua sắm, Dịch vụ,...)
    String categoryName;

    @JdbcTypeCode(SqlTypes.JSON)
//    @ColumnTransformer(write = "?")
    OaDisplay display;

    @Enumerated(EnumType.STRING)
    OaStatus status;

    @Enumerated(EnumType.STRING)
    OaVerified verified;
}