package com.zalo.model;

import com.zalo.model.enums.DeliveryStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "message_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageStatus extends BaseEntity {
    private Long messageId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status = DeliveryStatus.SENT;
}
