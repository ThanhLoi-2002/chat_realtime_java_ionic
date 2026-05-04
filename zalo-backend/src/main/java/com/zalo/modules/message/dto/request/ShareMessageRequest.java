package com.zalo.modules.message.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
public class ShareMessageRequest {
    List<Long> conversationIds;
    Long messageId;
    boolean isAttachDesc;
    String content;
    Long conversationId;
}
