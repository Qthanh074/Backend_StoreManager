package org.example.storemanager.repository.catalog;

import org.example.storemanager.entity.catalog.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    Optional<Unit> findByUnitCodeAndIsDeletedFalse(String unitCode);
    Optional<Unit> findByIdAndIsDeletedFalse(Long id);
    boolean existsByUnitCodeAndIsDeletedFalse(String unitCode);
    List<Unit> findAllByIsDeletedFalse();
    List<Unit> findAllByIsDeletedTrue();
}
