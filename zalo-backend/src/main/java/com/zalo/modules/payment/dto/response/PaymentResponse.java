package com.zalo.modules.payment.dto.response;

import com.zalo.common.base.BaseResponse;
import com.zalo.common.util.MoneyUtil;
import com.zalo.modules.payment.entity.Payment;
import com.zalo.modules.payment.entity.PaymentMethod;
import com.zalo.modules.payment.entity.PaymentProvider;
import com.zalo.modules.payment.entity.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse extends BaseResponse {
    BigDecimal amount;
    Long orderId;

    PaymentProvider provider;
    PaymentMethod method;

    String transactionId;
    String paymentCode;

    String currency;

    PaymentStatus status;

    String gatewayResponse;

    LocalDateTime paidAt;
    LocalDateTime expiredAt;

    public PaymentResponse(Payment e, String... relations) {
        super(e, relations);

        BeanUtils.copyProperties(e, this, "createdBy", "updatedBy");

        this.amount = MoneyUtil.toMoney(e.getCurrency(), e.getAmount()).getAmount();
    }
}
