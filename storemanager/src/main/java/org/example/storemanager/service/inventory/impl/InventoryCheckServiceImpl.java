package org.example.storemanager.service.inventory.impl;

import org.example.storemanager.dto.request.inventory.CheckLineRequest;
import org.example.storemanager.dto.request.inventory.InventoryCheckRequest;
import org.example.storemanager.dto.response.inventory.CheckLineResponse;
import org.example.storemanager.dto.response.inventory.InventoryCheckResponse;
import org.example.storemanager.entity.catalog.Product;
import org.example.storemanager.entity.catalog.Unit;
import org.example.storemanager.entity.inventory.Inventory;
import org.example.storemanager.entity.inventory.InventoryCheck;
import org.example.storemanager.entity.inventory.InventoryCheckDetail;
import org.example.storemanager.entity.system.Branch;
import org.example.storemanager.enums.ErrorCode;
import org.example.storemanager.enums.inventory.CheckStatus;
import org.example.storemanager.exception.BusinessException;
import org.example.storemanager.exception.ResourceNotFoundException;
import org.example.storemanager.repository.catalog.ProductRepository;
import org.example.storemanager.repository.catalog.UnitRepository;
import org.example.storemanager.repository.inventory.InventoryCheckDetailRepository;
import org.example.storemanager.repository.inventory.InventoryCheckRepository;
import org.example.storemanager.repository.inventory.InventoryRepository;
import org.example.storemanager.repository.system.BranchRepository;
import org.example.storemanager.service.inventory.InventoryCheckService;
import org.example.storemanager.service.inventory.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryCheckServiceImpl implements InventoryCheckService {

    private final InventoryCheckRepository checkRepository;
    private final InventoryCheckDetailRepository detailRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final UnitRepository unitRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;

    @Autowired
    public InventoryCheckServiceImpl(InventoryCheckRepository checkRepository,
                                     InventoryCheckDetailRepository detailRepository,
                                     BranchRepository branchRepository,
                                     ProductRepository productRepository,
                                     UnitRepository unitRepository,
                                     InventoryRepository inventoryRepository,
                                     InventoryService inventoryService) {
        this.checkRepository = checkRepository;
        this.detailRepository = detailRepository;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
        this.unitRepository = unitRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryService = inventoryService;
    }

    private String getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        }
        return "system";
    }

    // ─────────────────────────────────────────────────────────
    // Queries
    // ─────────────────────────────────────────────────────────

    @Override
    public List<InventoryCheckResponse> getAllChecks() {
        return checkRepository.findByIsDeletedFalse().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<InventoryCheckResponse> getChecksByBranch(Long branchId) {
        return checkRepository.findByBranchIdAndIsDeletedFalse(branchId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public InventoryCheckResponse getCheckById(Long id) {
        InventoryCheck check = findCheckOrThrow(id);
        return mapToResponse(check);
    }

    // ─────────────────────────────────────────────────────────
    // Commands
    // ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public InventoryCheckResponse createCheck(InventoryCheckRequest request) {
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRANCH_NOT_FOUND, "Chi nhánh", "id", request.getBranchId()));

        // Kiểm tra mã phiếu trùng
        if (checkRepository.existsByCheckCodeAndIsDeletedFalse(request.getCheckCode())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "Mã phiếu kiểm kê [" + request.getCheckCode() + "] đã tồn tại.");
        }

        CheckStatus initialStatus = request.getStatus() != null ? request.getStatus() : CheckStatus.DRAFT;

        InventoryCheck check = InventoryCheck.builder()
                .checkCode(request.getCheckCode())
                .checkDate(request.getCheckDate() != null ? request.getCheckDate() : LocalDateTime.now())
                .status(initialStatus.name())
                .branch(branch)
                .build();

        check.setCreatedBy(getCurrentUser());
        InventoryCheck savedCheck = checkRepository.save(check);

        buildAndSaveDetails(request.getCheckLines(), savedCheck, branch.getId());

        return mapToResponse(savedCheck);
    }

    @Override
    @Transactional
    public InventoryCheckResponse updateCheck(Long id, InventoryCheckRequest request) {
        InventoryCheck check = findCheckOrThrow(id);

        // Chỉ cho sửa khi còn DRAFT
        if (!CheckStatus.DRAFT.name().equals(check.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION,
                    "Chỉ có thể chỉnh sửa phiếu kiểm kê ở trạng thái DRAFT (đang nháp).");
        }

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRANCH_NOT_FOUND, "Chi nhánh", "id", request.getBranchId()));

        check.setCheckCode(request.getCheckCode());
        check.setCheckDate(request.getCheckDate());
        check.setBranch(branch);
        check.setUpdatedBy(getCurrentUser());

        // Soft-delete các dòng cũ
        List<InventoryCheckDetail> oldDetails = detailRepository.findByCheckIdAndIsDeletedFalse(id);
        for (InventoryCheckDetail d : oldDetails) {
            d.setIsDeleted(true);
            d.setDeletedAt(LocalDateTime.now());
            d.setDeletedBy(getCurrentUser());
            detailRepository.save(d);
        }

        buildAndSaveDetails(request.getCheckLines(), check, branch.getId());

        InventoryCheck saved = checkRepository.save(check);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public InventoryCheckResponse startCheck(Long id) {
        InventoryCheck check = findCheckOrThrow(id);

        if (!CheckStatus.DRAFT.name().equals(check.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION,
                    "Chỉ có thể bắt đầu kiểm kê từ trạng thái DRAFT.");
        }

        check.setStatus(CheckStatus.IN_PROGRESS.name());
        check.setUpdatedBy(getCurrentUser());
        return mapToResponse(checkRepository.save(check));
    }

    @Override
    @Transactional
    public InventoryCheckResponse completeCheck(Long id) {
        InventoryCheck check = findCheckOrThrow(id);

        if (!CheckStatus.IN_PROGRESS.name().equals(check.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION,
                    "Chỉ có thể hoàn tất phiếu kiểm kê đang ở trạng thái IN_PROGRESS.");
        }

        check.setStatus(CheckStatus.COMPLETED.name());
        check.setUpdatedBy(getCurrentUser());
        InventoryCheck saved = checkRepository.save(check);

        // Điều chỉnh tồn kho theo chênh lệch
        List<InventoryCheckDetail> details = detailRepository.findByCheckIdAndIsDeletedFalse(id);
        for (InventoryCheckDetail detail : details) {
            BigDecimal diff = detail.getDiffQty();
            if (diff == null || diff.compareTo(BigDecimal.ZERO) == 0) {
                continue; // Không có chênh lệch → bỏ qua
            }

            String transactionType = diff.compareTo(BigDecimal.ZERO) > 0
                    ? "STOCK_ADJUSTMENT_PLUS"   // Thực tế nhiều hơn → cộng kho
                    : "STOCK_ADJUSTMENT_MINUS"; // Thực tế ít hơn  → trừ kho

            inventoryService.updateStock(
                    saved.getBranch().getId(),
                    detail.getProduct().getId(),
                    diff,
                    transactionType,
                    saved.getId(),
                    null
            );
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public InventoryCheckResponse cancelCheck(Long id) {
        InventoryCheck check = findCheckOrThrow(id);

        if (CheckStatus.COMPLETED.name().equals(check.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION,
                    "Không thể hủy phiếu kiểm kê đã hoàn tất (đã điều chỉnh tồn kho).");
        }

        check.setIsDeleted(true);
        check.setDeletedAt(LocalDateTime.now());
        check.setDeletedBy(getCurrentUser());
        checkRepository.save(check);

        // Soft-delete detail
        List<InventoryCheckDetail> details = detailRepository.findByCheckIdAndIsDeletedFalse(id);
        for (InventoryCheckDetail d : details) {
            d.setIsDeleted(true);
            d.setDeletedAt(LocalDateTime.now());
            d.setDeletedBy(getCurrentUser());
            detailRepository.save(d);
        }

        return mapToResponse(check);
    }

    // ─────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────

    /**
     * Tạo và lưu các dòng chi tiết kiểm kê.
     * Tự động lấy systemQty từ bảng Inventory (tồn kho hiện tại tại chi nhánh).
     * Tính diffQty = actualQty - systemQty.
     */
    private void buildAndSaveDetails(List<CheckLineRequest> lines, InventoryCheck check, Long branchId) {
        for (CheckLineRequest line : lines) {
            Product product = productRepository.findByIdAndIsDeletedFalse(line.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm", "id", line.getProductId()));

            // Lấy systemQty từ tồn kho thực tế (hoặc 0 nếu chưa có record)
            BigDecimal systemQty = inventoryRepository
                    .findByBranchIdAndProductIdAndIsDeletedFalse(branchId, product.getId())
                    .map(Inventory::getQuantity)
                    .orElse(BigDecimal.ZERO);

            BigDecimal actualQty = BigDecimal.valueOf(line.getActualQuantity());
            BigDecimal diffQty = actualQty.subtract(systemQty);

            InventoryCheckDetail detail = InventoryCheckDetail.builder()
                    .check(check)
                    .product(product)
                    .systemQty(systemQty)
                    .actualQty(actualQty)
                    .diffQty(diffQty)
                    .reason(line.getReason())
                    .build();

            detail.setCreatedBy(getCurrentUser());
            detailRepository.save(detail);
        }
    }

    private InventoryCheck findCheckOrThrow(Long id) {
        return checkRepository.findById(id)
                .filter(c -> !Boolean.TRUE.equals(c.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Không tìm thấy phiếu kiểm kê", "id", id));
    }

    private InventoryCheckResponse mapToResponse(InventoryCheck check) {
        List<InventoryCheckDetail> details = detailRepository.findByCheckIdAndIsDeletedFalse(check.getId());

        List<CheckLineResponse> lines = details.stream().map(d -> {
            Unit unit = d.getProduct().getBaseUnit();
            return CheckLineResponse.builder()
                    .id(d.getId())
                    .productId(d.getProduct().getId())
                    .productCode(d.getProduct().getProductCode())
                    .productName(d.getProduct().getName())
                    .productUnitId(unit != null ? unit.getId() : null)
                    .unitName(unit != null ? unit.getUnitName() : null)
                    .expectedQuantity(d.getSystemQty() != null ? d.getSystemQty().intValue() : 0)
                    .actualQuantity(d.getActualQty() != null ? d.getActualQty().intValue() : 0)
                    .discrepancy(d.getDiffQty() != null ? d.getDiffQty().intValue() : 0)
                    .reason(d.getReason())
                    .build();
        }).collect(Collectors.toList());

        return InventoryCheckResponse.builder()
                .id(check.getId())
                .checkCode(check.getCheckCode())
                .checkDate(check.getCheckDate())
                .branchId(check.getBranch().getId())
                .branchName(check.getBranch().getBranchName())
                .status(CheckStatus.valueOf(check.getStatus()))
                .checkLines(lines)
                .createdBy(check.getCreatedBy())
                .createdAt(check.getCreatedAt())
                .updatedAt(check.getUpdatedAt())
                .build();
    }
}
