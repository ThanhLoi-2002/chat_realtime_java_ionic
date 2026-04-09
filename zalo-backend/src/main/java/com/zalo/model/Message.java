package com.zalo.model;

import com.zalo.model.covert.FileConverter;
import com.zalo.model.enums.MessageType;
import com.zalo.model.metadata.SystemMetadata;
import com.zalo.modules.media.entities.Media;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.util.List;

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

    @JdbcTypeCode(SqlTypes.JSON)
//    @Column(columnDefinition = "json")
    @ColumnTransformer(write = "?")// Ép Hibernate ghi thẳng giá trị, không dùng CAST(? as json)
    private SystemMetadata systemMetadata;

    @Enumerated(EnumType.STRING)
    MessageType contentType = MessageType.TEXT;

    @Convert(converter = FileConverter.class)
    @Column(columnDefinition = "LONGTEXT")
    File file;

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
    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY)
    private List<MessageStatus> statuses;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "moduleId", insertable = false, updatable = false)
    @SQLRestriction("module_type = 'MESSAGE'") // Chỉ lấy media thuộc về Message
    private List<Media> attachments;
}
