package org.example.storemanager.controller.wms;

import jakarta.validation.Valid;
import org.example.storemanager.dto.request.wms.WarehouseBinRequest;
import org.example.storemanager.dto.request.wms.WarehouseZoneRequest;
import org.example.storemanager.dto.response.common.ApiResponse;
import org.example.storemanager.dto.response.wms.WarehouseBinResponse;
import org.example.storemanager.dto.response.wms.WarehouseZoneResponse;
import org.example.storemanager.service.wms.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class WarehouseController {

    private final WarehouseService warehouseService;

    @Autowired
    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    // ======================== ZONE ========================

    /**
     * GET /api/v1/warehouse/zones
     * Danh sách khu vực kho
     */
    @GetMapping("/api/v1/warehouse/zones")
    public ResponseEntity<ApiResponse<List<WarehouseZoneResponse>>> getAllZones() {
        return ResponseEntity.ok(ApiResponse.ok(warehouseService.getAllZones()));
    }

    /**
     * GET /api/v1/warehouse/zones/{id}
     */
    @GetMapping("/api/v1/warehouse/zones/{id}")
    public ResponseEntity<ApiResponse<WarehouseZoneResponse>> getZone(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(warehouseService.getZoneById(id)));
    }

    /**
     * POST /api/v1/warehouse/zones
     * Tạo khu vực kho mới — cần branchId hợp lệ
     */
    @PostMapping("/api/v1/warehouse/zones")
    public ResponseEntity<ApiResponse<WarehouseZoneResponse>> createZone(
            @Valid @RequestBody WarehouseZoneRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(warehouseService.createZone(request)));
    }

    /**
     * PUT /api/v1/warehouse/zones/{id}
     */
    @PutMapping("/api/v1/warehouse/zones/{id}")
    public ResponseEntity<ApiResponse<WarehouseZoneResponse>> updateZone(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseZoneRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(warehouseService.updateZone(id, request)));
    }

    /**
     * DELETE /api/v1/warehouse/zones/{id}
     */
    @DeleteMapping("/api/v1/warehouse/zones/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteZone(@PathVariable Long id) {
        warehouseService.deleteZone(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    // ======================== BIN ========================

    /**
     * GET /api/v1/warehouse/bins
     * Danh sách ô/kệ kho
     */
    @GetMapping("/api/v1/warehouse/bins")
    public ResponseEntity<ApiResponse<List<WarehouseBinResponse>>> getAllBins() {
        return ResponseEntity.ok(ApiResponse.ok(warehouseService.getAllBins()));
    }

    /**
     * GET /api/v1/warehouse/bins/{id}
     */
    @GetMapping("/api/v1/warehouse/bins/{id}")
    public ResponseEntity<ApiResponse<WarehouseBinResponse>> getBin(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(warehouseService.getBinById(id)));
    }

    /**
     * POST /api/v1/warehouse/bins
     * Tạo ô/kệ kho mới — cần zoneId hợp lệ
     */
    @PostMapping("/api/v1/warehouse/bins")
    public ResponseEntity<ApiResponse<WarehouseBinResponse>> createBin(
            @Valid @RequestBody WarehouseBinRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(warehouseService.createBin(request)));
    }

    /**
     * PUT /api/v1/warehouse/bins/{id}
     */
    @PutMapping("/api/v1/warehouse/bins/{id}")
    public ResponseEntity<ApiResponse<WarehouseBinResponse>> updateBin(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseBinRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(warehouseService.updateBin(id, request)));
    }

    /**
     * DELETE /api/v1/warehouse/bins/{id}
     */
    @DeleteMapping("/api/v1/warehouse/bins/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBin(@PathVariable Long id) {
        warehouseService.deleteBin(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
