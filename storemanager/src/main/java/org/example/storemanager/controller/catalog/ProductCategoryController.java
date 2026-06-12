package org.example.storemanager.controller.catalog;

import jakarta.validation.Valid;
import org.example.storemanager.dto.request.catalog.ProductCategoryRequest;
import org.example.storemanager.dto.response.catalog.ProductCategoryResponse;
import org.example.storemanager.dto.response.common.ApiResponse;
import org.example.storemanager.service.catalog.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    @Autowired
    public ProductCategoryController(ProductCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:category:view')")
    public ResponseEntity<ApiResponse<List<ProductCategoryResponse>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.getAllCategories()));
    }

    @GetMapping("/deleted")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:category:view')")
    public ResponseEntity<ApiResponse<List<ProductCategoryResponse>>> getSoftDeletedCategories() {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.getSoftDeletedCategories()));
    }

    @GetMapping("/roots")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:category:view')")
    public ResponseEntity<ApiResponse<List<ProductCategoryResponse>>> getRootCategories() {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.getRootCategories()));
    }

    @GetMapping("/{id}/children")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:category:view')")
    public ResponseEntity<ApiResponse<List<ProductCategoryResponse>>> getChildrenOf(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.getChildrenOf(id)));
    }

    @GetMapping("/tree")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:category:tree')")
    public ResponseEntity<ApiResponse<List<ProductCategoryResponse>>> getCategoryTree() {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.getCategoryTree()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:category:view')")
    public ResponseEntity<ApiResponse<ProductCategoryResponse>> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.getCategoryById(id)));
    }

    @PostMapping
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:category:create')")
    public ResponseEntity<ApiResponse<ProductCategoryResponse>> createCategory(@Valid @RequestBody ProductCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(categoryService.createCategory(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:category:update')")
    public ResponseEntity<ApiResponse<ProductCategoryResponse>> updateCategory(@PathVariable Long id, @Valid @RequestBody ProductCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.updateCategory(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:category:delete')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
