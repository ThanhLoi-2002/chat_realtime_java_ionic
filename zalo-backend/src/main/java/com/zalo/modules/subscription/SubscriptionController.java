package com.zalo.modules.subscription;

import com.zalo.common.filter.SubscriptionPackageFilter;
import com.zalo.modules.admin.subscriptionPackage.dto.response.SubscriptionPackageResponse;
import com.zalo.modules.admin.subscriptionPackage.entity.SubscriptionPackage;
import com.zalo.modules.subscription.entity.Subscription;
import com.zalo.modules.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

//    @GetMapping
//    public Page<SubscriptionPackageResponse> findAll(
//            @ModelAttribute SubscriptionPackageFilter filter
//    ) {
//    }
}
