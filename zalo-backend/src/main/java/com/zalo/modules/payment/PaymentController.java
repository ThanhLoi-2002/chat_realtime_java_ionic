package com.zalo.modules.payment;

import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.modules.payment.dto.response.PaymentResponse;
import com.zalo.modules.payment.service.PaymentService;
import com.zalo.modules.admin.system.user.dto.response.UserPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/app/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/subscription-order")
    public Map<String, String> createSubscriptionOrder(@RequestParam Long packageId, @CurrentUser UserPayload user) {
        return Map.of("code", paymentService.createSubscriptionOrder(packageId, user.getId()));
    }

    @GetMapping("/subscription-order-payment-info")
    public PaymentResponse getSubscriptionOrderPaymentInfo(@RequestParam String code) {
        return paymentService.getSubscriptionOrderPaymentInfo(code);
    }
}
