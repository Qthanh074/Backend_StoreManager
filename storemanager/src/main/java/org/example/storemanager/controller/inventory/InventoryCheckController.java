package org.example.storemanager.controller.inventory;

import jakarta.validation.Valid;
import org.example.storemanager.dto.request.inventory.InventoryCheckRequest;
import org.example.storemanager.dto.response.inventory.InventoryCheckResponse;
import org.example.storemanager.dto.response.common.ApiResponse;
import org.example.storemanager.service.inventory.InventoryCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories/checks")
public class InventoryCheckController {

    private final InventoryCheckService checkService;

    @Autowired
    public InventoryCheckController(InventoryCheckService checkService) {
        this.checkService = checkService;
    }

    /**
     * Lấy danh sách phiếu kiểm kê.
     * Nếu truyền branchId → lọc theo chi nhánh.
     */
    @GetMapping
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:check:manage')")
    public ResponseEntity<ApiResponse<List<InventoryCheckResponse>>> getAllChecks(
            @RequestParam(required = false) Long branchId) {
        if (branchId != null) {
            return ResponseEntity.ok(ApiResponse.ok(checkService.getChecksByBranch(branchId)));
        }
        return ResponseEntity.ok(ApiResponse.ok(checkService.getAllChecks()));
    }

    /**
     * Lấy chi tiết một phiếu kiểm kê.
     */
    @GetMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:check:manage')")
    public ResponseEntity<ApiResponse<InventoryCheckResponse>> getCheckById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(checkService.getCheckById(id)));
    }

    /**
     * Tạo phiếu kiểm kê mới (DRAFT).
     * systemQty sẽ được tự động lấy từ tồn kho hiện tại tại chi nhánh.
     */
    @PostMapping
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:check:manage')")
    public ResponseEntity<ApiResponse<InventoryCheckResponse>> createCheck(
            @Valid @RequestBody InventoryCheckRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(checkService.createCheck(request)));
    }

    /**
     * Cập nhật phiếu kiểm kê (chỉ khi DRAFT).
     */
    @PutMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:check:manage')")
    public ResponseEntity<ApiResponse<InventoryCheckResponse>> updateCheck(
            @PathVariable Long id, @Valid @RequestBody InventoryCheckRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(checkService.updateCheck(id, request)));
    }

    /**
     * Bắt đầu kiểm kê thực tế: DRAFT → IN_PROGRESS.
     */
    @PutMapping("/{id}/start")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:check:manage')")
    public ResponseEntity<ApiResponse<InventoryCheckResponse>> startCheck(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(checkService.startCheck(id)));
    }

    /**
     * Hoàn tất kiểm kê: IN_PROGRESS → COMPLETED.
     * Tự động điều chỉnh tồn kho theo chênh lệch và ghi StockLedger.
     */
    @PutMapping("/{id}/complete")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:check:manage')")
    public ResponseEntity<ApiResponse<InventoryCheckResponse>> completeCheck(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(checkService.completeCheck(id)));
    }

    /**
     * Hủy phiếu kiểm kê (chỉ khi DRAFT hoặc IN_PROGRESS).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('inventory:check:manage')")
    public ResponseEntity<ApiResponse<InventoryCheckResponse>> cancelCheck(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(checkService.cancelCheck(id)));
    }
}
