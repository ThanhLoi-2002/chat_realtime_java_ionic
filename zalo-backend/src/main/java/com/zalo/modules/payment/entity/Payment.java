package com.zalo.modules.payment.entity;

import com.zalo.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {
    Long orderId;

    /**
     * Payment provider
     */
    @Enumerated(EnumType.STRING)
    PaymentProvider provider;

    /**
     * Phương thức thanh toán
     */
    @Enumerated(EnumType.STRING)
    PaymentMethod method;

    /**
     * Transaction ID của gateway
     */
    String transactionId;

    /**
     * Payment code nội bộ
     */
    String paymentCode;

    /**
     * Amount
     */
    Long amount;

    String currency;

    @Enumerated(EnumType.STRING)
    PaymentStatus status;

    /**
     * Gateway response
     */
     String gatewayResponse;

    LocalDateTime paidAt;
    LocalDateTime expiredAt;
}
