package com.zalo.modules.payment;

import com.zalo.common.configuration.anotation.Public.Public;
import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.common.util.MoneyUtil;
import com.zalo.modules.payment.dto.request.CreatePaymentRequest;
import com.zalo.modules.payment.dto.response.PaymentResponse;
import com.zalo.modules.payment.entity.Payment;
import com.zalo.modules.payment.service.PaymentService;
import com.zalo.modules.admin.system.user.dto.response.UserPayload;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.joda.money.Money;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/app/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping()
    public PaymentResponse create(@RequestBody @Valid CreatePaymentRequest request, @CurrentUser UserPayload user) {
        Money money = MoneyUtil.toMoney(request.getCurrency(), request.getAmount());

        Payment payment = paymentService.createPayment(request, money, user.getId());
        return new PaymentResponse(payment);
    }

    @GetMapping()
    @Public
    public Map<String, Object> qr() {
        Map<String, Object> obj = new HashMap<>();
        obj.put("qrCode", paymentService.createQrCodeLink());
        return obj;
    }
}
