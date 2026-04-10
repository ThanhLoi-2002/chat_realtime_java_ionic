package com.zalo.modules.conversation.entities;

import com.zalo.common.base.BaseEntity;
import com.zalo.common.entity.File;
import com.zalo.model.Message;
import com.zalo.modules.user.entities.User;
import com.zalo.common.covert.FileConverter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "conversation")
public class Conversation extends BaseEntity {
    @Enumerated(EnumType.STRING)
    ConversationType type;

    String name;

    String inviteCode;

    @Column(columnDefinition = "TEXT")
    String description;

    Long ownerId;

    // ✅ relation tới owner (read-only)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId", insertable = false, updatable = false)
    private User owner;

    @Convert(converter = FileConverter.class)
    @Column(columnDefinition = "LONGTEXT")
    File avatar;

    Long lastMessageId;

    // ✅ relation tới lastMessage (read-only)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastMessageId", insertable = false, updatable = false)
    private Message lastMessage;

    Long recipientId;

    // ✅ relation tới recipient (read-only)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipientId", insertable = false, updatable = false)
    private User recipient;
}
