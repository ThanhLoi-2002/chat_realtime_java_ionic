package com.zalo.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QrCodeService {
    @Value("${bank.bank-id}")
    public String bankId;

    @Value("${bank.account-no}")
    public String accountNo;

    @Value("${bank.account-name}")
    public String accountName;

    @Value("${bank.template}")
    public String template;

    public String createQrCodeLink(Long amount, String orderCode) {
        // muốn download thì thêm &download=true
        return String.format(
                "https://qr.sepay.vn/img?bank=%s&acc=%s&holder=%s&template=%s&amount=%.0f&des=%s&showinfo=true&fullacc=true",
                bankId, accountNo, accountName, template, amount.floatValue(), orderCode
        );
    }
}
