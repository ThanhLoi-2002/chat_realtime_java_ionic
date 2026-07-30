package com.zalo.common.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class CodeGeneratorService {
    public String generate() {
        // Lấy ngày hiện tại: 260727 (YYMMDD)
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));

        // Lấy 6 ký tự ngẫu nhiên viết hoa từ UUID
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();

        // Kết quả ví dụ: 260727-A3F9B1
        return datePart + "-" + randomPart;
    }
}
