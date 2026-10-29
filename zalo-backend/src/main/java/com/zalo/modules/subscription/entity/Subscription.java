package com.zalo.modules.subscription.entity;


import com.zalo.common.base.BaseEntity;
import com.zalo.modules.admin.subscriptionPackage.entity.BillingCycle;
import com.zalo.modules.admin.subscriptionPackage.entity.SubscriptionCategory;
import com.zalo.modules.admin.subscriptionPackage.entity.TargetType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Subscription extends BaseEntity {
    @Enumerated(EnumType.STRING)
    TargetType targetType;

    Long targetId;

    @Enumerated(EnumType.STRING)
    SubscriptionCategory category;

    Long subscriptionPackageId; // Lưu ID thuần, không tạo mối quan hệ JPA

    @Enumerated(EnumType.STRING)
    BillingCycle billingCycle;

    @Enumerated(EnumType.STRING)
    SubscriptionStatus status;

    LocalDateTime startDate;
    LocalDateTime endDate;
    Boolean autoRenew;
}
