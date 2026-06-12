package org.example.storemanager.repository.inventory;

import org.example.storemanager.entity.inventory.StockTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockTransferRepository extends JpaRepository<StockTransfer, Long> {

    Optional<StockTransfer> findByTransferCodeAndIsDeletedFalse(String transferCode);

    List<StockTransfer> findByFromBranchIdAndIsDeletedFalse(Long fromBranchId);

    List<StockTransfer> findByToBranchIdAndIsDeletedFalse(Long toBranchId);

    List<StockTransfer> findByIsDeletedFalse();
}
