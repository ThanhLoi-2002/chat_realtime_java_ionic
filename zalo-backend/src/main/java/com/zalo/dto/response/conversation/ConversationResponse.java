package com.zalo.dto.response.conversation;

import com.zalo.dto.response.BaseResponse;
import com.zalo.model.File;
import com.zalo.model.enums.ConversationType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationResponse extends BaseResponse {
    ConversationType type; // PRIVATE or GROUP

    String name;
    File avatar;
}
