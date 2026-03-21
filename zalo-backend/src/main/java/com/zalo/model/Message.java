package com.zalo.model;

import com.zalo.model.covert.FileConverter;
import com.zalo.model.enums.MessageType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

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

    @Column(name = "senderId")
    Long senderId;

    @Column(columnDefinition = "TEXT")
    String content;

    @Enumerated(EnumType.STRING)
    MessageType contentType = MessageType.TEXT;

    @Convert(converter = FileConverter.class)
    @Column(columnDefinition = "LONGTEXT")
    File file;

    Long replyToMessageId;

    // ✅ conversation
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "conversationId", insertable = false, updatable = false)
//    private Conversation conversation;

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
}
