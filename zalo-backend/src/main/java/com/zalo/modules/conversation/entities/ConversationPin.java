package com.zalo.modules.conversation.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "conversation_pin")
@IdClass(ConversationPinId.class)
public class ConversationPin{
    @Id
    Long userId;

    @Id
    Long conversationId;

    LocalDateTime ct = LocalDateTime.now();
}
