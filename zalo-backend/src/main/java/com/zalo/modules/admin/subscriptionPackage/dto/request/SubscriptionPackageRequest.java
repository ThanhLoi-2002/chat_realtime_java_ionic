package com.zalo.modules.admin.subscriptionPackage.dto.request;

import com.zalo.modules.admin.subscriptionPackage.entity.BillingCycle;
import com.zalo.modules.admin.subscriptionPackage.entity.SubscriptionCategory;
import com.zalo.modules.admin.subscriptionPackage.entity.SubscriptionLevel;
import com.zalo.modules.admin.subscriptionPackage.entity.TargetType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionPackageRequest {
    String name;
    String description;
    Long price;
    SubscriptionCategory category;
    TargetType targetType;
    BillingCycle billingCycle;
    Long monthlyPrice;      // Giá quy đổi /tháng (VD: 200,000đ)

    // Các thông số hiển thị tiết kiệm
    Integer savingsPercentage;   // Phần trăm tiết kiệm (VD: 20%)
    Long savingsAmount;     // Số tiền tiết kiệm (VD: 600,000đ)
    Integer compareId;
    List<FeatureDTO> features;
    List<GiftDTO> gifts;
    SubscriptionLevel level;
}