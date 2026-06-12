package org.example.storemanager.controller.inventory;

import org.example.storemanager.dto.response.inventory.InventoryResponse;
import org.example.storemanager.dto.response.common.ApiResponse;
import org.example.storemanager.service.inventory.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:products:view')")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventory(
            @RequestParam(required = false) Long branchId) {
        if (branchId != null) {
            return ResponseEntity.ok(ApiResponse.ok(inventoryService.getInventoryByBranch(branchId)));
        }
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getAllInventory()));
    }

    @GetMapping("/branch/{branchId}/product/{productId}")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:products:view')")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventoryByBranchAndProduct(
            @PathVariable Long branchId, @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getInventoryByBranchAndProduct(branchId, productId)));
    }
}
