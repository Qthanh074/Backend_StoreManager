package org.example.storemanager.service.inventory;

import org.example.storemanager.dto.request.inventory.CancelIssueRequest;
import org.example.storemanager.dto.response.inventory.CancelIssueResponse;

import java.util.List;

public interface CancelIssueService {

    List<CancelIssueResponse> getAllCancelIssues();

    List<CancelIssueResponse> getCancelIssuesByBranch(Long branchId);

    CancelIssueResponse getCancelIssueById(Long id);

    CancelIssueResponse createCancelIssue(CancelIssueRequest request);

    CancelIssueResponse updateCancelIssue(Long id, CancelIssueRequest request);

    /**
     * Duyệt xuất hủy: trừ tồn kho và ghi sổ thẻ kho.
     */
    CancelIssueResponse approveCancelIssue(Long id);

    CancelIssueResponse rejectCancelIssue(Long id);
}
