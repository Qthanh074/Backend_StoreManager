package org.example.storemanager.repository.wms;

import org.example.storemanager.entity.wms.ProductLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductLocationRepository extends JpaRepository<ProductLocation, Long> {
    Optional<ProductLocation> findByProductIdAndBinIdAndIsDeletedFalse(Long productId, Long binId);
    List<ProductLocation> findAllByIsDeletedFalse();
    List<ProductLocation> findAllByProductIdAndIsDeletedFalse(Long productId);
    List<ProductLocation> findAllByBinIdAndIsDeletedFalse(Long binId);
}
