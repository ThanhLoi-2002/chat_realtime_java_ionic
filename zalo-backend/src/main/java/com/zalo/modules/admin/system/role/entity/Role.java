package com.zalo.modules.admin.system.role.entity;

import com.zalo.modules.admin.system.structure.entity.AppType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String description;

    @Enumerated(EnumType.STRING)
    AppType appType;
    Long moduleId;
}
