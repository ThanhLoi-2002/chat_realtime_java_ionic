package com.zalo.modules.message.dto.request;

import com.zalo.modules.message.entity.SystemMessageType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
public class CreateSystemMessageRequest {
    Long conversationId;
    String content;
    Long senderId;
    List<Long> userIdsAddedToGroup;
    SystemMessageType systemMessageType;
    Map<String, Object> info;
}
