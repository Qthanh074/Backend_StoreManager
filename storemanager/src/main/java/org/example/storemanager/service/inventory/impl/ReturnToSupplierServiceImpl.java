package org.example.storemanager.service.inventory.impl;

import org.example.storemanager.dto.request.inventory.ReturnToSupplierRequest;
import org.example.storemanager.dto.request.inventory.ReturnLineRequest;
import org.example.storemanager.dto.response.inventory.ReturnToSupplierResponse;
import org.example.storemanager.dto.response.inventory.ReturnLineResponse;
import org.example.storemanager.entity.inventory.ReturnToSupplier;
import org.example.storemanager.entity.inventory.ReturnToSupplierDetail;
import org.example.storemanager.entity.system.Branch;
import org.example.storemanager.entity.partnerarea.Supplier;
import org.example.storemanager.entity.catalog.Product;
import org.example.storemanager.enums.ErrorCode;
import org.example.storemanager.enums.inventory.ReturnToSupplierStatus;
import org.example.storemanager.exception.BusinessException;
import org.example.storemanager.exception.ResourceNotFoundException;
import org.example.storemanager.repository.inventory.ReturnToSupplierRepository;
import org.example.storemanager.repository.inventory.ReturnToSupplierDetailRepository;
import org.example.storemanager.repository.system.BranchRepository;
import org.example.storemanager.repository.partnerarea.SupplierRepository;
import org.example.storemanager.repository.catalog.ProductRepository;
import org.example.storemanager.service.inventory.ReturnToSupplierService;
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
public class ReturnToSupplierServiceImpl implements ReturnToSupplierService {

    private final ReturnToSupplierRepository returnRepository;
    private final ReturnToSupplierDetailRepository detailRepository;
    private final BranchRepository branchRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    @Autowired
    public ReturnToSupplierServiceImpl(ReturnToSupplierRepository returnRepository,
                                       ReturnToSupplierDetailRepository detailRepository,
                                       BranchRepository branchRepository,
                                       SupplierRepository supplierRepository,
                                       ProductRepository productRepository,
                                       InventoryService inventoryService) {
        this.returnRepository = returnRepository;
        this.detailRepository = detailRepository;
        this.branchRepository = branchRepository;
        this.supplierRepository = supplierRepository;
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
    private boolean isStockDeductedStatus(ReturnToSupplierStatus status) {
        return ReturnToSupplierStatus.APPROVED_CREDIT_NOTE.equals(status)
                || ReturnToSupplierStatus.COMPLETED.equals(status);
    }

    @Override
    public List<ReturnToSupplierResponse> getAllReturns() {
        return returnRepository.findByIsDeletedFalse().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<ReturnToSupplierResponse> getReturnsByBranch(Long branchId) {
        return returnRepository.findByBranchIdAndIsDeletedFalse(branchId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public ReturnToSupplierResponse getReturnById(Long id) {
        ReturnToSupplier rtv = returnRepository.findById(id)
                .filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy chứng từ trả hàng nhà cung cấp", "id", id));
        return mapToResponse(rtv);
    }

    @Override
    @Transactional
    public ReturnToSupplierResponse createReturn(ReturnToSupplierRequest request) {
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRANCH_NOT_FOUND, "Chi nhánh", "id", request.getBranchId()));
        Supplier supplier = supplierRepository.findByIdAndIsDeletedFalse(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SUPPLIER_NOT_FOUND, "Nhà cung cấp", "id", request.getSupplierId()));

        ReturnToSupplierStatus initialStatus = request.getStatus() != null
                ? request.getStatus() : ReturnToSupplierStatus.PENDING_SUPPLIER_APPROVAL;

        ReturnToSupplier rtv = ReturnToSupplier.builder()
                .returnCode(request.getReturnCode())
                .returnDate(request.getReturnDate() != null ? request.getReturnDate() : LocalDateTime.now())
                .grnRefNumber(request.getGrnRefNumber())
                .totalAmount(request.getTotalAmount())
                .status(initialStatus)
                .reason(request.getReason())
                .branch(branch)
                .supplier(supplier)
                .build();

        rtv.setCreatedBy(getCurrentUser());
        rtv.setNote(request.getNote());
        ReturnToSupplier savedRtv = returnRepository.save(rtv);

        List<ReturnToSupplierDetail> details = buildAndSaveDetails(request.getReturnLines(), savedRtv);

        BigDecimal totalValuation = details.stream()
                .map(ReturnToSupplierDetail::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (savedRtv.getTotalAmount() == null || savedRtv.getTotalAmount().compareTo(BigDecimal.ZERO) == 0) {
            savedRtv.setTotalAmount(totalValuation);
            returnRepository.save(savedRtv);
        }

        // Trừ kho ngay nếu tạo thẳng với trạng thái kích hoạt
        if (isStockDeductedStatus(savedRtv.getStatus())) {
            deductInventory(savedRtv, details);
        }

        return mapToResponse(savedRtv);
    }

    @Override
    @Transactional
    public ReturnToSupplierResponse updateReturn(Long id, ReturnToSupplierRequest request) {
        ReturnToSupplier rtv = returnRepository.findById(id)
                .filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy chứng từ trả hàng nhà cung cấp", "id", id));

        if (isStockDeductedStatus(rtv.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Không thể chỉnh sửa phiếu trả hàng đã hoàn thành hoặc đã phê duyệt.");
        }

        ReturnToSupplierStatus previousStatus = rtv.getStatus();

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRANCH_NOT_FOUND, "Chi nhánh", "id", request.getBranchId()));
        Supplier supplier = supplierRepository.findByIdAndIsDeletedFalse(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SUPPLIER_NOT_FOUND, "Nhà cung cấp", "id", request.getSupplierId()));

        rtv.setReturnCode(request.getReturnCode());
        rtv.setReturnDate(request.getReturnDate());
        rtv.setGrnRefNumber(request.getGrnRefNumber());
        rtv.setReason(request.getReason());
        rtv.setBranch(branch);
        rtv.setSupplier(supplier);
        rtv.setNote(request.getNote());
        rtv.setUpdatedBy(getCurrentUser());

        // Xóa các dòng cũ
        List<ReturnToSupplierDetail> oldDetails = detailRepository.findByReturnReceiptIdAndIsDeletedFalse(id);
        for (ReturnToSupplierDetail d : oldDetails) {
            d.setIsDeleted(true);
            d.setDeletedAt(LocalDateTime.now());
            d.setDeletedBy(getCurrentUser());
            detailRepository.save(d);
        }

        List<ReturnToSupplierDetail> details = buildAndSaveDetails(request.getReturnLines(), rtv);

        BigDecimal totalValuation = details.stream()
                .map(ReturnToSupplierDetail::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        rtv.setTotalAmount(totalValuation);

        if (request.getStatus() != null) {
            rtv.setStatus(request.getStatus());
        }

        ReturnToSupplier saved = returnRepository.save(rtv);

        // Chỉ trừ kho nếu trạng thái cũ chưa kích hoạt và trạng thái mới đã kích hoạt
        if (!isStockDeductedStatus(previousStatus) && isStockDeductedStatus(saved.getStatus())) {
            deductInventory(saved, details);
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ReturnToSupplierResponse approveReturn(Long id) {
        ReturnToSupplier rtv = returnRepository.findById(id)
                .filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy chứng từ trả hàng nhà cung cấp", "id", id));

        if (isStockDeductedStatus(rtv.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Phiếu này đã được phê duyệt.");
        }
        if (ReturnToSupplierStatus.REJECTED.equals(rtv.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Không thể phê duyệt phiếu đã bị từ chối.");
        }

        rtv.setStatus(ReturnToSupplierStatus.APPROVED_CREDIT_NOTE);
        rtv.setUpdatedBy(getCurrentUser());
        ReturnToSupplier saved = returnRepository.save(rtv);

        List<ReturnToSupplierDetail> details = detailRepository.findByReturnReceiptIdAndIsDeletedFalse(id);
        deductInventory(saved, details);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ReturnToSupplierResponse rejectReturn(Long id) {
        ReturnToSupplier rtv = returnRepository.findById(id)
                .filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy chứng từ trả hàng nhà cung cấp", "id", id));

        if (isStockDeductedStatus(rtv.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Không thể từ chối phiếu trả hàng đã được phê duyệt hoặc hoàn tất.");
        }

        rtv.setStatus(ReturnToSupplierStatus.REJECTED);
        rtv.setUpdatedBy(getCurrentUser());
        ReturnToSupplier saved = returnRepository.save(rtv);
        return mapToResponse(saved);
    }

    // ─────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────

    private List<ReturnToSupplierDetail> buildAndSaveDetails(List<ReturnLineRequest> lines, ReturnToSupplier rtv) {
        List<ReturnToSupplierDetail> details = new ArrayList<>();
        for (ReturnLineRequest line : lines) {
            Product product = productRepository.findByIdAndIsDeletedFalse(line.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm", "id", line.getProductId()));

            BigDecimal qty = BigDecimal.valueOf(line.getQuantity());
            BigDecimal subTotal = qty.multiply(line.getUnitPrice());

            ReturnToSupplierDetail detail = ReturnToSupplierDetail.builder()
                    .returnReceipt(rtv)
                    .product(product)
                    .quantity(qty)
                    .unitPrice(line.getUnitPrice())
                    .subTotal(subTotal)
                    .build();

            detail.setCreatedBy(getCurrentUser());
            details.add(detailRepository.save(detail));
        }
        return details;
    }

    private void deductInventory(ReturnToSupplier rtv, List<ReturnToSupplierDetail> details) {
        for (ReturnToSupplierDetail detail : details) {
            inventoryService.updateStock(rtv.getBranch().getId(), detail.getProduct().getId(),
                    detail.getQuantity().negate(), "RETURN_TO_SUPPLIER", rtv.getId(), null);
        }
    }

    private ReturnToSupplierResponse mapToResponse(ReturnToSupplier rtv) {
        List<ReturnToSupplierDetail> details = detailRepository.findByReturnReceiptIdAndIsDeletedFalse(rtv.getId());
        List<ReturnLineResponse> lines = details.stream().map(d -> ReturnLineResponse.builder()
                .id(d.getId())
                .productId(d.getProduct().getId())
                .productCode(d.getProduct().getProductCode())
                .productName(d.getProduct().getName())
                .quantity(d.getQuantity() != null ? d.getQuantity().intValue() : 0)
                .unitPrice(d.getUnitPrice())
                .subTotal(d.getSubTotal())
                .build()).collect(Collectors.toList());

        return ReturnToSupplierResponse.builder()
                .id(rtv.getId())
                .returnCode(rtv.getReturnCode())
                .returnDate(rtv.getReturnDate())
                .grnRefNumber(rtv.getGrnRefNumber())
                .totalAmount(rtv.getTotalAmount())
                .status(rtv.getStatus())
                .reason(rtv.getReason())
                .supplierId(rtv.getSupplier().getId())
                .supplierName(rtv.getSupplier().getName())
                .branchId(rtv.getBranch().getId())
                .branchName(rtv.getBranch().getBranchName())
                .returnLines(lines)
                .createdBy(rtv.getCreatedBy())
                .createdAt(rtv.getCreatedAt())
                .updatedAt(rtv.getUpdatedAt())
                .note(rtv.getNote())
                .build();
    }
}
