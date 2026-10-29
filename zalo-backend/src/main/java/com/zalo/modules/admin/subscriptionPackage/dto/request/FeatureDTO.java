package com.zalo.modules.admin.subscriptionPackage.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeatureDTO {
    private String text;    // Nội dung: "Nhãn nhận diện zBusiness"
    private String tag;     // Thẻ tag (nếu có): "Business" hoặc "Mới"
    private String subtext; // Mô tả phụ (nếu có): "Đặc quyền khi định danh..."
}
