package com.zalo.dto.request.Message;

import com.zalo.model.enums.ReactionType;
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
