package com.zalo.modules.sticker;

import com.zalo.modules.sticker.dto.response.StickerResponse;
import com.zalo.modules.sticker.service.StickerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Sticker Crawler Công cụ", description = "API kích hoạt hệ thống cào dữ liệu sticker từ bên thứ ba")
@RestController
@RequestMapping("/stickers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StickerController {
    StickerService stickerService;

    @GetMapping("")
    public List<StickerResponse> getAll() {
        return stickerService.getAll().stream().map(StickerResponse::new).toList();
    }

    @PostMapping("/sync-zalo-stickers")
    public List<StickerResponse> syncZaloStickers() throws Exception {
        return stickerService.syncZaloStickers().stream().map(StickerResponse::new).toList();
    }
}
