package com.zalo.modules.admin.subscriptionPackage.repo;

import com.zalo.modules.admin.subscriptionPackage.entity.SubscriptionCategory;
import com.zalo.modules.admin.subscriptionPackage.entity.SubscriptionPackage;
import com.zalo.modules.admin.subscriptionPackage.entity.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPackageRepo extends JpaRepository<SubscriptionPackage, Long>, JpaSpecificationExecutor<SubscriptionPackage> {
    Optional<SubscriptionPackage> findByName(String name);

    // Lấy danh sách gói cước hiển thị cho user chọn
    List<SubscriptionPackage> findAllByCategoryAndTargetTypeAndStt(
            SubscriptionCategory category, TargetType targetType, int status);
}
