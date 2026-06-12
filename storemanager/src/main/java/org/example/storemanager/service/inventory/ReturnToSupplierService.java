package org.example.storemanager.service.inventory;

import org.example.storemanager.dto.request.inventory.ReturnToSupplierRequest;
import org.example.storemanager.dto.response.inventory.ReturnToSupplierResponse;

import java.util.List;

public interface ReturnToSupplierService {

    List<ReturnToSupplierResponse> getAllReturns();

    List<ReturnToSupplierResponse> getReturnsByBranch(Long branchId);

    ReturnToSupplierResponse getReturnById(Long id);

    ReturnToSupplierResponse createReturn(ReturnToSupplierRequest request);

    ReturnToSupplierResponse updateReturn(Long id, ReturnToSupplierRequest request);

    /**
     * Phê duyệt xuất trả hàng: trừ tồn kho và ghi sổ thẻ kho.
     */
    ReturnToSupplierResponse approveReturn(Long id);

    ReturnToSupplierResponse rejectReturn(Long id);
}
