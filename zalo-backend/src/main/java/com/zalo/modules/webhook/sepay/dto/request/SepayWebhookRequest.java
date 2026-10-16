package com.zalo.modules.webhook.sepay.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SepayWebhookRequest {
    Long id;
    String gateway;
    String transactionDate;
    String accountNumber;
    String code;
    String content;
    BigDecimal transferAmount;
    String transferType; // "in" hoặc "out"
    String referenceCode;
    String description;
}
