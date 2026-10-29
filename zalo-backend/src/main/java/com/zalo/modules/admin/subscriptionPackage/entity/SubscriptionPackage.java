package com.zalo.modules.admin.subscriptionPackage.entity;

import com.zalo.common.base.BaseEntity;
import com.zalo.common.covert.JsonFeatureConverter;
import com.zalo.common.covert.JsonGiftConverter;
import com.zalo.modules.admin.subscriptionPackage.dto.request.FeatureDTO;
import com.zalo.modules.admin.subscriptionPackage.dto.request.GiftDTO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class SubscriptionPackage extends BaseEntity {
    String name;          // 'Gói AI VIP (Tháng)', 'Nâng cấp Cộng đồng'
    Long price;     // Giá tiền (VND)
    String description;

    @Enumerated(EnumType.STRING)
    SubscriptionCategory category; //'AI', 'BUSINESS', 'COMMUNITY'

    @Enumerated(EnumType.STRING)
    TargetType targetType; //'USER' hoặc 'GROUP'

    @Enumerated(EnumType.STRING)
    BillingCycle billingCycle; //'MONTHLY', 'QUARTERLY', 'YEARLY'

    Long monthlyPrice;      // Giá quy đổi /tháng (VD: 200,000đ)

    // Các thông số hiển thị tiết kiệm
    Integer savingsPercentage;   // Phần trăm tiết kiệm (VD: 20%)
    Long savingsAmount;     // Số tiền tiết kiệm (VD: 600,000đ)

    Integer compareId;

    @Convert(converter = JsonFeatureConverter.class)
    @Column(columnDefinition = "TEXT")
    List<FeatureDTO> features;

    @Convert(converter = JsonGiftConverter.class)
    @Column(columnDefinition = "TEXT")
    List<GiftDTO> gifts; // Danh sách quà tặng kèm theo gói

    @Enumerated(EnumType.STRING)
    SubscriptionLevel level;
}