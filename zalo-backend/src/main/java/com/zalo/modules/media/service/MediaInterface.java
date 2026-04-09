package com.zalo.modules.media.service;

import com.zalo.modules.media.dtos.requests.MediaRequest;

import java.util.List;

public interface MediaInterface {
    void save(MediaRequest dto, Long userId);
    void saveAll(List<MediaRequest> dtos, Long userId);
}
