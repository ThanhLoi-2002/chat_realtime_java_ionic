package com.zalo.modules.app.classificationCard.service;

import com.zalo.modules.app.classificationCard.entity.ClassificationCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassificationCardRepository extends JpaRepository<ClassificationCard, Long> {
    List<ClassificationCard> findAllByCuAndSttOrderByPositionAsc(Long userId, Integer stt);

    boolean existsByNameAndCuAndStt(String name, Long userId, Integer stt);
}
