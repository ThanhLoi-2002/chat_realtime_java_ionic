package com.zalo.modules.conversation.entities;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class ConversationPinId implements Serializable {
    private Long conversationId;
    private Long userId;
}
