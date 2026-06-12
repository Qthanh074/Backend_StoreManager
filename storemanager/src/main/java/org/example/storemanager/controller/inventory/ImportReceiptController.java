package org.example.storemanager.controller.inventory;

import jakarta.validation.Valid;
import org.example.storemanager.dto.request.inventory.ImportReceiptRequest;
import org.example.storemanager.dto.response.inventory.ImportReceiptResponse;
import org.example.storemanager.dto.response.common.ApiResponse;
import org.example.storemanager.service.inventory.ImportReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories/imports")
public class ImportReceiptController {

    private final ImportReceiptService receiptService;

    @Autowired
    public ImportReceiptController(ImportReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:imports:manage')")
    public ResponseEntity<ApiResponse<List<ImportReceiptResponse>>> getAllReceipts(
            @RequestParam(required = false) Long branchId) {
        if (branchId != null) {
            return ResponseEntity.ok(ApiResponse.ok(receiptService.getReceiptsByBranch(branchId)));
        }
        return ResponseEntity.ok(ApiResponse.ok(receiptService.getAllReceipts()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:imports:manage')")
    public ResponseEntity<ApiResponse<ImportReceiptResponse>> getReceiptById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(receiptService.getReceiptById(id)));
    }

    @PostMapping
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:imports:manage')")
    public ResponseEntity<ApiResponse<ImportReceiptResponse>> createReceipt(
            @Valid @RequestBody ImportReceiptRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(receiptService.createReceipt(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:imports:manage')")
    public ResponseEntity<ApiResponse<ImportReceiptResponse>> updateReceipt(
            @PathVariable Long id, @Valid @RequestBody ImportReceiptRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(receiptService.updateReceipt(id, request)));
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:imports:manage')")
    public ResponseEntity<ApiResponse<ImportReceiptResponse>> completeReceipt(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(receiptService.completeReceipt(id)));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:imports:manage')")
    public ResponseEntity<ApiResponse<ImportReceiptResponse>> cancelReceipt(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(receiptService.cancelReceipt(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:imports:manage')")
    public ResponseEntity<ApiResponse<Void>> deleteReceipt(@PathVariable Long id) {
        receiptService.deleteReceipt(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
