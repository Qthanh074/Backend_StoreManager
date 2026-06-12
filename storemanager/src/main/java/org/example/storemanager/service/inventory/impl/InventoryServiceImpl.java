package org.example.storemanager.service.inventory.impl;

import org.example.storemanager.dto.response.inventory.InventoryResponse;
import org.example.storemanager.entity.inventory.Inventory;
import org.example.storemanager.entity.system.Branch;
import org.example.storemanager.entity.catalog.Product;
import org.example.storemanager.enums.ErrorCode;
import org.example.storemanager.exception.BusinessException;
import org.example.storemanager.exception.ResourceNotFoundException;
import org.example.storemanager.repository.inventory.InventoryRepository;
import org.example.storemanager.repository.system.BranchRepository;
import org.example.storemanager.repository.catalog.ProductRepository;
import org.example.storemanager.service.inventory.InventoryService;
import org.example.storemanager.service.inventory.StockLedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final StockLedgerService ledgerService;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository,
                                BranchRepository branchRepository,
                                ProductRepository productRepository,
                                StockLedgerService ledgerService) {
        this.inventoryRepository = inventoryRepository;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
        this.ledgerService = ledgerService;
    }

    private String getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        }
        return "system";
    }

    @Override
    public List<InventoryResponse> getInventoryByBranch(Long branchId) {
        return inventoryRepository.findByBranchIdAndIsDeletedFalse(branchId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findByIsDeletedFalse()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public InventoryResponse getInventoryByBranchAndProduct(Long branchId, Long productId) {
        Inventory inventory = inventoryRepository.findByBranchIdAndProductIdAndIsDeletedFalse(branchId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy tồn kho cho sản phẩm này tại chi nhánh", "productId", productId));
        return mapToResponse(inventory);
    }

    @Override
    @Transactional
    public void updateStock(Long branchId, Long productId, BigDecimal quantityChange, String transactionType, Long referenceId, Long batchId) {
        Inventory inventory = inventoryRepository.findByBranchIdAndProductIdAndIsDeletedFalse(branchId, productId)
                .orElse(null);

        BigDecimal previousQty = BigDecimal.ZERO;
        if (inventory == null) {
            Branch branch = branchRepository.findById(branchId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRANCH_NOT_FOUND, "Chi nhánh", "id", branchId));
            Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm", "id", productId));

            inventory = Inventory.builder()
                    .branch(branch)
                    .product(product)
                    .quantity(BigDecimal.ZERO)
                    .lastUpdated(LocalDateTime.now())
                    .build();
            inventory.setCreatedBy(getCurrentUser());
        } else {
            previousQty = inventory.getQuantity();
            inventory.setUpdatedBy(getCurrentUser());
        }

        BigDecimal newQty = previousQty.add(quantityChange);
        if (newQty.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK, 
                    "Số lượng tồn kho sản phẩm [" + inventory.getProduct().getName() + "] không đủ để thực hiện giao dịch (Tồn: " + previousQty + ", Cần trừ: " + quantityChange.abs() + ").");
        }

        inventory.setQuantity(newQty);
        inventory.setLastUpdated(LocalDateTime.now());
        inventoryRepository.save(inventory);

        // Ghi nhật ký vào StockLedger
        ledgerService.logChange(branchId, productId, quantityChange, newQty, transactionType, referenceId, batchId);
    }

    private InventoryResponse mapToResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProduct().getId())
                .productCode(inventory.getProduct().getProductCode())
                .productName(inventory.getProduct().getName())
                .branchId(inventory.getBranch().getId())
                .branchName(inventory.getBranch().getBranchName())
                .quantityOnHand(inventory.getQuantity() != null ? inventory.getQuantity().intValue() : 0)
                .quantityReserved(0)
                .quantityAvailable(inventory.getQuantity() != null ? inventory.getQuantity().intValue() : 0)
                .locationBin(inventory.getNote() != null ? inventory.getNote() : "")
                .lastUpdatedAt(inventory.getLastUpdated() != null ? inventory.getLastUpdated() : LocalDateTime.now())
                .build();
    }
}
