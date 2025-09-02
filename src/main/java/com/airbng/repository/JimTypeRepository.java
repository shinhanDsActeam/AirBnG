package com.airbng.repository;

import com.airbng.domain.jimtype.JimType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JimTypeRepository extends JpaRepository<JimType, Long> {
    @Query("SELECT j.jimTypeId FROM JimType j WHERE j.jimTypeId IN :ids")
    List<Long> findValidIds(@Param("ids") List<Long> ids);
}
