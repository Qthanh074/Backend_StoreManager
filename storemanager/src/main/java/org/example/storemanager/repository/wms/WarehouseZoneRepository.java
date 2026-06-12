package org.example.storemanager.repository.wms;

import org.example.storemanager.entity.wms.WarehouseZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseZoneRepository extends JpaRepository<WarehouseZone, Long> {
    List<WarehouseZone> findByIsDeletedFalse();
    Optional<WarehouseZone> findByIdAndIsDeletedFalse(Long id);
    boolean existsByZoneCodeAndIsDeletedFalse(String zoneCode);
}
