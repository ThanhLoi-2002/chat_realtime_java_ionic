package com.zalo.modules.admin.subscriptionPackage.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiftDTO {
    private String title; // Tên quà tặng: "Tặng zStyle giúp tạo dựng hình ảnh chuyên nghiệp"
    private String note;  // Ghi chú/điều kiện: "khi nâng cấp gói Pro 12 tháng"
    private String icon;
}
