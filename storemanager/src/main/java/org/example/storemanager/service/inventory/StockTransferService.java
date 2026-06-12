package org.example.storemanager.service.inventory;

import org.example.storemanager.dto.request.inventory.StockTransferRequest;
import org.example.storemanager.dto.response.inventory.StockTransferResponse;

import java.util.List;

public interface StockTransferService {

    List<StockTransferResponse> getAllTransfers();

    List<StockTransferResponse> getTransfersByFromBranch(Long branchId);

    List<StockTransferResponse> getTransfersByToBranch(Long branchId);

    StockTransferResponse getTransferById(Long id);

    StockTransferResponse createTransfer(StockTransferRequest request);

    StockTransferResponse updateTransfer(Long id, StockTransferRequest request);

    /**
     * Xuất hàng đi (Trừ tồn kho chi nhánh gửi)
     */
    StockTransferResponse shipTransfer(Long id);

    /**
     * Nhận hàng đến (Cộng tồn kho chi nhánh nhận)
     */
    StockTransferResponse receiveTransfer(Long id);

    StockTransferResponse rejectTransfer(Long id);

    void deleteTransfer(Long id);
}
