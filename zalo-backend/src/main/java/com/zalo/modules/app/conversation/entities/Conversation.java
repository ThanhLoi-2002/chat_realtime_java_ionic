package com.zalo.modules.app.conversation.entities;

import com.zalo.common.base.BaseEntity;
import com.zalo.modules.app.groupSetting.entities.GroupSetting;
import com.zalo.modules.app.media.entities.Media;
import com.zalo.modules.app.message.entity.Message;
import com.zalo.modules.admin.system.user.entities.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
    User owner;

    // Trong Conversation.java
    Long avatarId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatarId", insertable = false, updatable = false)
    Media avatar;

    Long lastMessageId;

    // ✅ relation tới lastMessage (read-only)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastMessageId", insertable = false, updatable = false)
    Message lastMessage;

    Long recipientId;

    // ✅ relation tới recipient (read-only)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipientId", insertable = false, updatable = false)
    User recipient;

    @JdbcTypeCode(SqlTypes.JSON)
    @ColumnTransformer(write = "?")
    GroupSetting settings = new GroupSetting();
}
