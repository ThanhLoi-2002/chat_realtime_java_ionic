package com.zalo.modules.oa.structure.entity;

import com.zalo.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "structure")
public class Structure extends BaseEntity {
    // ID của menu cha (Nếu là menu gốc cao nhất thì pid = 0 hoặc null)
    @Column(name = "pid", nullable = false)
    private Integer pid = 0;

    // Đường dẫn router hoặc mã định danh (ví dụ: '/dashboard', 'sys001structure')
    @Column(name = "code", length = 100, nullable = false)
    private String code;

    // Chuỗi lưu class icon (ví dụ: 'fas fa-home' hoặc nguyên thẻ '<i class="fas fa-home"></i>')
    @Column(name = "icon", length = 255)
    private String icon;

    // Tên hiển thị của menu trên giao diện (ví dụ: 'Tổng quan', 'Quản lý người dùng')
    @Column(name = "description", length = 255)
    private String description;

    // Loại menu (0: Root, 1: Group, 2: Module, 99: Page/Action)
    @Column(name = "type", nullable = false)
    private Integer type = 99;

    // Thứ tự sắp xếp hiển thị giữa các menu đồng cấp
    @Column(name = "sort", nullable = false)
    private Integer sort = 0;
}
