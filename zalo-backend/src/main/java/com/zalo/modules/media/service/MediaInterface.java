package com.zalo.modules.media.service;

import com.zalo.modules.media.dtos.requests.MediaRequest;
import com.zalo.modules.media.entities.Media;
import com.zalo.modules.media.entities.MediaType;

import java.util.List;

public interface MediaInterface {
    Media save(MediaRequest dto, Long userId);
    void saveAll(List<MediaRequest> dtos, Long userId);

    List<Media> findByModuleIdInAndModuleType(List<Long> moduleIds, MediaType type);
}
