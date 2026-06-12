package org.example.storemanager.repository.wms;

import org.example.storemanager.entity.wms.WarehouseBin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WarehouseBinRepository extends JpaRepository<WarehouseBin, Long> {
    Optional<WarehouseBin> findByIdAndIsDeletedFalse(Long id);
    Optional<WarehouseBin> findByBinCodeAndIsDeletedFalse(String binCode);
}
