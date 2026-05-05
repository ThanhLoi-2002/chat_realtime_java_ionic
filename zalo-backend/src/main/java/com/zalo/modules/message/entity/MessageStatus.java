package com.zalo.modules.message.entity;

import com.zalo.common.base.BaseEntity;
import com.zalo.modules.user.entities.User;
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

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "messageId", insertable = false, updatable = false)
//    private Message message;
}
