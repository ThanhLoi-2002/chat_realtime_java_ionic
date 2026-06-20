package com.zalo.modules.app.conversation.entities;

import com.zalo.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "conversation_member")
public class ConversationMember extends BaseEntity {
    Long conversationId;

    Long userId;

    @Enumerated(EnumType.STRING)
    MemberRole role = MemberRole.MEMBER;

    @Enumerated(EnumType.STRING)
    MemberStatus status;

    Long addById;

    Long lastReadMessageId;
}
