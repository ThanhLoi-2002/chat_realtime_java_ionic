package com.zalo.modules.subscriptionOrder.repo;

import com.zalo.modules.subscriptionOrder.entity.SubscriptionOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionOrderRepo extends JpaRepository<SubscriptionOrder, Long> {
    Optional<SubscriptionOrder> findByCode(String code);
}
