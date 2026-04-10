package com.zalo.modules.message.dto.response;

import com.zalo.common.base.BaseResponse;
import com.zalo.modules.message.entity.MessageReaction;
import com.zalo.modules.message.entity.ReactionType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class MessageReactionResponse extends BaseResponse {
    Long messageId;
    ReactionType type;
    Integer count;

    public MessageReactionResponse(MessageReaction mr, String... relations) {
        super(mr, relations);
        BeanUtils.copyProperties(mr, this, "createdBy", "updatedBy");
    }
}
