package com.zalo.modules.app.media.service;

import com.zalo.modules.app.media.dtos.requests.MediaRequest;
import com.zalo.modules.app.media.entities.Media;
import com.zalo.modules.app.media.entities.MediaType;

import java.util.List;

public interface MediaInterface {
    Media save(MediaRequest dto, Long userId);
    void saveAll(List<MediaRequest> dtos, Long userId);


    List<Media> findByModuleIdInAndModuleType(List<Long> moduleIds, MediaType type);
}
