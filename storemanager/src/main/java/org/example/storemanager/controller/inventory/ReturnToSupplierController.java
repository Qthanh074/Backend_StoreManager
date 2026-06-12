package org.example.storemanager.controller.inventory;

import jakarta.validation.Valid;
import org.example.storemanager.dto.request.inventory.ReturnToSupplierRequest;
import org.example.storemanager.dto.response.inventory.ReturnToSupplierResponse;
import org.example.storemanager.dto.response.common.ApiResponse;
import org.example.storemanager.service.inventory.ReturnToSupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories/returns")
public class ReturnToSupplierController {

    private final ReturnToSupplierService returnService;

    @Autowired
    public ReturnToSupplierController(ReturnToSupplierService returnService) {
        this.returnService = returnService;
    }

    @GetMapping
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:returns:manage')")
    public ResponseEntity<ApiResponse<List<ReturnToSupplierResponse>>> getAllReturns(
            @RequestParam(required = false) Long branchId) {
        if (branchId != null) {
            return ResponseEntity.ok(ApiResponse.ok(returnService.getReturnsByBranch(branchId)));
        }
        return ResponseEntity.ok(ApiResponse.ok(returnService.getAllReturns()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:returns:manage')")
    public ResponseEntity<ApiResponse<ReturnToSupplierResponse>> getReturnById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(returnService.getReturnById(id)));
    }

    @PostMapping
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:returns:manage')")
    public ResponseEntity<ApiResponse<ReturnToSupplierResponse>> createReturn(
            @Valid @RequestBody ReturnToSupplierRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(returnService.createReturn(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:returns:manage')")
    public ResponseEntity<ApiResponse<ReturnToSupplierResponse>> updateReturn(
            @PathVariable Long id, @Valid @RequestBody ReturnToSupplierRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(returnService.updateReturn(id, request)));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:returns:manage')")
    public ResponseEntity<ApiResponse<ReturnToSupplierResponse>> approveReturn(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(returnService.approveReturn(id)));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:returns:manage')")
    public ResponseEntity<ApiResponse<ReturnToSupplierResponse>> rejectReturn(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(returnService.rejectReturn(id)));
    }
}
