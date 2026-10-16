package com.zalo.modules.subscriptionPackage.repo;

import com.zalo.modules.subscriptionPackage.entity.SubscriptionPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionPackageRepo extends JpaRepository<SubscriptionPackage, Long> {
    Optional<SubscriptionPackage> findByCode(String code);
}
