package com.zalo.modules.sticker.entity;

import com.zalo.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "sticker")
public class Sticker extends BaseEntity {
    String stickerId;
    String name;
    String thumbImg;
    String iconUrl;
    String source;

    @JdbcTypeCode(SqlTypes.JSON)
    @ColumnTransformer(write = "?")
    List<StickerItem> items;
}
