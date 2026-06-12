package org.example.storemanager.repository.catalog;

import org.example.storemanager.entity.catalog.PriceListDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceListDetailRepository extends JpaRepository<PriceListDetail, Long> {
    List<PriceListDetail> findAllByPriceListIdAndIsDeletedFalse(Long priceListId);
    Optional<PriceListDetail> findByPriceListIdAndProductIdAndIsDeletedFalse(Long priceListId, Long productId);
    void deleteAllByPriceListId(Long priceListId);
}
