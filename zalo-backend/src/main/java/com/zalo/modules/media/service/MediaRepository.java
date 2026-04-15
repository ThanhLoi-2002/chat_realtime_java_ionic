package com.zalo.modules.media.service;

import com.zalo.modules.media.entities.Media;
import com.zalo.modules.media.entities.MediaType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {
    @EntityGraph(attributePaths = {"createdBy"})
    List<Media> findByModuleIdInAndModuleType(List<Long> moduleIds, MediaType type);
}
