package org.example.storemanager.repository.catalog;

import org.example.storemanager.entity.catalog.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceListRepository extends JpaRepository<PriceList, Long> {
    Optional<PriceList> findByListCodeAndIsDeletedFalse(String listCode);
    Optional<PriceList> findByIdAndIsDeletedFalse(Long id);
    boolean existsByListCodeAndIsDeletedFalse(String listCode);

    @Query("SELECT p FROM PriceList p WHERE p.isDeleted = false " +
           "AND (:branchId IS NULL OR p.branch.id = :branchId) " +
           "AND (:isActive IS NULL OR p.isActive = :isActive) " +
           "AND (:now IS NULL OR ((p.startDate IS NULL OR p.startDate <= :now) AND (p.endDate IS NULL OR p.endDate >= :now)))")
    List<PriceList> findActivePriceLists(Long branchId, Boolean isActive, LocalDateTime now);

    List<PriceList> findAllByIsDeletedTrue();
}
