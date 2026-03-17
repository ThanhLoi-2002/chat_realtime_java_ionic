package com.zalo.repository;

import com.zalo.repository.dto.LangDto;
import org.springframework.data.jpa.repository.JpaRepository;
import com.zalo.model.Lang;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LangRepository extends JpaRepository<Lang, Long> {

    Optional<Lang> findByCode(String code);

    boolean existsByCode(String code);

    @Query("""
            SELECT\s
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
}