package org.example.storemanager.repository.catalog;

import org.example.storemanager.entity.catalog.ProductUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductUnitRepository extends JpaRepository<ProductUnit, Long> {
    List<ProductUnit> findAllByProductIdAndIsDeletedFalse(Long productId);
    void deleteAllByProductId(Long productId);
}
