package com.zalo.modules.payment.dto.response;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
    String code;       // Mã nội dung chuyển khoản (VD: DH839201)
    Long price;
    String qrCodeUrl;

//    public PaymentResponse(Payment e) {
//        BeanUtils.copyProperties(e, this);
//    }
}
