package com.zalo.modules.subscriptionOrder.entity;

import com.zalo.common.base.BaseEntity;
import com.zalo.modules.admin.subscriptionPackage.entity.BillingCycle;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class SubscriptionOrder extends BaseEntity {
    String code;
    Long packageId;
    Long price;

    @Enumerated(EnumType.STRING)
    BillingCycle billingCycle;

    @Enumerated(EnumType.STRING)
    SubscriptionOrderStatus status;

    @PrePersist
    protected void onCreate() {
        if (code == null || code.isBlank()) {
            code = "SO" + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        }
    }
}
