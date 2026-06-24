package com.zalo.modules.admin.system.structure.service;

import com.zalo.modules.admin.system.structure.entity.AppType;
import com.zalo.modules.admin.system.structure.entity.Structure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StructureRepository extends JpaRepository<Structure, Long> {
    List<Structure> findBySttOrderBySortAsc(int stt);
    List<Structure> findByStt(int stt);

    List<Structure> findBySttAndAppTypeOrderBySortAsc(int stt, AppType appType);
    List<Structure> findBySttAndAppTypeAndTypeOrderBySortAsc(int stt, AppType appType, int type);
    List<Structure> findBySttAndPidOrderBySortAsc(int stt, Long pid);

    @Modifying
    @Query("UPDATE Structure s SET s.sort = :sort WHERE s.id = :id")
    void updateParentAndSort(Integer id, Integer sort);
}
