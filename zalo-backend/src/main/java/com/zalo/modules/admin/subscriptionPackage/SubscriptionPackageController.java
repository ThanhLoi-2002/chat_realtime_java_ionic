package com.zalo.modules.admin.subscriptionPackage;

import com.zalo.common.configuration.anotation.ResponseMessage;
import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.common.filter.SubscriptionPackageFilter;
import com.zalo.modules.admin.subscriptionPackage.entity.BillingCycle;
import com.zalo.modules.admin.system.user.dto.response.UserPayload;
import com.zalo.modules.admin.subscriptionPackage.dto.request.SubscriptionPackageRequest;
import com.zalo.modules.admin.subscriptionPackage.dto.response.SubscriptionPackageResponse;
import com.zalo.modules.admin.subscriptionPackage.entity.SubscriptionPackage;
import com.zalo.modules.admin.subscriptionPackage.service.SubscriptionPackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/subscription-package")
@RequiredArgsConstructor
public class SubscriptionPackageController {
    private final SubscriptionPackageService subscriptionPackageService;

    @GetMapping
    public Page<SubscriptionPackageResponse> findAll(
            @ModelAttribute SubscriptionPackageFilter filter
    ) {
        Page<SubscriptionPackage> data = subscriptionPackageService.findAll(filter);
        return data.map(SubscriptionPackageResponse::new);
    }

    @GetMapping("/{id}")
    public SubscriptionPackageResponse getById(
            @PathVariable Long id
    ) {
        return new SubscriptionPackageResponse(subscriptionPackageService.getById(id));
    }

    @PostMapping
    @ResponseMessage("success")
    public SubscriptionPackageResponse create(@RequestBody @Valid SubscriptionPackageRequest req, @CurrentUser UserPayload user) {
        return new SubscriptionPackageResponse(subscriptionPackageService.create(req, user.getId()));
    }

    @PutMapping("/{id}")
    @ResponseMessage("success")
    public SubscriptionPackageResponse update(
            @PathVariable Long id,
            @RequestBody @Valid SubscriptionPackageRequest req, @CurrentUser UserPayload user
    ) {
        return new SubscriptionPackageResponse(subscriptionPackageService.update(id, req, user.getId()));
    }

    @DeleteMapping("/{id}")
    @ResponseMessage("success")
    public void delete(@PathVariable Long id) {
        subscriptionPackageService.delete(id);
    }

    @GetMapping("/available-packages")
    public Map<BillingCycle, List<SubscriptionPackageResponse>> getAvailablePackages(
            @ModelAttribute SubscriptionPackageFilter filter
    ) {
        return subscriptionPackageService.getAvailablePackages(filter);
    }
}
