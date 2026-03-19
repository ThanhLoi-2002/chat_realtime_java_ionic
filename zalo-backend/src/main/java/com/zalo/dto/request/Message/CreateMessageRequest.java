package com.zalo.dto.request.Message;

import com.zalo.model.enums.MessageType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
public class CreateMessageRequest {
    String content;
    MessageType type = MessageType.TEXT;
    Long replyToId;
}
