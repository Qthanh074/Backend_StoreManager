package org.example.storemanager.repository.inventory;

import org.example.storemanager.entity.inventory.ProductBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductBatchRepository extends JpaRepository<ProductBatch, Long> {

    Optional<ProductBatch> findByBatchNumberAndProductIdAndIsDeletedFalse(String batchNumber, Long productId);

    List<ProductBatch> findByProductIdAndIsDeletedFalse(Long productId);

    List<ProductBatch> findByIsDeletedFalse();
}
