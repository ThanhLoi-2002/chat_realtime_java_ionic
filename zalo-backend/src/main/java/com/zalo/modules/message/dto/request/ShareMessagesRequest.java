package com.zalo.modules.message.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
public class ShareMessagesRequest {
    List<Long> conversationIds;
    List<Long> messagesId;
    String content;
    Long conversationId;
}
