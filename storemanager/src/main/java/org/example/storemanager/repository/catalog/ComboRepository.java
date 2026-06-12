package org.example.storemanager.repository.catalog;

import org.example.storemanager.entity.catalog.Combo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComboRepository extends JpaRepository<Combo, Long> {
    Optional<Combo> findByComboCodeAndIsDeletedFalse(String comboCode);
    Optional<Combo> findByIdAndIsDeletedFalse(Long id);
    boolean existsByComboCodeAndIsDeletedFalse(String comboCode);

    @Query("SELECT c FROM Combo c WHERE c.isDeleted = false " +
           "AND (CAST(:search AS string) IS NULL " +
           "OR LOWER(c.comboName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
           "OR LOWER(c.comboCode) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))) " +
           "AND (:isActive IS NULL OR c.isActive = :isActive)")
    Page<Combo> searchCombos(String search, Boolean isActive, Pageable pageable);

    java.util.List<Combo> findAllByIsDeletedTrue();
}
