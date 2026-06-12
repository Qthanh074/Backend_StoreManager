package org.example.storemanager.controller.inventory;

import jakarta.validation.Valid;
import org.example.storemanager.dto.request.inventory.CancelIssueRequest;
import org.example.storemanager.dto.response.inventory.CancelIssueResponse;
import org.example.storemanager.dto.response.common.ApiResponse;
import org.example.storemanager.service.inventory.CancelIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories/cancel-issues")
public class CancelIssueController {

    private final CancelIssueService cancelService;

    @Autowired
    public CancelIssueController(CancelIssueService cancelService) {
        this.cancelService = cancelService;
    }

    @GetMapping
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:writeoff:manage')")
    public ResponseEntity<ApiResponse<List<CancelIssueResponse>>> getAllCancelIssues(
            @RequestParam(required = false) Long branchId) {
        if (branchId != null) {
            return ResponseEntity.ok(ApiResponse.ok(cancelService.getCancelIssuesByBranch(branchId)));
        }
        return ResponseEntity.ok(ApiResponse.ok(cancelService.getAllCancelIssues()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:writeoff:manage')")
    public ResponseEntity<ApiResponse<CancelIssueResponse>> getCancelIssueById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(cancelService.getCancelIssueById(id)));
    }

    @PostMapping
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:writeoff:manage')")
    public ResponseEntity<ApiResponse<CancelIssueResponse>> createCancelIssue(
            @Valid @RequestBody CancelIssueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(cancelService.createCancelIssue(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:writeoff:manage')")
    public ResponseEntity<ApiResponse<CancelIssueResponse>> updateCancelIssue(
            @PathVariable Long id, @Valid @RequestBody CancelIssueRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(cancelService.updateCancelIssue(id, request)));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:writeoff:manage')")
    public ResponseEntity<ApiResponse<CancelIssueResponse>> approveCancelIssue(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(cancelService.approveCancelIssue(id)));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:writeoff:manage')")
    public ResponseEntity<ApiResponse<CancelIssueResponse>> rejectCancelIssue(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(cancelService.rejectCancelIssue(id)));
    }
}
