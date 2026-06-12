package org.example.storemanager.service.inventory.impl;

import org.example.storemanager.dto.request.inventory.StockTransferRequest;
import org.example.storemanager.dto.request.inventory.TransferLineRequest;
import org.example.storemanager.dto.response.inventory.StockTransferResponse;
import org.example.storemanager.dto.response.inventory.TransferLineResponse;
import org.example.storemanager.entity.inventory.StockTransfer;
import org.example.storemanager.entity.inventory.StockTransferDetail;
import org.example.storemanager.entity.system.Branch;
import org.example.storemanager.entity.catalog.Product;
import org.example.storemanager.enums.ErrorCode;
import org.example.storemanager.enums.inventory.TransferStatus;
import org.example.storemanager.exception.BusinessException;
import org.example.storemanager.exception.ResourceNotFoundException;
import org.example.storemanager.repository.inventory.StockTransferRepository;
import org.example.storemanager.repository.inventory.StockTransferDetailRepository;
import org.example.storemanager.repository.system.BranchRepository;
import org.example.storemanager.repository.catalog.ProductRepository;
import org.example.storemanager.service.inventory.StockTransferService;
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
public class StockTransferServiceImpl implements StockTransferService {

    private final StockTransferRepository transferRepository;
    private final StockTransferDetailRepository detailRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    @Autowired
    public StockTransferServiceImpl(StockTransferRepository transferRepository,
                                    StockTransferDetailRepository detailRepository,
                                    BranchRepository branchRepository,
                                    ProductRepository productRepository,
                                    InventoryService inventoryService) {
        this.transferRepository = transferRepository;
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

    @Override
    public List<StockTransferResponse> getAllTransfers() {
        return transferRepository.findByIsDeletedFalse().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<StockTransferResponse> getTransfersByFromBranch(Long branchId) {
        return transferRepository.findByFromBranchIdAndIsDeletedFalse(branchId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<StockTransferResponse> getTransfersByToBranch(Long branchId) {
        return transferRepository.findByToBranchIdAndIsDeletedFalse(branchId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public StockTransferResponse getTransferById(Long id) {
        StockTransfer transfer = transferRepository.findById(id)
                .filter(t -> !Boolean.TRUE.equals(t.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy phiếu điều chuyển", "id", id));
        return mapToResponse(transfer);
    }

    @Override
    @Transactional
    public StockTransferResponse createTransfer(StockTransferRequest request) {
        if (request.getFromBranchId().equals(request.getToBranchId())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "Chi nhánh xuất và chi nhánh nhận không được trùng nhau.");
        }
        Branch fromBranch = branchRepository.findById(request.getFromBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRANCH_NOT_FOUND, "Chi nhánh xuất", "id", request.getFromBranchId()));
        Branch toBranch = branchRepository.findById(request.getToBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRANCH_NOT_FOUND, "Chi nhánh nhận", "id", request.getToBranchId()));

        StockTransfer transfer = StockTransfer.builder()
                .transferCode(request.getTransferCode())
                .transferDate(request.getTransferDate() != null ? request.getTransferDate() : LocalDateTime.now())
                .status(request.getStatus() != null ? request.getStatus().name() : TransferStatus.DRAFT.name())
                .fromBranch(fromBranch)
                .toBranch(toBranch)
                .build();

        transfer.setCreatedBy(getCurrentUser());
        StockTransfer savedTransfer = transferRepository.save(transfer);

        List<StockTransferDetail> details = new ArrayList<>();
        for (TransferLineRequest line : request.getTransferLines()) {
            Product product = productRepository.findByIdAndIsDeletedFalse(line.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm", "id", line.getProductId()));

            BigDecimal qty = BigDecimal.valueOf(line.getTransferQuantity());

            StockTransferDetail detail = StockTransferDetail.builder()
                    .transfer(savedTransfer)
                    .product(product)
                    .quantityShipped(qty)
                    .quantityReceived(BigDecimal.ZERO)
                    .build();

            detail.setCreatedBy(getCurrentUser());
            detail.setNote(line.getReason());
            details.add(detailRepository.save(detail));
        }

        // Tự động trừ kho của chi nhánh chuyển đi nếu chuyển trực tiếp trạng thái thành IN_TRANSIT
        if (TransferStatus.IN_TRANSIT.name().equals(savedTransfer.getStatus())) {
            deductFromSourceInventory(savedTransfer, details);
        }

        return mapToResponse(savedTransfer);
    }

    @Override
    @Transactional
    public StockTransferResponse updateTransfer(Long id, StockTransferRequest request) {
        if (request.getFromBranchId().equals(request.getToBranchId())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "Chi nhánh xuất và chi nhánh nhận không được trùng nhau.");
        }
        StockTransfer transfer = transferRepository.findById(id)
                .filter(t -> !Boolean.TRUE.equals(t.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy phiếu điều chuyển", "id", id));

        if (!TransferStatus.DRAFT.name().equals(transfer.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Chỉ có thể sửa phiếu điều chuyển ở trạng thái DRAFT.");
        }

        Branch fromBranch = branchRepository.findById(request.getFromBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRANCH_NOT_FOUND, "Chi nhánh xuất", "id", request.getFromBranchId()));
        Branch toBranch = branchRepository.findById(request.getToBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRANCH_NOT_FOUND, "Chi nhánh nhận", "id", request.getToBranchId()));

        transfer.setTransferCode(request.getTransferCode());
        transfer.setTransferDate(request.getTransferDate());
        transfer.setFromBranch(fromBranch);
        transfer.setToBranch(toBranch);
        transfer.setUpdatedBy(getCurrentUser());

        StockTransfer saved = transferRepository.save(transfer);

        // Xóa các dòng cũ
        List<StockTransferDetail> oldDetails = detailRepository.findByTransferIdAndIsDeletedFalse(id);
        for (StockTransferDetail d : oldDetails) {
            d.setIsDeleted(true);
            d.setDeletedAt(LocalDateTime.now());
            d.setDeletedBy(getCurrentUser());
            detailRepository.save(d);
        }

        List<StockTransferDetail> details = new ArrayList<>();
        for (TransferLineRequest line : request.getTransferLines()) {
            Product product = productRepository.findByIdAndIsDeletedFalse(line.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm", "id", line.getProductId()));

            BigDecimal qty = BigDecimal.valueOf(line.getTransferQuantity());

            StockTransferDetail detail = StockTransferDetail.builder()
                    .transfer(saved)
                    .product(product)
                    .quantityShipped(qty)
                    .quantityReceived(BigDecimal.ZERO)
                    .build();

            detail.setCreatedBy(getCurrentUser());
            detail.setNote(line.getReason());
            details.add(detailRepository.save(detail));
        }

        if (request.getStatus() != null && TransferStatus.IN_TRANSIT.name().equals(request.getStatus().name())) {
            saved.setStatus(TransferStatus.IN_TRANSIT.name());
            deductFromSourceInventory(saved, details);
            transferRepository.save(saved);
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public StockTransferResponse shipTransfer(Long id) {
        StockTransfer transfer = transferRepository.findById(id)
                .filter(t -> !Boolean.TRUE.equals(t.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy phiếu điều chuyển", "id", id));

        if (!TransferStatus.DRAFT.name().equals(transfer.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Chỉ có thể xuất hàng đi từ phiếu DRAFT.");
        }

        transfer.setStatus(TransferStatus.IN_TRANSIT.name());
        transfer.setUpdatedBy(getCurrentUser());
        StockTransfer saved = transferRepository.save(transfer);

        List<StockTransferDetail> details = detailRepository.findByTransferIdAndIsDeletedFalse(id);
        deductFromSourceInventory(saved, details);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public StockTransferResponse receiveTransfer(Long id) {
        StockTransfer transfer = transferRepository.findById(id)
                .filter(t -> !Boolean.TRUE.equals(t.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy phiếu điều chuyển", "id", id));

        if (!TransferStatus.IN_TRANSIT.name().equals(transfer.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Chỉ có thể nhận hàng khi phiếu ở trạng thái đang vận chuyển (IN_TRANSIT).");
        }

        transfer.setStatus(TransferStatus.COMPLETED.name());
        transfer.setUpdatedBy(getCurrentUser());
        StockTransfer saved = transferRepository.save(transfer);

        List<StockTransferDetail> details = detailRepository.findByTransferIdAndIsDeletedFalse(id);
        for (StockTransferDetail detail : details) {
            detail.setQuantityReceived(detail.getQuantityShipped());
            detailRepository.save(detail);

            // Cộng tồn kho chi nhánh nhận
            inventoryService.updateStock(saved.getToBranch().getId(), detail.getProduct().getId(),
                    detail.getQuantityShipped(), "TRANSFER_IN", saved.getId(), null);
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public StockTransferResponse rejectTransfer(Long id) {
        StockTransfer transfer = transferRepository.findById(id)
                .filter(t -> !Boolean.TRUE.equals(t.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy phiếu điều chuyển", "id", id));

        if (TransferStatus.COMPLETED.name().equals(transfer.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Không thể từ chối phiếu điều chuyển đã hoàn thành.");
        }

        // Nếu đang IN_TRANSIT mà từ chối, cần trả lại hàng về kho cũ
        if (TransferStatus.IN_TRANSIT.name().equals(transfer.getStatus())) {
            List<StockTransferDetail> details = detailRepository.findByTransferIdAndIsDeletedFalse(id);
            for (StockTransferDetail detail : details) {
                // Hoàn lại tồn kho cho kho gửi
                inventoryService.updateStock(transfer.getFromBranch().getId(), detail.getProduct().getId(),
                        detail.getQuantityShipped(), "TRANSFER_REJECT_RETURN", transfer.getId(), null);
            }
        }

        transfer.setStatus("REJECTED");
        transfer.setUpdatedBy(getCurrentUser());
        StockTransfer saved = transferRepository.save(transfer);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteTransfer(Long id) {
        StockTransfer transfer = transferRepository.findById(id)
                .filter(t -> !Boolean.TRUE.equals(t.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy phiếu điều chuyển", "id", id));

        if (!TransferStatus.DRAFT.name().equals(transfer.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Chỉ có thể xóa phiếu nháp.");
        }

        transfer.setIsDeleted(true);
        transfer.setDeletedAt(LocalDateTime.now());
        transfer.setDeletedBy(getCurrentUser());
        transferRepository.save(transfer);
    }

    private void deductFromSourceInventory(StockTransfer transfer, List<StockTransferDetail> details) {
        for (StockTransferDetail detail : details) {
            // Trừ tồn kho chi nhánh gửi
            inventoryService.updateStock(transfer.getFromBranch().getId(), detail.getProduct().getId(),
                    detail.getQuantityShipped().negate(), "TRANSFER_OUT", transfer.getId(), null);
        }
    }

    private StockTransferResponse mapToResponse(StockTransfer transfer) {
        List<StockTransferDetail> details = detailRepository.findByTransferIdAndIsDeletedFalse(transfer.getId());
        List<TransferLineResponse> lines = details.stream().map(d -> TransferLineResponse.builder()
                .id(d.getId())
                .productId(d.getProduct().getId())
                .productCode(d.getProduct().getProductCode())
                .productName(d.getProduct().getName())
                .transferQuantity(d.getQuantityShipped() != null ? d.getQuantityShipped().intValue() : 0)
                .reason(d.getNote())
                .build()).collect(Collectors.toList());

        return StockTransferResponse.builder()
                .id(transfer.getId())
                .transferCode(transfer.getTransferCode())
                .transferDate(transfer.getTransferDate())
                .fromBranchId(transfer.getFromBranch().getId())
                .fromBranchName(transfer.getFromBranch().getBranchName())
                .toBranchId(transfer.getToBranch().getId())
                .toBranchName(transfer.getToBranch().getBranchName())
                .status(TransferStatus.valueOf(transfer.getStatus()))
                .transferLines(lines)
                .createdBy(transfer.getCreatedBy())
                .createdAt(transfer.getCreatedAt())
                .updatedAt(transfer.getUpdatedAt())
                .build();
    }
}
