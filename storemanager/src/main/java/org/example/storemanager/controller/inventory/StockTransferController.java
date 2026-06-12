package org.example.storemanager.controller.inventory;

import jakarta.validation.Valid;
import org.example.storemanager.dto.request.inventory.StockTransferRequest;
import org.example.storemanager.dto.response.inventory.StockTransferResponse;
import org.example.storemanager.dto.response.common.ApiResponse;
import org.example.storemanager.service.inventory.StockTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories/transfers")
public class StockTransferController {

    private final StockTransferService transferService;

    @Autowired
    public StockTransferController(StockTransferService transferService) {
        this.transferService = transferService;
    }

    @GetMapping
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:transfers:manage')")
    public ResponseEntity<ApiResponse<List<StockTransferResponse>>> getAllTransfers(
            @RequestParam(required = false) Long fromBranchId,
            @RequestParam(required = false) Long toBranchId) {
        if (fromBranchId != null) {
            return ResponseEntity.ok(ApiResponse.ok(transferService.getTransfersByFromBranch(fromBranchId)));
        } else if (toBranchId != null) {
            return ResponseEntity.ok(ApiResponse.ok(transferService.getTransfersByToBranch(toBranchId)));
        }
        return ResponseEntity.ok(ApiResponse.ok(transferService.getAllTransfers()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:transfers:manage')")
    public ResponseEntity<ApiResponse<StockTransferResponse>> getTransferById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(transferService.getTransferById(id)));
    }

    @PostMapping
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:transfers:manage')")
    public ResponseEntity<ApiResponse<StockTransferResponse>> createTransfer(
            @Valid @RequestBody StockTransferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(transferService.createTransfer(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:transfers:manage')")
    public ResponseEntity<ApiResponse<StockTransferResponse>> updateTransfer(
            @PathVariable Long id, @Valid @RequestBody StockTransferRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(transferService.updateTransfer(id, request)));
    }

    @PutMapping("/{id}/ship")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:transfers:manage')")
    public ResponseEntity<ApiResponse<StockTransferResponse>> shipTransfer(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(transferService.shipTransfer(id)));
    }

    @PutMapping("/{id}/receive")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:transfers:manage')")
    public ResponseEntity<ApiResponse<StockTransferResponse>> receiveTransfer(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(transferService.receiveTransfer(id)));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:transfers:manage')")
    public ResponseEntity<ApiResponse<StockTransferResponse>> rejectTransfer(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(transferService.rejectTransfer(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:transfers:manage')")
    public ResponseEntity<ApiResponse<Void>> deleteTransfer(@PathVariable Long id) {
        transferService.deleteTransfer(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
