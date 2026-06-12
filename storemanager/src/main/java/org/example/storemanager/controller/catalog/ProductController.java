package org.example.storemanager.controller.catalog;

import jakarta.validation.Valid;
import org.example.storemanager.dto.request.catalog.ProductRequest;
import org.example.storemanager.dto.request.catalog.SerialNumberBatchRequest;
import org.example.storemanager.dto.response.catalog.ProductResponse;
import org.example.storemanager.dto.response.catalog.SerialNumberResponse;
import org.example.storemanager.dto.response.common.ApiResponse;
import org.example.storemanager.service.catalog.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:product:view')")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok(productService.searchProducts(search, categoryId, isActive, page, size)));
    }

    @GetMapping("/deleted")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:product:view')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getSoftDeletedProducts() {
        return ResponseEntity.ok(ApiResponse.ok(productService.getSoftDeletedProducts()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:product:view')")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getProductById(id)));
    }

    @PostMapping
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:product:create')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(productService.createProduct(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:product:update')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(productService.updateProduct(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:product:delete')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PostMapping("/{id}/image")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:product:update')")
    public ResponseEntity<ApiResponse<ProductResponse>> uploadProductImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.ok(productService.uploadProductImage(id, file)));
    }

    @GetMapping("/{id}/serials")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:product:serial:view')")
    public ResponseEntity<ApiResponse<List<SerialNumberResponse>>> getProductSerials(
            @PathVariable Long id,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getProductSerials(id, status)));
    }

    @PostMapping("/{id}/serials")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:product:serial:create')")
    public ResponseEntity<ApiResponse<List<SerialNumberResponse>>> addProductSerials(
            @PathVariable Long id,
            @Valid @RequestBody SerialNumberBatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(productService.addProductSerials(id, request)));
    }

    @GetMapping("/serials/deleted")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:product:serial:view')")
    public ResponseEntity<ApiResponse<List<SerialNumberResponse>>> getSoftDeletedSerials() {
        return ResponseEntity.ok(ApiResponse.ok(productService.getSoftDeletedSerials()));
    }
}
