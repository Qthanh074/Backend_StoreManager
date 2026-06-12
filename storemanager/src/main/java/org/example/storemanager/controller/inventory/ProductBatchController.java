package org.example.storemanager.controller.inventory;

import jakarta.validation.Valid;
import org.example.storemanager.dto.request.inventory.ProductBatchRequest;
import org.example.storemanager.dto.response.inventory.ProductBatchResponse;
import org.example.storemanager.dto.response.common.ApiResponse;
import org.example.storemanager.service.inventory.ProductBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories/batches")
public class ProductBatchController {

    private final ProductBatchService batchService;

    @Autowired
    public ProductBatchController(ProductBatchService batchService) {
        this.batchService = batchService;
    }

    @GetMapping
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:products:view')")
    public ResponseEntity<ApiResponse<List<ProductBatchResponse>>> getAllBatches() {
        return ResponseEntity.ok(ApiResponse.ok(batchService.getAllBatches()));
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:products:view')")
    public ResponseEntity<ApiResponse<List<ProductBatchResponse>>> getBatchesByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.ok(batchService.getBatchesByProduct(productId)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:products:view')")
    public ResponseEntity<ApiResponse<ProductBatchResponse>> getBatchById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(batchService.getBatchById(id)));
    }

    @PostMapping
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:products:write')")
    public ResponseEntity<ApiResponse<ProductBatchResponse>> createBatch(
            @Valid @RequestBody ProductBatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(batchService.createBatch(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:products:write')")
    public ResponseEntity<ApiResponse<ProductBatchResponse>> updateBatch(
            @PathVariable Long id, @Valid @RequestBody ProductBatchRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(batchService.updateBatch(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:products:write')")
    public ResponseEntity<ApiResponse<Void>> deleteBatch(@PathVariable Long id) {
        batchService.deleteBatch(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
