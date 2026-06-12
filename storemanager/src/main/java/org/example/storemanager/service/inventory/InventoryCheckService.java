package org.example.storemanager.service.inventory;

import org.example.storemanager.dto.request.inventory.InventoryCheckRequest;
import org.example.storemanager.dto.response.inventory.InventoryCheckResponse;

import java.util.List;

public interface InventoryCheckService {

    List<InventoryCheckResponse> getAllChecks();

    List<InventoryCheckResponse> getChecksByBranch(Long branchId);

    InventoryCheckResponse getCheckById(Long id);

    /**
     * Tạo phiếu kiểm kê DRAFT. Tự động lấy systemQty từ tồn kho hiện tại,
     * tính diffQty = actualQty - systemQty.
     */
    InventoryCheckResponse createCheck(InventoryCheckRequest request);

    /**
     * Cập nhật phiếu kiểm kê (chỉ khi còn DRAFT).
     */
    InventoryCheckResponse updateCheck(Long id, InventoryCheckRequest request);

    /**
     * Chuyển trạng thái sang IN_PROGRESS (bắt đầu kiểm kê thực tế).
     */
    InventoryCheckResponse startCheck(Long id);

    /**
     * Hoàn tất kiểm kê: chuyển sang COMPLETED và tự động điều chỉnh tồn kho
     * theo chênh lệch (diffQty). Ghi StockLedger với loại STOCK_ADJUSTMENT_PLUS/MINUS.
     */
    InventoryCheckResponse completeCheck(Long id);

    /**
     * Hủy phiếu kiểm kê (chỉ khi DRAFT hoặc IN_PROGRESS, chưa điều chỉnh kho).
     */
    InventoryCheckResponse cancelCheck(Long id);
}
