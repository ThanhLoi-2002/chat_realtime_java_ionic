package com.zalo.modules.app.sticker.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StickerItem {
    String id;
    String url;
    int frameCount;
    String stickerId;
}
