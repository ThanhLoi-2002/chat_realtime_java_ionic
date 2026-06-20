package com.zalo.modules.app.message.dto.response;

import com.zalo.common.base.BaseResponse;
import com.zalo.modules.app.sticker.entity.StickerItem;
import com.zalo.modules.app.user.dto.response.UserResponse;
import com.zalo.modules.app.message.entity.Message;
import com.zalo.modules.app.message.entity.MessageType;
import com.zalo.modules.app.media.dtos.responses.MediaResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageResponse extends BaseResponse {
    Long conversationId;
    String content;
    MessageType contentType;
    StickerItem sticker;

    MessageResponse replyToMessage;

    Long senderId;
    UserResponse sender;

    List<MessageReactionResponse> reactions = List.of();

    SystemMetadataResponse systemMetadata;

    List<MediaResponse> attachments;
    LinkPreviewResponse linkMetadata;
    String lang;

    public MessageResponse(Message m, String... relations) {
        super(m, relations);
        BeanUtils.copyProperties(m, this, "replyToMessage", "sender", "createdBy", "updatedBy");

        Set<String> rels = relations != null ? new HashSet<>(Arrays.asList(relations)) : Collections.emptySet();

        if (rels.contains("replyToMessage")) {
            if (m.getReplyToMessage() != null) {
                this.replyToMessage = new MessageResponse(m.getReplyToMessage(), "sender");
            }
        }

        if (rels.contains("sender")) {
            if (m.getSender() != null) {
                this.sender = new UserResponse(m.getSender());
            }
        }

        if(m.getSystemMetadata() != null){
            this.systemMetadata =  new SystemMetadataResponse(m.getSystemMetadata());
        }

//        this.attachments = m.getAttachments().stream().map(MediaResponse::new).toList();
    }
}
