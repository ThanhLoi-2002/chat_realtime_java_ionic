package com.zalo.model;

import com.zalo.model.enums.MemberRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

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

    Long addById;

    Long lastReadMessageId;
}
