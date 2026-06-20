package com.zalo.modules.app.message.dto.request;

import com.zalo.modules.app.message.dto.response.LinkPreviewResponse;
import com.zalo.modules.app.message.entity.MessageType;
import com.zalo.modules.app.media.dtos.requests.MediaRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
public class CreateMessageRequest {
    String content;
    MessageType contentType = MessageType.TEXT;
    Long replyToId;
    List<MediaRequest> attachments;
    LinkPreviewResponse linkMetadata;
}
