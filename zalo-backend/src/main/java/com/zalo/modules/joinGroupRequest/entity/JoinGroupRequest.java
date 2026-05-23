package com.zalo.modules.joinGroupRequest.entity;

import com.zalo.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "join_group_request")
public class JoinGroupRequest extends BaseEntity {
    Long conversationId;
    String message;
    Long userId;
}
