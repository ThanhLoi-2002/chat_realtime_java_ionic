package com.zalo.dto.response.Message;

import com.zalo.dto.response.BaseResponse;
import com.zalo.model.MessageReaction;
import com.zalo.model.enums.ReactionType;
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
