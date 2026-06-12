package org.example.storemanager.controller.catalog;

import jakarta.validation.Valid;
import org.example.storemanager.dto.request.catalog.PriceListRequest;
import org.example.storemanager.dto.response.catalog.PriceListResponse;
import org.example.storemanager.dto.response.common.ApiResponse;
import org.example.storemanager.service.catalog.PriceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pricelists")
public class PriceListController {

    private final PriceListService priceListService;

    @Autowired
    public PriceListController(PriceListService priceListService) {
        this.priceListService = priceListService;
    }

    @GetMapping
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:pricelist:view')")
    public ResponseEntity<ApiResponse<List<PriceListResponse>>> getActivePriceLists(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Boolean isActive) {
        return ResponseEntity.ok(ApiResponse.ok(priceListService.getActivePriceLists(branchId, isActive)));
    }

    @GetMapping("/deleted")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:pricelist:view')")
    public ResponseEntity<ApiResponse<List<PriceListResponse>>> getSoftDeletedPriceLists() {
        return ResponseEntity.ok(ApiResponse.ok(priceListService.getSoftDeletedPriceLists()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:pricelist:view')")
    public ResponseEntity<ApiResponse<PriceListResponse>> getPriceListById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(priceListService.getPriceListById(id)));
    }

    @PostMapping
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:pricelist:create')")
    public ResponseEntity<ApiResponse<PriceListResponse>> createPriceList(@Valid @RequestBody PriceListRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(priceListService.createPriceList(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:pricelist:update')")
    public ResponseEntity<ApiResponse<PriceListResponse>> updatePriceList(@PathVariable Long id, @Valid @RequestBody PriceListRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(priceListService.updatePriceList(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:pricelist:delete')")
    public ResponseEntity<ApiResponse<Void>> deletePriceList(@PathVariable Long id) {
        priceListService.deletePriceList(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
