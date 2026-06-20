package com.zalo.modules.app.message.entity;

import com.zalo.common.base.BaseEntity;
import com.zalo.common.entity.SystemMetadata;
import com.zalo.modules.app.message.dto.response.LinkPreviewResponse;
import com.zalo.modules.app.sticker.entity.StickerItem;
import com.zalo.modules.app.user.entities.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "message")
public class Message extends BaseEntity {
    Long conversationId;

    @Column(name = "senderId")
    Long senderId;

    @Column(columnDefinition = "TEXT")
    String content;

    String lang;

    @JdbcTypeCode(SqlTypes.JSON)
    @ColumnTransformer(write = "?")// Ép Hibernate ghi thẳng giá trị, không dùng CAST(? as json)
    SystemMetadata systemMetadata;

    @JdbcTypeCode(SqlTypes.JSON)
    @ColumnTransformer(write = "?")// Ép Hibernate ghi thẳng giá trị, không dùng CAST(? as json)
    StickerItem sticker;

    @JdbcTypeCode(SqlTypes.JSON)
    @ColumnTransformer(write = "?")// Ép Hibernate ghi thẳng giá trị, không dùng CAST(? as json)
    LinkPreviewResponse linkMetadata;

    @Enumerated(EnumType.STRING)
    MessageType contentType = MessageType.TEXT;

    Long replyToMessageId;

    // ✅ sender
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senderId", insertable = false, updatable = false)
    private User sender;

    // ✅ reply message
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replyToMessageId", insertable = false, updatable = false)
    private Message replyToMessage;

    // ✅ message status list
//    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY)
//    private List<MessageStatus> statuses;

//    @OneToMany(fetch = FetchType.LAZY)
//    @JoinColumn(name = "moduleId", insertable = false, updatable = false)
//    @SQLRestriction("module_type = 'MESSAGE'") // Chỉ lấy media thuộc về Message
//    @BatchSize(size = 20)
//    private List<Media> attachments;

//    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY)
//    @BatchSize(size = 20)
//    private List<Media> attachments;
}
