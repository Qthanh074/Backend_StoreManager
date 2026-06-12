package org.example.storemanager.service.inventory;

import org.example.storemanager.dto.response.inventory.StockLedgerResponse;
import java.math.BigDecimal;
import java.util.List;

public interface StockLedgerService {

    List<StockLedgerResponse> getAllLedgerEntries();

    List<StockLedgerResponse> getLedgerEntriesByBranch(Long branchId);

    List<StockLedgerResponse> getLedgerEntriesByProduct(Long productId);

    List<StockLedgerResponse> getLedgerEntriesByBranchAndProduct(Long branchId, Long productId);

    /**
     * Ghi nhận một dòng biến động tồn kho vào thẻ kho.
     */
    void logChange(Long branchId, Long productId, BigDecimal changeQty, BigDecimal balanceAfter, String transactionType, Long referenceId, Long batchId);
}
