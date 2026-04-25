package com.zalo.modules.classificationCard.entity;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class ClassificationConversationId implements Serializable {
    private Long classificationId;
    private Long conversationId;
}
