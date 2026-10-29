package com.zalo.modules.subscription.repo;

import com.zalo.modules.admin.subscriptionPackage.entity.SubscriptionCategory;
import com.zalo.modules.admin.subscriptionPackage.entity.TargetType;
import com.zalo.modules.subscription.entity.Subscription;
import com.zalo.modules.subscription.entity.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepo extends JpaRepository<Subscription, Long> {
    // Tìm gói đang active của một đối tượng theo loại dịch vụ
    Optional<Subscription> findByTargetTypeAndTargetIdAndCategoryAndStatus(
            TargetType targetType, Long targetId, SubscriptionCategory category, SubscriptionStatus status);

    // Lấy tất cả gói của 1 user hoặc group
    List<Subscription> findAllByTargetTypeAndTargetId(TargetType targetType, Long targetId);
}
