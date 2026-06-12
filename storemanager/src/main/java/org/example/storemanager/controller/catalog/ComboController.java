package org.example.storemanager.controller.catalog;

import jakarta.validation.Valid;
import org.example.storemanager.dto.request.catalog.ComboRequest;
import org.example.storemanager.dto.response.catalog.ComboResponse;
import org.example.storemanager.dto.response.common.ApiResponse;
import org.example.storemanager.service.catalog.ComboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/combos")
public class ComboController {

    private final ComboService comboService;

    @Autowired
    public ComboController(ComboService comboService) {
        this.comboService = comboService;
    }

    @GetMapping
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:combo:view')")
    public ResponseEntity<ApiResponse<Page<ComboResponse>>> searchCombos(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok(comboService.searchCombos(search, isActive, page, size)));
    }

    @GetMapping("/deleted")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:combo:view')")
    public ResponseEntity<ApiResponse<java.util.List<ComboResponse>>> getSoftDeletedCombos() {
        return ResponseEntity.ok(ApiResponse.ok(comboService.getSoftDeletedCombos()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:combo:view')")
    public ResponseEntity<ApiResponse<ComboResponse>> getComboById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(comboService.getComboById(id)));
    }

    @PostMapping
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:combo:create')")
    public ResponseEntity<ApiResponse<ComboResponse>> createCombo(@Valid @RequestBody ComboRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(comboService.createCombo(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:combo:update')")
    public ResponseEntity<ApiResponse<ComboResponse>> updateCombo(@PathVariable Long id, @Valid @RequestBody ComboRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(comboService.updateCombo(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('catalog:combo:delete')")
    public ResponseEntity<ApiResponse<Void>> deleteCombo(@PathVariable Long id) {
        comboService.deleteCombo(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
