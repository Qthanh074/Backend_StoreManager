package org.example.storemanager.service.inventory;

import org.example.storemanager.dto.request.inventory.ImportReceiptRequest;
import org.example.storemanager.dto.response.inventory.ImportReceiptResponse;

import java.util.List;

public interface ImportReceiptService {

    List<ImportReceiptResponse> getAllReceipts();

    List<ImportReceiptResponse> getReceiptsByBranch(Long branchId);

    ImportReceiptResponse getReceiptById(Long id);

    ImportReceiptResponse createReceipt(ImportReceiptRequest request);

    ImportReceiptResponse updateReceipt(Long id, ImportReceiptRequest request);

    /**
     * Xác nhận hoàn tất nhập kho: cộng tồn kho, lưu thông tin lô sản phẩm, ghi sổ kho.
     */
    ImportReceiptResponse completeReceipt(Long id);

    ImportReceiptResponse cancelReceipt(Long id);

    void deleteReceipt(Long id);
}
