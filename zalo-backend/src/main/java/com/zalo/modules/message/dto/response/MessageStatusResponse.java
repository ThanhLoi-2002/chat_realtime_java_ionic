package com.zalo.modules.message.dto.response;

import com.zalo.common.base.BaseResponse;
import com.zalo.modules.message.entity.DeliveryStatus;
import com.zalo.modules.message.entity.Message;
import com.zalo.modules.message.entity.MessageStatus;
import com.zalo.modules.user.dto.response.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageStatusResponse extends BaseResponse {
    Long messageId;
    UserResponse user;
    DeliveryStatus status;

    public MessageStatusResponse(MessageStatus m, String... relations) {
        super(m, relations);
        BeanUtils.copyProperties(m, this, "user", "createdBy", "updatedBy");

        Set<String> rels = relations != null ? new HashSet<>(Arrays.asList(relations)) : Collections.emptySet();
        if(rels.contains("user")){
            this.user = new UserResponse(m.getUser());
        }
    }
}
