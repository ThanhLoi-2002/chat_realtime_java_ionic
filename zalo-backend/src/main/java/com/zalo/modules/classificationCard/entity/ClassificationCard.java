package com.zalo.modules.classificationCard.entity;

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
@Table(name = "classification_cards")
public class ClassificationCard extends BaseEntity {
    private String name;

    private String color;

    private Integer position; // Dùng để drag-and-drop sắp xếp thứ tự
}
