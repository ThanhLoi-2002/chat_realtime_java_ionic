package com.zalo.modules.webhook.sepay;

import com.zalo.common.configuration.anotation.Public.Public;
import com.zalo.modules.webhook.sepay.dto.request.SePayWebhookRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class SepayWebhookController {

    @PostMapping("/sepay")
    @Public
    public SePayWebhookRequest handleSepayWebhook(@RequestBody SePayWebhookRequest payload) {
        // In ra console toàn bộ thông tin nhận được để test
        log.info("================== NHẬN WEBHOOK TỪ SEPAY ==================");
        log.info("Ngân hàng: {}", payload.getGateway());
        log.info("Số tài khoản: {}", payload.getAccountNumber());
        log.info("Số tiền chuyển: {}", payload.getTransferAmount());
        log.info("Nội dung chuyển khoản: {}", payload.getContent());
        log.info("Mã tham chiếu (Reference Code): {}", payload.getReferenceCode());
        log.info("Nội dung chi tiết (Description): {}", payload.getDescription());
        log.info("==========================================================");

        // Logic test: Bạn có thể check nội dung chuyển khoản chứa mã đơn hàng ở đây nếu muốn
        if (payload.getContent() != null) {
            if (payload.getContent().contains("DH123")) {
                log.info("--> THANH TOÁN THÀNH CÔNG CHO ĐƠN HÀNG: DH123");
            }
        }

        // Phản hồi lại cho Sepay biết là đã nhận thành công
        return payload;
    }
}
