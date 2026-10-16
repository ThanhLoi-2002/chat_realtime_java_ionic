package com.zalo.modules.payment.dto.request;

import com.zalo.modules.payment.entity.PaymentMethod;
import com.zalo.modules.payment.entity.PaymentProvider;
import com.zalo.modules.payment.entity.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreatePaymentRequest {
    Long orderId;
    PaymentProvider provider;
    PaymentMethod method;
    String transactionId;
    String paymentCode;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "amount must be greater than 0")
    @Digits(integer = 15, fraction = 4, message = "amount has too many digits")
    BigDecimal amount;

    @NotBlank(message = "currency is required")
    String currency;

    PaymentStatus status;
    String gatewayResponse;

    LocalDateTime paidAt;
    LocalDateTime expiredAt;
}
