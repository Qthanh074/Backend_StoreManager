package org.example.storemanager.repository.inventory;

import org.example.storemanager.entity.inventory.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByBranchIdAndProductIdAndIsDeletedFalse(Long branchId, Long productId);

    List<Inventory> findByBranchIdAndIsDeletedFalse(Long branchId);

    List<Inventory> findByProductIdAndIsDeletedFalse(Long productId);

    List<Inventory> findByIsDeletedFalse();
}
