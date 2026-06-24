package com.zalo.modules.app.message.entity;

import com.zalo.common.base.BaseEntity;
import com.zalo.modules.admin.system.user.entities.User;
import jakarta.persistence.*;
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

    // ✅ sender
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status = DeliveryStatus.SENT;
}
