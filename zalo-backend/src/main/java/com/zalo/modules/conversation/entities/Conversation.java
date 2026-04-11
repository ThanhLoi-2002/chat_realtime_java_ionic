package com.zalo.modules.conversation.entities;

import com.zalo.common.base.BaseEntity;
import com.zalo.common.entity.File;
import com.zalo.modules.media.entities.Media;
import com.zalo.modules.message.entity.Message;
import com.zalo.modules.user.entities.User;
import com.zalo.common.covert.FileConverter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

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

    // Trong Conversation.java
    Long avatarId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatarId", insertable = false, updatable = false)
    private Media avatar;

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
