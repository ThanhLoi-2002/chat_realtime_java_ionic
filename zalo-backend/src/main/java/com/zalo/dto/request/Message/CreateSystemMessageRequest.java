package com.zalo.dto.request.Message;

import com.zalo.model.enums.SystemMessageType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
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
}
