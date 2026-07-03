package com.zalo.modules.app.sticker.service;

import com.zalo.modules.app.sticker.entity.UserSticker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserStickerRepository extends JpaRepository<UserSticker, Long> {
    List<UserSticker> findByCuAndStt(Long cu, int stt);
}
