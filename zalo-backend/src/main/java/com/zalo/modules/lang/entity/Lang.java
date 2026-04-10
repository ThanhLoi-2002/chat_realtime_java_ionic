package com.zalo.modules.lang.entity;

import com.zalo.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "lang")
public class Lang extends BaseEntity {
    @Column(nullable = false, unique = true)
    String code;

    String vi;
    String en;
    String cn;
    String tw;
}
