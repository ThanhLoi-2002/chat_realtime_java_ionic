package com.zalo.modules.sticker;

import com.zalo.common.configuration.anotation.conversationMember.CheckConversationMember;
import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.modules.sticker.dto.response.StickerResponse;
import com.zalo.modules.sticker.entity.StickerItem;
import com.zalo.modules.sticker.service.StickerService;
import com.zalo.modules.user.entities.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{conversationId}")
    @CheckConversationMember
    public void sendSticker(@CurrentUser User user, @PathVariable Long conversationId, @RequestBody StickerItem sticker) {
        stickerService.sendSticker(conversationId, user.getId(), sticker);
    }
}
