package com.zalo.modules.transaction.entity;

import com.zalo.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Transaction extends BaseEntity {
    Long packageId;

    BigDecimal amount;

    @Column(unique = true)
    String orderCode; // Mã đơn hàng độc nhất dùng cho nội dung chuyển khoản (VD: ZALOUPG171...)

    @Enumerated(EnumType.STRING)
    TransactionStatus status = TransactionStatus.PENDING;
    LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    TransactionType type;

    @JdbcTypeCode(SqlTypes.JSON)
    @ColumnTransformer(write = "?")
    TransactionMetadata metadata;
}
