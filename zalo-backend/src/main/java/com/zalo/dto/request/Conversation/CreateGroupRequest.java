package com.zalo.dto.request.Conversation;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
public class CreateGroupRequest {
    @NotBlank(message = "required")
    String name;

    List<Long> participantIds;
}
