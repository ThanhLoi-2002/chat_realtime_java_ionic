package com.zalo.modules.media.service;

import com.zalo.modules.media.entities.Media;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<Media, Long> {
}
