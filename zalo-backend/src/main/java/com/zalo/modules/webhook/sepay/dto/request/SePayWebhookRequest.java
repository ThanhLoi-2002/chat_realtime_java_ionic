package com.zalo.modules.webhook.sepay.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SePayWebhookRequest {
    private Long id;                  // ID giao dịch trên SePay

    @JsonProperty("gateway")
    private String gateway;           // Ngân hàng (VCB, MB,...)

    @JsonProperty("transactionDate")
    private String transactionDate;   // Thời gian giao dịch

    @JsonProperty("accountNumber")
    private String accountNumber;     // Số tài khoản nhận

    @JsonProperty("subAccount")
    private String subAccount;        // Tài khoản phụ (nếu có)

    @JsonProperty("code")
    private String code;              // Mã giao dịch thanh toán

    @JsonProperty("content")
    private String content;           //  Nội dung chuyển khoản (Chứa mã orderCode của bạn, VD: ZBOX171... )

    @JsonProperty("transferType")
    private String transferType;      // in (tiền vào) / out (tiền ra)

    @JsonProperty("transferAmount")
    private BigDecimal transferAmount; // Số tiền khách chuyển thực tế

    @JsonProperty("accumulated")
    private BigDecimal accumulated;   // Số dư lũy kế

    @JsonProperty("referenceCode")
    private String referenceCode;     // Mã tham chiếu ngân hàng

    @JsonProperty("description")
    private String description;       // Mô tả giao dịch đầy đủ
}