package com.zalo.modules.app.message.entity;

import com.zalo.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "message_pin")
public class MessagePin extends BaseEntity {
    Long conversationId;
    Long messageId;
    int isActive = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "messageId", insertable = false, updatable = false)
    Message message;

    public MessagePin(Long messageId, Long convId, Long userId) {
        this.messageId = messageId;
        this.conversationId = convId;
        this.setCu(userId);
    }
}
