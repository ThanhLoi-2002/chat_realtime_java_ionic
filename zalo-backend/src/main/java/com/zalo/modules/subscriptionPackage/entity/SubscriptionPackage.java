package com.zalo.modules.subscriptionPackage.entity;

import com.zalo.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Table(name = "subscription_packages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class SubscriptionPackage extends BaseEntity {
    String name;          // Ví dụ: Gói Tháng, Gói Quý, Gói Năm
    @Column(unique = true)
    String code;          // FREE, MONTHLY, QUARTERLY, YEARLY
    Long price;     // Giá tiền (VND)
    Integer durationDays; // Số ngày hiệu lực (30, 90, 365)

    @Column(columnDefinition = "TEXT")
    String description;   // Mô tả tính năng đi kèm
}