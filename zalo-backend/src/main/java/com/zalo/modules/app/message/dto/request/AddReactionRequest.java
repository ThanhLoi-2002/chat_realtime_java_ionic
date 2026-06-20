package com.zalo.modules.app.message.dto.request;

import com.zalo.modules.app.message.entity.ReactionType;
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
