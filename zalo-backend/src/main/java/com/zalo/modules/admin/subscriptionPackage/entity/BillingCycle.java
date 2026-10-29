package com.zalo.modules.admin.subscriptionPackage.entity;

import lombok.Getter;

@Getter
public enum BillingCycle {
    MONTHLY(1),
    QUARTERLY(3),
    YEARLY(12),
    LIFETIME(0);

    private final int months;

    BillingCycle(int months) {
        this.months = months;
    }

    public boolean isLifetime() {
        return this == LIFETIME;
    }
}
