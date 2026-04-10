package com.zalo.model;

import com.zalo.common.base.BaseEntity;
import com.zalo.model.enums.ReactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "message_reaction")
public class MessageReaction extends BaseEntity {
    Long messageId;

    @Enumerated(EnumType.STRING)
    ReactionType type;

    Integer count;
}
