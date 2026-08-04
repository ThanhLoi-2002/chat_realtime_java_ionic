package com.zalo.modules.admin.oa.category.entity;

import com.zalo.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "oa_category")
public class Category extends BaseEntity {
    String code;
    String name;
    String description;
}