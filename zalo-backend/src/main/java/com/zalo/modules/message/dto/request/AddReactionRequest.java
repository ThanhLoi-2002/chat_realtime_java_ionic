package com.zalo.modules.message.dto.request;

import com.zalo.modules.message.entity.ReactionType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
public class AddReactionRequest {
    Long messageId;
    ReactionType type;
}
