package org.example.storemanager.service.inventory.impl;

import org.example.storemanager.dto.response.inventory.StockLedgerResponse;
import org.example.storemanager.entity.inventory.StockLedger;
import org.example.storemanager.entity.inventory.ProductBatch;
import org.example.storemanager.entity.system.Branch;
import org.example.storemanager.entity.catalog.Product;
import org.example.storemanager.enums.ErrorCode;
import org.example.storemanager.exception.ResourceNotFoundException;
import org.example.storemanager.repository.inventory.StockLedgerRepository;
import org.example.storemanager.repository.inventory.ProductBatchRepository;
import org.example.storemanager.repository.system.BranchRepository;
import org.example.storemanager.repository.catalog.ProductRepository;
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
public class StockLedgerServiceImpl implements StockLedgerService {

    private final StockLedgerRepository ledgerRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final ProductBatchRepository batchRepository;

    @Autowired
    public StockLedgerServiceImpl(StockLedgerRepository ledgerRepository,
                                  BranchRepository branchRepository,
                                  ProductRepository productRepository,
                                  ProductBatchRepository batchRepository) {
        this.ledgerRepository = ledgerRepository;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
        this.batchRepository = batchRepository;
    }

    private String getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        }
        return "system";
    }

    @Override
    public List<StockLedgerResponse> getAllLedgerEntries() {
        return ledgerRepository.findByIsDeletedFalseOrderByCreatedAtDesc()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<StockLedgerResponse> getLedgerEntriesByBranch(Long branchId) {
        return ledgerRepository.findByBranchIdAndIsDeletedFalseOrderByCreatedAtDesc(branchId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<StockLedgerResponse> getLedgerEntriesByProduct(Long productId) {
        return ledgerRepository.findByProductIdAndIsDeletedFalseOrderByCreatedAtDesc(productId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<StockLedgerResponse> getLedgerEntriesByBranchAndProduct(Long branchId, Long productId) {
        return ledgerRepository.findByBranchIdAndProductIdAndIsDeletedFalseOrderByCreatedAtDesc(branchId, productId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void logChange(Long branchId, Long productId, BigDecimal changeQty, BigDecimal balanceAfter,
                          String transactionType, Long referenceId, Long batchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRANCH_NOT_FOUND, "Chi nhánh", "id", branchId));
        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm", "id", productId));

        ProductBatch batch = null;
        if (batchId != null) {
            batch = batchRepository.findById(batchId).orElse(null);
        }

        StockLedger entry = StockLedger.builder()
                .branch(branch)
                .product(product)
                .changeQty(changeQty)
                .balanceAfter(balanceAfter)
                .transactionType(transactionType)
                .referenceId(referenceId)
                .batch(batch)
                .build();

        entry.setCreatedBy(getCurrentUser());
        entry.setNote("Giao dịch tự động ghi sổ từ luồng: " + transactionType);
        ledgerRepository.save(entry);
    }

    private StockLedgerResponse mapToResponse(StockLedger ledger) {
        String referenceDoc = ledger.getTransactionType() + " ID: " + ledger.getReferenceId();
        return StockLedgerResponse.builder()
                .id(ledger.getId())
                .productId(ledger.getProduct().getId())
                .productCode(ledger.getProduct().getProductCode())
                .productName(ledger.getProduct().getName())
                .branchId(ledger.getBranch().getId())
                .branchName(ledger.getBranch().getBranchName())
                .transactionType(ledger.getTransactionType())
                .quantityChange(ledger.getChangeQty() != null ? ledger.getChangeQty().intValue() : 0)
                .runningBalance(ledger.getBalanceAfter() != null ? ledger.getBalanceAfter().intValue() : 0)
                .referenceDocument(referenceDoc)
                .notes(ledger.getNote())
                .transactionDate(ledger.getCreatedAt() != null ? ledger.getCreatedAt() : LocalDateTime.now())
                .createdBy(ledger.getCreatedBy())
                .build();
    }
}
