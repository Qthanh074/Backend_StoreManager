package org.example.storemanager.repository.inventory;

import org.example.storemanager.entity.inventory.ImportReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImportReceiptRepository extends JpaRepository<ImportReceipt, Long> {

    Optional<ImportReceipt> findByReceiptCodeAndIsDeletedFalse(String receiptCode);

    List<ImportReceipt> findByBranchIdAndIsDeletedFalse(Long branchId);

    List<ImportReceipt> findByIsDeletedFalse();
}
