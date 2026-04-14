package com.zalo.modules.lang.service;

import com.zalo.modules.lang.dto.LangDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.zalo.modules.lang.entity.Lang;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LangRepository extends JpaRepository<Lang, Long> {

    boolean existsByCode(String code);

    @Query("""
              SELECT
                  l.code as code,
                  CASE\s
                      WHEN :lang = 'vi' THEN l.vi
                      WHEN :lang = 'en' THEN l.en
                      WHEN :lang = 'cn' THEN l.cn
                      WHEN :lang = 'tw' THEN l.tw
                  END as value
              FROM Lang l
            \s""")
    List<LangDto> findByLang(@Param("lang") String lang);

    @Query("""
                SELECT l
                FROM Lang l
                WHERE (:code IS NULL OR LOWER(l.code) LIKE LOWER(CONCAT('%', :code, '%')))
                AND l.stt = 1
                ORDER BY l.ct DESC
            """)
    Page<Lang> findAllLang(
            @Param("code") String code,
            Pageable pageable
    );
}