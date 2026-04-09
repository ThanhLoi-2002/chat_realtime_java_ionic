package com.zalo.dto.request.Message;

import com.zalo.model.enums.MessageType;
import com.zalo.modules.media.dtos.requests.MediaRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

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
}
