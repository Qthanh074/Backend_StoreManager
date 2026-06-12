package org.example.storemanager.service.inventory.impl;

import org.example.storemanager.dto.request.inventory.CancelIssueRequest;
import org.example.storemanager.dto.request.inventory.CancelLineRequest;
import org.example.storemanager.dto.response.inventory.CancelIssueResponse;
import org.example.storemanager.dto.response.inventory.CancelLineResponse;
import org.example.storemanager.entity.inventory.CancelIssue;
import org.example.storemanager.entity.inventory.CancelIssueDetail;
import org.example.storemanager.entity.system.Branch;
import org.example.storemanager.entity.catalog.Product;
import org.example.storemanager.enums.ErrorCode;
import org.example.storemanager.enums.inventory.CancelIssueStatus;
import org.example.storemanager.exception.BusinessException;
import org.example.storemanager.exception.ResourceNotFoundException;
import org.example.storemanager.repository.inventory.CancelIssueRepository;
import org.example.storemanager.repository.inventory.CancelIssueDetailRepository;
import org.example.storemanager.repository.system.BranchRepository;
import org.example.storemanager.repository.catalog.ProductRepository;
import org.example.storemanager.service.inventory.CancelIssueService;
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
public class CancelIssueServiceImpl implements CancelIssueService {

    private final CancelIssueRepository cancelRepository;
    private final CancelIssueDetailRepository detailRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    @Autowired
    public CancelIssueServiceImpl(CancelIssueRepository cancelRepository,
                                  CancelIssueDetailRepository detailRepository,
                                  BranchRepository branchRepository,
                                  ProductRepository productRepository,
                                  InventoryService inventoryService) {
        this.cancelRepository = cancelRepository;
        this.detailRepository = detailRepository;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
    }

    private String getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        }
        return "system";
    }

    /** Trạng thái kích hoạt trừ tồn kho */
    private boolean isStockDeductedStatus(CancelIssueStatus status) {
        return CancelIssueStatus.APPROVED.equals(status) || CancelIssueStatus.PROCESSED.equals(status);
    }

    @Override
    public List<CancelIssueResponse> getAllCancelIssues() {
        return cancelRepository.findByIsDeletedFalse().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<CancelIssueResponse> getCancelIssuesByBranch(Long branchId) {
        return cancelRepository.findByBranchIdAndIsDeletedFalse(branchId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public CancelIssueResponse getCancelIssueById(Long id) {
        CancelIssue cancel = cancelRepository.findById(id)
                .filter(c -> !Boolean.TRUE.equals(c.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy phiếu xuất hủy", "id", id));
        return mapToResponse(cancel);
    }

    @Override
    @Transactional
    public CancelIssueResponse createCancelIssue(CancelIssueRequest request) {
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRANCH_NOT_FOUND, "Chi nhánh", "id", request.getBranchId()));

        CancelIssueStatus initialStatus = request.getStatus() != null ? request.getStatus() : CancelIssueStatus.PENDING_APPROVAL;

        CancelIssue cancel = CancelIssue.builder()
                .cancelCode(request.getCancelCode())
                .cancelDate(request.getCancelDate() != null ? request.getCancelDate() : LocalDateTime.now())
                .totalValue(request.getTotalValue())
                .reason(request.getReason())
                .status(initialStatus)
                .branch(branch)
                .build();

        cancel.setCreatedBy(getCurrentUser());
        cancel.setNote(request.getNote());
        CancelIssue savedCancel = cancelRepository.save(cancel);

        List<CancelIssueDetail> details = buildAndSaveDetails(request.getCancelLines(), savedCancel);

        BigDecimal totalValuation = details.stream()
                .map(CancelIssueDetail::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (savedCancel.getTotalValue() == null || savedCancel.getTotalValue().compareTo(BigDecimal.ZERO) == 0) {
            savedCancel.setTotalValue(totalValuation);
            cancelRepository.save(savedCancel);
        }

        // Trừ kho ngay nếu tạo thẳng với trạng thái kích hoạt
        if (isStockDeductedStatus(savedCancel.getStatus())) {
            deductInventory(savedCancel, details);
        }

        return mapToResponse(savedCancel);
    }

    @Override
    @Transactional
    public CancelIssueResponse updateCancelIssue(Long id, CancelIssueRequest request) {
        CancelIssue cancel = cancelRepository.findById(id)
                .filter(c -> !Boolean.TRUE.equals(c.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy phiếu xuất hủy", "id", id));

        // Không cho sửa phiếu đã phê duyệt/xử lý
        if (isStockDeductedStatus(cancel.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Không thể sửa phiếu xuất hủy đã phê duyệt hoặc đã xử lý.");
        }

        CancelIssueStatus previousStatus = cancel.getStatus();

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRANCH_NOT_FOUND, "Chi nhánh", "id", request.getBranchId()));

        cancel.setCancelCode(request.getCancelCode());
        cancel.setCancelDate(request.getCancelDate());
        cancel.setReason(request.getReason());
        cancel.setBranch(branch);
        cancel.setNote(request.getNote());
        cancel.setUpdatedBy(getCurrentUser());

        // Xóa dòng cũ
        List<CancelIssueDetail> oldDetails = detailRepository.findByCancelIssueIdAndIsDeletedFalse(id);
        for (CancelIssueDetail d : oldDetails) {
            d.setIsDeleted(true);
            d.setDeletedAt(LocalDateTime.now());
            d.setDeletedBy(getCurrentUser());
            detailRepository.save(d);
        }

        List<CancelIssueDetail> details = buildAndSaveDetails(request.getCancelLines(), cancel);

        BigDecimal totalValuation = details.stream()
                .map(CancelIssueDetail::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cancel.setTotalValue(totalValuation);

        if (request.getStatus() != null) {
            cancel.setStatus(request.getStatus());
        }

        CancelIssue saved = cancelRepository.save(cancel);

        // Chỉ trừ kho nếu trạng thái cũ chưa kích hoạt và trạng thái mới đã kích hoạt
        if (!isStockDeductedStatus(previousStatus) && isStockDeductedStatus(saved.getStatus())) {
            deductInventory(saved, details);
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CancelIssueResponse approveCancelIssue(Long id) {
        CancelIssue cancel = cancelRepository.findById(id)
                .filter(c -> !Boolean.TRUE.equals(c.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy phiếu xuất hủy", "id", id));

        if (isStockDeductedStatus(cancel.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Phiếu xuất hủy này đã được phê duyệt từ trước.");
        }
        if (CancelIssueStatus.REJECTED.equals(cancel.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Không thể phê duyệt phiếu đã bị từ chối.");
        }

        cancel.setStatus(CancelIssueStatus.APPROVED);
        cancel.setUpdatedBy(getCurrentUser());
        CancelIssue saved = cancelRepository.save(cancel);

        List<CancelIssueDetail> details = detailRepository.findByCancelIssueIdAndIsDeletedFalse(id);
        deductInventory(saved, details);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CancelIssueResponse rejectCancelIssue(Long id) {
        CancelIssue cancel = cancelRepository.findById(id)
                .filter(c -> !Boolean.TRUE.equals(c.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy phiếu xuất hủy", "id", id));

        if (isStockDeductedStatus(cancel.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Không thể từ chối phiếu xuất hủy đã được phê duyệt.");
        }

        cancel.setStatus(CancelIssueStatus.REJECTED);
        cancel.setUpdatedBy(getCurrentUser());
        CancelIssue saved = cancelRepository.save(cancel);
        return mapToResponse(saved);
    }

    // ─────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────

    private List<CancelIssueDetail> buildAndSaveDetails(List<org.example.storemanager.dto.request.inventory.CancelLineRequest> lines, CancelIssue cancel) {
        List<CancelIssueDetail> details = new ArrayList<>();
        for (CancelLineRequest line : lines) {
            Product product = productRepository.findByIdAndIsDeletedFalse(line.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm", "id", line.getProductId()));

            BigDecimal qty = BigDecimal.valueOf(line.getQuantity());
            BigDecimal unitPrice = line.getUnitPrice() != null ? line.getUnitPrice() : product.getCostPrice();
            if (unitPrice == null) {
                unitPrice = product.getBasePrice() != null ? product.getBasePrice() : BigDecimal.ZERO;
            }
            BigDecimal subTotal = qty.multiply(unitPrice);

            CancelIssueDetail detail = CancelIssueDetail.builder()
                    .cancelIssue(cancel)
                    .product(product)
                    .quantity(qty)
                    .unitPrice(unitPrice)
                    .subTotal(subTotal)
                    .build();

            detail.setCreatedBy(getCurrentUser());
            details.add(detailRepository.save(detail));
        }
        return details;
    }

    private void deductInventory(CancelIssue cancel, List<CancelIssueDetail> details) {
        for (CancelIssueDetail detail : details) {
            inventoryService.updateStock(cancel.getBranch().getId(), detail.getProduct().getId(),
                    detail.getQuantity().negate(), "CANCEL", cancel.getId(), null);
        }
    }

    private CancelIssueResponse mapToResponse(CancelIssue cancel) {
        List<CancelIssueDetail> details = detailRepository.findByCancelIssueIdAndIsDeletedFalse(cancel.getId());
        List<CancelLineResponse> lines = details.stream().map(d -> CancelLineResponse.builder()
                .id(d.getId())
                .productId(d.getProduct().getId())
                .productCode(d.getProduct().getProductCode())
                .productName(d.getProduct().getName())
                .quantity(d.getQuantity() != null ? d.getQuantity().intValue() : 0)
                .unitPrice(d.getUnitPrice())
                .subTotal(d.getSubTotal())
                .build()).collect(Collectors.toList());

        return CancelIssueResponse.builder()
                .id(cancel.getId())
                .cancelCode(cancel.getCancelCode())
                .cancelDate(cancel.getCancelDate())
                .totalValue(cancel.getTotalValue())
                .reason(cancel.getReason())
                .status(cancel.getStatus())
                .branchId(cancel.getBranch().getId())
                .branchName(cancel.getBranch().getBranchName())
                .cancelLines(lines)
                .createdBy(cancel.getCreatedBy())
                .createdAt(cancel.getCreatedAt())
                .updatedAt(cancel.getUpdatedAt())
                .note(cancel.getNote())
                .build();
    }
}
