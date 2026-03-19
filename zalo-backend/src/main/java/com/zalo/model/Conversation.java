package com.zalo.model;

import com.zalo.model.covert.FileConverter;
import com.zalo.model.enums.ConversationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "conversation")
public class Conversation extends BaseEntity{
    @Enumerated(EnumType.STRING)
    ConversationType type; // PRIVATE or GROUP

    String name;

    @Convert(converter = FileConverter.class)
    @Column(columnDefinition = "LONGTEXT")
    File avatar;

    Long lastMessageId;
}
