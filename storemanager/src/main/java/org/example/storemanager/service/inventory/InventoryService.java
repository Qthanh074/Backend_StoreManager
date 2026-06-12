package org.example.storemanager.service.inventory;

import org.example.storemanager.dto.response.inventory.InventoryResponse;
import java.math.BigDecimal;
import java.util.List;

public interface InventoryService {

    List<InventoryResponse> getInventoryByBranch(Long branchId);

    List<InventoryResponse> getAllInventory();

    InventoryResponse getInventoryByBranchAndProduct(Long branchId, Long productId);

    /**
     * Cập nhật tồn kho cho sản phẩm tại chi nhánh.
     * Tự động ghi nhận thẻ kho (StockLedger) khi thực hiện.
     */
    void updateStock(Long branchId, Long productId, BigDecimal quantityChange, String transactionType, Long referenceId, Long batchId);
}
