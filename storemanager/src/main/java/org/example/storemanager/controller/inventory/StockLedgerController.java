package org.example.storemanager.controller.inventory;

import org.example.storemanager.dto.response.inventory.StockLedgerResponse;
import org.example.storemanager.dto.response.common.ApiResponse;
import org.example.storemanager.service.inventory.StockLedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories/ledger")
public class StockLedgerController {

    private final StockLedgerService ledgerService;

    @Autowired
    public StockLedgerController(StockLedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @GetMapping
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:ledger:view')")
    public ResponseEntity<ApiResponse<List<StockLedgerResponse>>> getLedgerEntries(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long productId) {
        if (branchId != null && productId != null) {
            return ResponseEntity.ok(ApiResponse.ok(ledgerService.getLedgerEntriesByBranchAndProduct(branchId, productId)));
        } else if (branchId != null) {
            return ResponseEntity.ok(ApiResponse.ok(ledgerService.getLedgerEntriesByBranch(branchId)));
        } else if (productId != null) {
            return ResponseEntity.ok(ApiResponse.ok(ledgerService.getLedgerEntriesByProduct(productId)));
        }
        return ResponseEntity.ok(ApiResponse.ok(ledgerService.getAllLedgerEntries()));
    }
}
