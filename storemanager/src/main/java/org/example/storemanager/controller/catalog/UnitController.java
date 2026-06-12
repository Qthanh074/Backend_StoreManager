package org.example.storemanager.controller.catalog;

import jakarta.validation.Valid;
import org.example.storemanager.dto.request.catalog.UnitRequest;
import org.example.storemanager.dto.response.catalog.UnitResponse;
import org.example.storemanager.dto.response.common.ApiResponse;
import org.example.storemanager.service.catalog.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/units")
public class UnitController {

    private final UnitService unitService;

    @Autowired
    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @GetMapping
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:unit:view')")
    public ResponseEntity<ApiResponse<List<UnitResponse>>> getAllUnits() {
        return ResponseEntity.ok(ApiResponse.ok(unitService.getAllUnits()));
    }

    @GetMapping("/deleted")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:unit:view')")
    public ResponseEntity<ApiResponse<List<UnitResponse>>> getSoftDeletedUnits() {
        return ResponseEntity.ok(ApiResponse.ok(unitService.getSoftDeletedUnits()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:unit:view')")
    public ResponseEntity<ApiResponse<UnitResponse>> getUnitById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(unitService.getUnitById(id)));
    }

    @PostMapping
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:unit:create')")
    public ResponseEntity<ApiResponse<UnitResponse>> createUnit(@Valid @RequestBody UnitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(unitService.createUnit(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:unit:update')")
    public ResponseEntity<ApiResponse<UnitResponse>> updateUnit(@PathVariable Long id, @Valid @RequestBody UnitRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(unitService.updateUnit(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:unit:delete')")
    public ResponseEntity<ApiResponse<Void>> deleteUnit(@PathVariable Long id) {
        unitService.deleteUnit(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
