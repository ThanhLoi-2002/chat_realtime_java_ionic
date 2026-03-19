package com.zalo.model;

import com.zalo.model.covert.FileConverter;
import com.zalo.model.enums.MessageType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "message")
public class Message extends BaseEntity{
    Long conversationId;
    Long senderId;
    @Column(columnDefinition = "TEXT")
    String content;

    @Enumerated(EnumType.STRING)
    MessageType contentType = MessageType.TEXT;

    @Convert(converter = FileConverter.class)
    @Column(columnDefinition = "LONGTEXT")
    File file;

    Long replyToMessageId;
}
