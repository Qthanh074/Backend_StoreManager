package org.example.storemanager.controller.wms;

import jakarta.validation.Valid;
import org.example.storemanager.dto.request.wms.ProductLocationAssignRequest;
import org.example.storemanager.dto.response.wms.ProductLocationResponse;
import org.example.storemanager.dto.response.common.ApiResponse;
import org.example.storemanager.service.wms.ProductLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-locations")
public class ProductLocationController {

    private final ProductLocationService productLocationService;

    @Autowired
    public ProductLocationController(ProductLocationService productLocationService) {
        this.productLocationService = productLocationService;
    }

    @GetMapping
    @PreAuthorize("@securityEvaluator.hasPermission('wms:location:view')")
    public ResponseEntity<ApiResponse<List<ProductLocationResponse>>> getProductLocations(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long binId) {
        return ResponseEntity.ok(ApiResponse.ok(productLocationService.getProductLocations(productId, binId)));
    }

    @PostMapping("/assign")
    @PreAuthorize("@securityEvaluator.hasPermission('wms:location:assign')")
    public ResponseEntity<ApiResponse<ProductLocationResponse>> assignProductLocation(
            @Valid @RequestBody ProductLocationAssignRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(productLocationService.assignProductLocation(request)));
    }
}
