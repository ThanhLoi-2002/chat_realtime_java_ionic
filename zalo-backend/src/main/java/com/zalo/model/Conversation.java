package com.zalo.model;

import com.zalo.model.covert.FileConverter;
import com.zalo.model.enums.ConversationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "conversation")
public class Conversation extends BaseEntity{
    @Enumerated(EnumType.STRING)
    ConversationType type; // PRIVATE or GROUP

    String name;

    String code;

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

    // ✅ members
//    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY)
//    private List<ConversationMember> members;

    // ✅ messages (optional)
//    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY)
//    private List<Message> messages;
}
