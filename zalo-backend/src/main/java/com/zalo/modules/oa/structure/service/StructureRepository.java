package com.zalo.modules.oa.structure.service;

import com.zalo.modules.oa.structure.entity.Structure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StructureRepository extends JpaRepository<Structure, Long> {
}
