package com.zalo.dto.response.conversation;

import com.zalo.model.File;
import com.zalo.model.enums.ConversationType;
import com.zalo.model.enums.MessageType;

import java.time.LocalDateTime;

public interface ConversationViewResponse {
    Long getId();
    String getName();
    ConversationType getType();
    File getAvatar();
    LocalDateTime getCt();

    LocalDateTime getEt();

    Long getLastMessageId();
    String getLastMessageContent();
    MessageType getLastMessageType();

    String getSenderUsername();
    File getSenderAvatar();
}
