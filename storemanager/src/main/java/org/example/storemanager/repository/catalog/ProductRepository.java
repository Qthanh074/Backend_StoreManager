package org.example.storemanager.repository.catalog;

import org.example.storemanager.entity.catalog.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductCodeAndIsDeletedFalse(String productCode);
    Optional<Product> findByIdAndIsDeletedFalse(Long id);
    boolean existsByProductCodeAndIsDeletedFalse(String productCode);

    @Query("SELECT p FROM Product p WHERE p.isDeleted = false " +
           "AND (CAST(:search AS string) IS NULL " +
           "OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
           "OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
           "OR LOWER(p.barcode) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))) " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:isActive IS NULL OR p.isActive = :isActive)")
    Page<Product> searchProducts(String search, Long categoryId, Boolean isActive, Pageable pageable);

    java.util.List<Product> findAllByIsDeletedTrue();
}
