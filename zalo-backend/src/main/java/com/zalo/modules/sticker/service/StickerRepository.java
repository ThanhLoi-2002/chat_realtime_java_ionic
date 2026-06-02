package com.zalo.modules.sticker.service;

import com.zalo.modules.sticker.entity.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StickerRepository extends JpaRepository<Sticker, Long> {
    List<Sticker> findAllByStt(Integer stt);
}
