package org.example.storemanager.repository.inventory;

import org.example.storemanager.entity.inventory.InventoryCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryCheckRepository extends JpaRepository<InventoryCheck, Long> {

    List<InventoryCheck> findByIsDeletedFalse();

    List<InventoryCheck> findByBranchIdAndIsDeletedFalse(Long branchId);

    List<InventoryCheck> findByStatusAndIsDeletedFalse(String status);

    boolean existsByCheckCodeAndIsDeletedFalse(String checkCode);
}
