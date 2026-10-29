package com.zalo.modules.admin.subscriptionPackage.dto.response;

import com.zalo.common.base.BaseResponse;
import com.zalo.modules.admin.subscriptionPackage.dto.request.FeatureDTO;
import com.zalo.modules.admin.subscriptionPackage.dto.request.GiftDTO;
import com.zalo.modules.admin.subscriptionPackage.entity.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class SubscriptionPackageResponse extends BaseResponse {
    String name;
    Long price;
    String description;
    SubscriptionCategory category;
    TargetType targetType;
    BillingCycle billingCycle;
    String featuresConfig;
    Integer compareId;
    List<FeatureDTO> features;
    List<GiftDTO> gifts;
    SubscriptionLevel level;
    Long monthlyPrice;

    public SubscriptionPackageResponse(SubscriptionPackage e, String... relations) {
        super(e, relations);
        BeanUtils.copyProperties(e, this, "createdBy", "updatedBy");
    }
}
