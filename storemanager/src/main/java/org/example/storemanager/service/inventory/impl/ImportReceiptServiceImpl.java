package org.example.storemanager.service.inventory.impl;

import org.example.storemanager.dto.request.inventory.ImportReceiptRequest;
import org.example.storemanager.dto.request.inventory.ReceiptLineRequest;
import org.example.storemanager.dto.response.inventory.ImportReceiptResponse;
import org.example.storemanager.dto.response.inventory.ReceiptLineResponse;
import org.example.storemanager.entity.inventory.ImportReceipt;
import org.example.storemanager.entity.inventory.ImportReceiptDetail;
import org.example.storemanager.entity.inventory.ProductBatch;
import org.example.storemanager.entity.system.Branch;
import org.example.storemanager.entity.partnerarea.Supplier;
import org.example.storemanager.entity.catalog.Product;
import org.example.storemanager.entity.catalog.Unit;
import org.example.storemanager.enums.ErrorCode;
import org.example.storemanager.enums.inventory.ReceiptStatus;
import org.example.storemanager.exception.BusinessException;
import org.example.storemanager.exception.ResourceNotFoundException;
import org.example.storemanager.repository.inventory.ImportReceiptRepository;
import org.example.storemanager.repository.inventory.ImportReceiptDetailRepository;
import org.example.storemanager.repository.inventory.ProductBatchRepository;
import org.example.storemanager.repository.system.BranchRepository;
import org.example.storemanager.repository.partnerarea.SupplierRepository;
import org.example.storemanager.repository.catalog.ProductRepository;
import org.example.storemanager.repository.catalog.UnitRepository;
import org.example.storemanager.repository.sales.PurchaseOrderRepository;
import org.example.storemanager.service.inventory.ImportReceiptService;
import org.example.storemanager.service.inventory.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImportReceiptServiceImpl implements ImportReceiptService {

    private final ImportReceiptRepository receiptRepository;
    private final ImportReceiptDetailRepository detailRepository;
    private final BranchRepository branchRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final UnitRepository unitRepository;
    private final PurchaseOrderRepository poRepository;
    private final ProductBatchRepository batchRepository;
    private final InventoryService inventoryService;

    @Autowired
    public ImportReceiptServiceImpl(ImportReceiptRepository receiptRepository,
                                    ImportReceiptDetailRepository detailRepository,
                                    BranchRepository branchRepository,
                                    SupplierRepository supplierRepository,
                                    ProductRepository productRepository,
                                    UnitRepository unitRepository,
                                    PurchaseOrderRepository poRepository,
                                    ProductBatchRepository batchRepository,
                                    InventoryService inventoryService) {
        this.receiptRepository = receiptRepository;
        this.detailRepository = detailRepository;
        this.branchRepository = branchRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.unitRepository = unitRepository;
        this.poRepository = poRepository;
        this.batchRepository = batchRepository;
        this.inventoryService = inventoryService;
    }

    private String getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        }
        return "system";
    }

    @Override
    public List<ImportReceiptResponse> getAllReceipts() {
        return receiptRepository.findByIsDeletedFalse().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<ImportReceiptResponse> getReceiptsByBranch(Long branchId) {
        return receiptRepository.findByBranchIdAndIsDeletedFalse(branchId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public ImportReceiptResponse getReceiptById(Long id) {
        ImportReceipt receipt = receiptRepository.findById(id)
                .filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy phiếu nhập kho", "id", id));
        return mapToResponse(receipt);
    }

    @Override
    @Transactional
    public ImportReceiptResponse createReceipt(ImportReceiptRequest request) {
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRANCH_NOT_FOUND, "Chi nhánh", "id", request.getBranchId()));

        Supplier supplier = null;
        if (request.getSupplierId() != null) {
            supplier = supplierRepository.findByIdAndIsDeletedFalse(request.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SUPPLIER_NOT_FOUND, "Nhà cung cấp", "id", request.getSupplierId()));
        }

        org.example.storemanager.entity.sales.PurchaseOrder po = null;
        if (request.getPurchaseOrderId() != null) {
            po = poRepository.findByIdAndIsDeletedFalse(request.getPurchaseOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ORDER_NOT_FOUND, "Đơn mua hàng", "id", request.getPurchaseOrderId()));
        }

        ImportReceipt receipt = ImportReceipt.builder()
                .receiptCode(request.getReceiptCode())
                .receiptDate(request.getReceiptDate() != null ? request.getReceiptDate() : LocalDateTime.now())
                .totalAmount(request.getTotalAmount())
                .discount(request.getDiscount())
                .tax(request.getTax())
                .status(request.getStatus() != null ? request.getStatus().name() : ReceiptStatus.PENDING.name())
                .branch(branch)
                .supplier(supplier)
                .purchaseOrder(po)
                .build();

        receipt.setCreatedBy(getCurrentUser());
        ImportReceipt savedReceipt = receiptRepository.save(receipt);

        List<ImportReceiptDetail> details = new ArrayList<>();
        BigDecimal totalValuation = BigDecimal.ZERO;

        for (ReceiptLineRequest line : request.getReceiptLines()) {
            Product product = productRepository.findByIdAndIsDeletedFalse(line.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm", "id", line.getProductId()));

            Unit unit = null;
            if (line.getProductUnitId() != null) {
                unit = unitRepository.findByIdAndIsDeletedFalse(line.getProductUnitId()).orElse(null);
            }

            LocalDate expiry = null;
            if (line.getExpiryDate() != null && !line.getExpiryDate().trim().isEmpty()) {
                try {
                    expiry = LocalDate.parse(line.getExpiryDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (Exception e) {
                    // Ignored or parsed with default format
                }
            }

            BigDecimal qty = BigDecimal.valueOf(line.getQuantity());
            BigDecimal lineTotal = line.getLineTotal() != null ? line.getLineTotal() : qty.multiply(line.getUnitCost());
            totalValuation = totalValuation.add(lineTotal);

            ImportReceiptDetail detail = ImportReceiptDetail.builder()
                    .receipt(savedReceipt)
                    .product(product)
                    .unit(unit)
                    .quantity(qty)
                    .unitPrice(line.getUnitCost())
                    .subTotal(lineTotal)
                    .batchNumber(line.getBatchNumber())
                    .expiryDate(expiry)
                    .build();

            detail.setCreatedBy(getCurrentUser());
            details.add(detailRepository.save(detail));
        }

        // Cập nhật tổng giá trị nếu chưa truyền
        if (savedReceipt.getTotalAmount() == null || savedReceipt.getTotalAmount().compareTo(BigDecimal.ZERO) == 0) {
            savedReceipt.setTotalAmount(totalValuation);
            receiptRepository.save(savedReceipt);
        }

        // Tự động cộng tồn kho nếu trạng thái truyền vào là COMPLETED
        if (ReceiptStatus.COMPLETED.name().equals(savedReceipt.getStatus())) {
            processInventoryAndBatch(savedReceipt, details);
        }

        return mapToResponse(savedReceipt);
    }

    @Override
    @Transactional
    public ImportReceiptResponse updateReceipt(Long id, ImportReceiptRequest request) {
        ImportReceipt receipt = receiptRepository.findById(id)
                .filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy phiếu nhập kho", "id", id));

        // Không cho sửa phiếu đã hoàn tất hoặc đã hủy
        if (ReceiptStatus.COMPLETED.name().equals(receipt.getStatus()) || ReceiptStatus.CANCELLED.name().equals(receipt.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Không thể sửa phiếu nhập kho đã hoàn tất hoặc đã hủy.");
        }

        // Lưu trạng thái cũ để kiểm tra sau khi update
        String previousStatus = receipt.getStatus();

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRANCH_NOT_FOUND, "Chi nhánh", "id", request.getBranchId()));

        Supplier supplier = null;
        if (request.getSupplierId() != null) {
            supplier = supplierRepository.findByIdAndIsDeletedFalse(request.getSupplierId()).orElse(null);
        }

        receipt.setReceiptCode(request.getReceiptCode());
        receipt.setReceiptDate(request.getReceiptDate());
        receipt.setTotalAmount(request.getTotalAmount());
        receipt.setDiscount(request.getDiscount());
        receipt.setTax(request.getTax());
        receipt.setBranch(branch);
        receipt.setSupplier(supplier);
        receipt.setUpdatedBy(getCurrentUser());

        ImportReceipt saved = receiptRepository.save(receipt);

        // Xóa các dòng cũ và lưu các dòng mới
        List<ImportReceiptDetail> oldDetails = detailRepository.findByReceiptIdAndIsDeletedFalse(id);
        for (ImportReceiptDetail d : oldDetails) {
            d.setIsDeleted(true);
            d.setDeletedBy(getCurrentUser());
            d.setDeletedAt(LocalDateTime.now());
            detailRepository.save(d);
        }

        List<ImportReceiptDetail> details = new ArrayList<>();
        BigDecimal totalValuation = BigDecimal.ZERO;

        for (ReceiptLineRequest line : request.getReceiptLines()) {
            Product product = productRepository.findByIdAndIsDeletedFalse(line.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm", "id", line.getProductId()));

            Unit unit = null;
            if (line.getProductUnitId() != null) {
                unit = unitRepository.findByIdAndIsDeletedFalse(line.getProductUnitId()).orElse(null);
            }

            LocalDate expiry = null;
            if (line.getExpiryDate() != null && !line.getExpiryDate().trim().isEmpty()) {
                try {
                    expiry = LocalDate.parse(line.getExpiryDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (Exception e) {
                    // Ignored
                }
            }

            BigDecimal qty = BigDecimal.valueOf(line.getQuantity());
            BigDecimal lineTotal = line.getLineTotal() != null ? line.getLineTotal() : qty.multiply(line.getUnitCost());
            totalValuation = totalValuation.add(lineTotal);

            ImportReceiptDetail detail = ImportReceiptDetail.builder()
                    .receipt(saved)
                    .product(product)
                    .unit(unit)
                    .quantity(qty)
                    .unitPrice(line.getUnitCost())
                    .subTotal(lineTotal)
                    .batchNumber(line.getBatchNumber())
                    .expiryDate(expiry)
                    .build();

            detail.setCreatedBy(getCurrentUser());
            details.add(detailRepository.save(detail));
        }

        saved.setTotalAmount(totalValuation);

        // Chỉ cộng kho khi status chuyển từ chưa-COMPLETED sang COMPLETED
        // Guard: phiếu đã COMPLETED bị block ở trên nên previousStatus không bao giờ là COMPLETED ở đây
        // Nhưng vẫn giữ check rõ ràng để tránh edge case
        if (request.getStatus() != null && ReceiptStatus.COMPLETED.name().equals(request.getStatus().name())
                && !ReceiptStatus.COMPLETED.name().equals(previousStatus)) {
            saved.setStatus(ReceiptStatus.COMPLETED.name());
            saved = receiptRepository.save(saved);
            processInventoryAndBatch(saved, details);
        } else {
            saved = receiptRepository.save(saved);
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ImportReceiptResponse completeReceipt(Long id) {
        ImportReceipt receipt = receiptRepository.findById(id)
                .filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy phiếu nhập kho", "id", id));

        if (ReceiptStatus.COMPLETED.name().equals(receipt.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Phiếu nhập kho này đã hoàn tất rồi.");
        }
        if (ReceiptStatus.CANCELLED.name().equals(receipt.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Không thể hoàn tất phiếu nhập kho đã hủy.");
        }

        receipt.setStatus(ReceiptStatus.COMPLETED.name());
        receipt.setUpdatedBy(getCurrentUser());
        ImportReceipt saved = receiptRepository.save(receipt);

        List<ImportReceiptDetail> details = detailRepository.findByReceiptIdAndIsDeletedFalse(id);
        processInventoryAndBatch(saved, details);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ImportReceiptResponse cancelReceipt(Long id) {
        ImportReceipt receipt = receiptRepository.findById(id)
                .filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy phiếu nhập kho", "id", id));

        if (ReceiptStatus.COMPLETED.name().equals(receipt.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Không thể hủy phiếu nhập kho đã hoàn tất.");
        }

        receipt.setStatus(ReceiptStatus.CANCELLED.name());
        receipt.setUpdatedBy(getCurrentUser());
        ImportReceipt saved = receiptRepository.save(receipt);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteReceipt(Long id) {
        ImportReceipt receipt = receiptRepository.findById(id)
                .filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy phiếu nhập kho", "id", id));

        if (ReceiptStatus.COMPLETED.name().equals(receipt.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Không thể xóa phiếu nhập kho đã hoàn tất.");
        }

        receipt.setIsDeleted(true);
        receipt.setDeletedAt(LocalDateTime.now());
        receipt.setDeletedBy(getCurrentUser());
        receiptRepository.save(receipt);
    }

    private void processInventoryAndBatch(ImportReceipt receipt, List<ImportReceiptDetail> details) {
        for (ImportReceiptDetail detail : details) {
            Long batchId = null;

            // Xử lý lô sản phẩm (ProductBatch)
            if (detail.getBatchNumber() != null && !detail.getBatchNumber().trim().isEmpty()) {
                ProductBatch batch = batchRepository.findByBatchNumberAndProductIdAndIsDeletedFalse(
                        detail.getBatchNumber().trim(), detail.getProduct().getId()).orElse(null);

                if (batch == null) {
                    batch = ProductBatch.builder()
                            .batchNumber(detail.getBatchNumber().trim())
                            .manufactureDate(detail.getExpiryDate() != null ? detail.getExpiryDate().minusYears(2) : LocalDate.now())
                            .expiryDate(detail.getExpiryDate())
                            .initialUnits(detail.getQuantity())
                            .remainingUnits(detail.getQuantity())
                            .unitCost(detail.getUnitPrice())
                            .supplierName(receipt.getSupplier() != null ? receipt.getSupplier().getName() : null)
                            .qualityStatus("PASSED_QA")
                            .status("ACTIVE")
                            .product(detail.getProduct())
                            .build();
                    batch.setCreatedBy(getCurrentUser());
                } else {
                    BigDecimal remaining = batch.getRemainingUnits() != null ? batch.getRemainingUnits() : BigDecimal.ZERO;
                    batch.setRemainingUnits(remaining.add(detail.getQuantity()));
                    batch.setUpdatedBy(getCurrentUser());
                }

                ProductBatch savedBatch = batchRepository.save(batch);
                batchId = savedBatch.getId();
            }

            // Cập nhật tồn kho
            inventoryService.updateStock(receipt.getBranch().getId(), detail.getProduct().getId(),
                    detail.getQuantity(), "IMPORT", receipt.getId(), batchId);
        }
    }

    private ImportReceiptResponse mapToResponse(ImportReceipt receipt) {
        List<ImportReceiptDetail> details = detailRepository.findByReceiptIdAndIsDeletedFalse(receipt.getId());
        List<ReceiptLineResponse> lines = details.stream().map(d -> ReceiptLineResponse.builder()
                .id(d.getId())
                .productId(d.getProduct().getId())
                .productCode(d.getProduct().getProductCode())
                .productName(d.getProduct().getName())
                .productUnitId(d.getUnit() != null ? d.getUnit().getId() : null)
                .unitName(d.getUnit() != null ? d.getUnit().getUnitName() : null)
                .quantity(d.getQuantity() != null ? d.getQuantity().intValue() : 0)
                .unitCost(d.getUnitPrice())
                .lineTotal(d.getSubTotal())
                .batchNumber(d.getBatchNumber())
                .expiryDate(d.getExpiryDate() != null ? d.getExpiryDate().toString() : null)
                .build()).collect(Collectors.toList());

        return ImportReceiptResponse.builder()
                .id(receipt.getId())
                .receiptCode(receipt.getReceiptCode())
                .receiptDate(receipt.getReceiptDate())
                .supplierId(receipt.getSupplier() != null ? receipt.getSupplier().getId() : null)
                .supplierName(receipt.getSupplier() != null ? receipt.getSupplier().getName() : null)
                .branchId(receipt.getBranch().getId())
                .branchName(receipt.getBranch().getBranchName())
                .purchaseOrderId(receipt.getPurchaseOrder() != null ? receipt.getPurchaseOrder().getId() : null)
                .purchaseOrderCode(receipt.getPurchaseOrder() != null ? receipt.getPurchaseOrder().getPoCode() : null)
                .totalAmount(receipt.getTotalAmount())
                .discount(receipt.getDiscount())
                .tax(receipt.getTax())
                .status(ReceiptStatus.valueOf(receipt.getStatus()))
                .receiptLines(lines)
                .createdBy(receipt.getCreatedBy())
                .createdAt(receipt.getCreatedAt())
                .updatedAt(receipt.getUpdatedAt())
                .build();
    }
}
