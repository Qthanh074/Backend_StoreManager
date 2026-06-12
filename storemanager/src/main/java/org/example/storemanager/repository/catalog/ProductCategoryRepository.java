package org.example.storemanager.repository.catalog;

import org.example.storemanager.entity.catalog.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    Optional<ProductCategory> findByCategoryCodeAndIsDeletedFalse(String categoryCode);
    Optional<ProductCategory> findByIdAndIsDeletedFalse(Long id);
    boolean existsByCategoryCodeAndIsDeletedFalse(String categoryCode);
    List<ProductCategory> findAllByIsDeletedFalse();
    List<ProductCategory> findAllByIsDeletedTrue();
}
