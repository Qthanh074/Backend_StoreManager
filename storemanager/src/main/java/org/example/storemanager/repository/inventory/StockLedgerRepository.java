package org.example.storemanager.repository.inventory;

import org.example.storemanager.entity.inventory.StockLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockLedgerRepository extends JpaRepository<StockLedger, Long> {

    List<StockLedger> findByBranchIdAndProductIdAndIsDeletedFalseOrderByCreatedAtDesc(Long branchId, Long productId);

    List<StockLedger> findByBranchIdAndIsDeletedFalseOrderByCreatedAtDesc(Long branchId);

    List<StockLedger> findByProductIdAndIsDeletedFalseOrderByCreatedAtDesc(Long productId);

    List<StockLedger> findByIsDeletedFalseOrderByCreatedAtDesc();
}
