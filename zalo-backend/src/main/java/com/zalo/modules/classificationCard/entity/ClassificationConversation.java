package com.zalo.modules.classificationCard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "classification_conversation")
@IdClass(ClassificationConversationId.class)
@Getter
@Setter
@NoArgsConstructor
public class ClassificationConversation {
    @Id
    @Column(name = "classification_id")
    private Long classificationId;

    @Id
    @Column(name = "conversation_id")
    private Long conversationId;

    @Id
    @Column(name = "user_id")
    private Long userId;

    public ClassificationConversation(Long classificationId, Long conversationId, Long userId) {
        this.classificationId = classificationId;
        this.conversationId = conversationId;
        this.userId = userId;
    }
}
