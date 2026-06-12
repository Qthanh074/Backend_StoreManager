package org.example.storemanager.repository.inventory;

import org.example.storemanager.entity.inventory.ReturnToSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReturnToSupplierRepository extends JpaRepository<ReturnToSupplier, Long> {

    Optional<ReturnToSupplier> findByReturnCodeAndIsDeletedFalse(String returnCode);

    List<ReturnToSupplier> findByBranchIdAndIsDeletedFalse(Long branchId);

    List<ReturnToSupplier> findByIsDeletedFalse();
}
