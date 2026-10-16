package com.zalo.modules.payment.service;

import com.zalo.common.service.QrCodeService;
import com.zalo.modules.payment.dto.request.CreatePaymentRequest;
import com.zalo.modules.payment.entity.Payment;
import com.zalo.modules.payment.repo.PaymentRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.joda.money.Money;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    PaymentRepo paymentRepo;
    QrCodeService qrCodeService;

    public Payment createPayment(CreatePaymentRequest request, Money money, Long userId) {
        Payment payment = new Payment();
        BeanUtils.copyProperties(request, payment);
        payment.setAmount(money.getAmountMinorLong());
        payment.setCurrency(money.getCurrencyUnit().getCode());
        payment.setCu(userId);

        return paymentRepo.save(payment);
    }

    public String createQrCodeLink() {
        return qrCodeService.createQrCodeLink(123L, "123");
    }
}
