package com.zalo.modules.payment.service;

import com.zalo.common.service.QrCodeService;
import com.zalo.modules.admin.subscriptionPackage.entity.SubscriptionPackage;
import com.zalo.modules.admin.subscriptionPackage.repo.SubscriptionPackageRepo;
import com.zalo.modules.payment.dto.response.PaymentResponse;
import com.zalo.modules.subscriptionOrder.entity.SubscriptionOrder;
import com.zalo.modules.subscriptionOrder.entity.SubscriptionOrderStatus;
import com.zalo.modules.subscriptionOrder.repo.SubscriptionOrderRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    QrCodeService qrCodeService;
    SubscriptionOrderRepo subscriptionOrderRepo;
    SubscriptionPackageRepo subscriptionPackageRepo;

    public String createSubscriptionOrder(Long packageId, Long userId) {
        SubscriptionPackage pkg = subscriptionPackageRepo.findById(packageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));

        SubscriptionOrder order = new SubscriptionOrder();
        order.setCu(userId);
        order.setPackageId(pkg.getId());
        order.setPrice(pkg.getPrice());
        order.setBillingCycle(pkg.getBillingCycle());
        order.setStatus(SubscriptionOrderStatus.PENDING);
        subscriptionOrderRepo.save(order);

        return order.getCode();
    }

    public PaymentResponse getSubscriptionOrderPaymentInfo(String code) {
        SubscriptionOrder order = subscriptionOrderRepo.findByCode(code).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
        PaymentResponse res = new PaymentResponse();
        res.setCode(order.getCode());
        res.setQrCodeUrl(qrCodeService.createQrCodeLink(order.getPrice(), order.getCode()));
        return res;
    }

//    @Transactional
//    public boolean processSePayWebhook(SePayWebhookRequest req) {
//        String content = req.getContent().toUpperCase(); // Nội dung chuyển khoản khách nhập, VD: "ZBOX171283912"
//        BigDecimal paidAmount = req.getTransferAmount();
//
//        // 1. Tìm đơn hàng đang PENDING trong DB dựa vào nội dung chuyển khoản
//        // (Bạn có thể dùng hàm cắt chuỗi hoặc kiểm tra xem content có chứa orderCode hay không)
//        SubscriptionOrder order = orderRepository.findByStatusAndOrderCode("PENDING", extractOrderCode(content))
//                .orElse(null);
//
//        if (order == null) {
//            return false; // Không tìm thấy đơn hàng khớp với nội dung
//        }
//
//        // 2. Kiểm tra xem số tiền khách chuyển có khớp với giá gói cước hay không
//        if (paidAmount.compareTo(order.getPrice()) < 0) {
//            // Khách chuyển thiếu tiền -> Có thể lưu trạng thái "UNDERPAID" hoặc bỏ qua
//            return false;
//        }
//
//        // 3. Cập nhật trạng thái đơn hàng thành công
//        order.setStatus("COMPLETED");
//        orderRepository.save(order);
//
//        // 4. Kích hoạt/Tạo mới bản ghi Subscription (Quyền lợi thực tế cho user)
//        activateSubscriptionForUser(order);
//
//        return true;
//    }
//
//    private String extractOrderCode(String content) {
//        // Logic bóc tách mã orderCode từ nội dung chuyển khoản của khách
//        // Ví dụ nội dung là "THANH TOAN ZBOX171283912", bạn tìm chuỗi bắt đầu bằng "ZBOX"
//        // Ở đây giả sử content chính là hoặc chứa orderCode của bạn.
//        for (String word : content.split("\\s+")) {
//            if (word.startsWith("ZBOX")) {
//                return word;
//            }
//        }
//        return "";
//    }
//
//    private void activateSubscriptionForUser(SubscriptionOrder order) {
//        // Tính toán ngày hết hạn dựa vào BillingCycle (MONTHLY / YEARLY)
//        LocalDateTime startDate = LocalDateTime.now();
//        LocalDateTime endDate = startDate;
//
//        if ("YEARLY".equals(order.getBillingCycle())) {
//            endDate = startDate.plusYears(1);
//        } else {
//            endDate = startDate.plusMonths(1);
//        }
//
//        // Lưu vào bảng Subscriptions chính thức của User
//        Subscription sub = new Subscription();
//        sub.setTargetType(TargetType.USER); // Hoặc tùy thuộc hệ thống của bạn
//        sub.setTargetId(order.getUserId());
//        sub.setSubscriptionPackageId(order.getPackageId());
//        sub.setBillingCycle(BillingCycle.valueOf(order.getBillingCycle()));
//        sub.setStatus(SubscriptionStatus.ACTIVE);
//        sub.setStartDate(startDate);
//        sub.setEndDate(endDate);
//        sub.setAutoRenew(false);
//
//        subscriptionRepository.save(sub);
//    }
}
