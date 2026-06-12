package org.example.storemanager.service.inventory.impl;

import org.example.storemanager.dto.request.inventory.ProductBatchRequest;
import org.example.storemanager.dto.response.inventory.ProductBatchResponse;
import org.example.storemanager.entity.inventory.ProductBatch;
import org.example.storemanager.entity.catalog.Product;
import org.example.storemanager.enums.ErrorCode;
import org.example.storemanager.exception.ResourceNotFoundException;
import org.example.storemanager.repository.inventory.ProductBatchRepository;
import org.example.storemanager.repository.catalog.ProductRepository;
import org.example.storemanager.service.inventory.ProductBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductBatchServiceImpl implements ProductBatchService {

    private final ProductBatchRepository batchRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ProductBatchServiceImpl(ProductBatchRepository batchRepository,
                                   ProductRepository productRepository) {
        this.batchRepository = batchRepository;
        this.productRepository = productRepository;
    }

    private String getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        }
        return "system";
    }

    @Override
    public List<ProductBatchResponse> getAllBatches() {
        return batchRepository.findByIsDeletedFalse().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<ProductBatchResponse> getBatchesByProduct(Long productId) {
        return batchRepository.findByProductIdAndIsDeletedFalse(productId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public ProductBatchResponse getBatchById(Long id) {
        ProductBatch batch = batchRepository.findById(id)
                .filter(b -> !Boolean.TRUE.equals(b.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy lô sản phẩm", "id", id));
        return mapToResponse(batch);
    }

    @Override
    @Transactional
    public ProductBatchResponse createBatch(ProductBatchRequest request) {
        Product product = productRepository.findByIdAndIsDeletedFalse(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm", "id", request.getProductId()));

        ProductBatch batch = ProductBatch.builder()
                .batchNumber(request.getBatchNumber())
                .manufactureDate(request.getManufactureDate())
                .expiryDate(request.getExpiryDate())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .product(product)
                .initialUnits(request.getInitialUnits())
                .remainingUnits(request.getRemainingUnits())
                .unitCost(request.getUnitCost())
                .supplierName(request.getSupplierName())
                .location(request.getLocation())
                .qualityStatus(request.getQualityStatus() != null ? request.getQualityStatus() : "PASSED_QA")
                .inspector(request.getInspector())
                .build();

        batch.setCreatedBy(getCurrentUser());
        batch.setNote(request.getNotes());
        ProductBatch saved = batchRepository.save(batch);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ProductBatchResponse updateBatch(Long id, ProductBatchRequest request) {
        ProductBatch batch = batchRepository.findById(id)
                .filter(b -> !Boolean.TRUE.equals(b.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy lô sản phẩm", "id", id));

        Product product = productRepository.findByIdAndIsDeletedFalse(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm", "id", request.getProductId()));

        batch.setBatchNumber(request.getBatchNumber());
        batch.setManufactureDate(request.getManufactureDate());
        batch.setExpiryDate(request.getExpiryDate());
        batch.setStatus(request.getStatus());
        batch.setProduct(product);
        batch.setInitialUnits(request.getInitialUnits());
        batch.setRemainingUnits(request.getRemainingUnits());
        batch.setUnitCost(request.getUnitCost());
        batch.setSupplierName(request.getSupplierName());
        batch.setLocation(request.getLocation());
        batch.setQualityStatus(request.getQualityStatus());
        batch.setInspector(request.getInspector());
        batch.setNote(request.getNotes());
        batch.setUpdatedBy(getCurrentUser());

        ProductBatch saved = batchRepository.save(batch);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteBatch(Long id) {
        ProductBatch batch = batchRepository.findById(id)
                .filter(b -> !Boolean.TRUE.equals(b.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy lô sản phẩm", "id", id));

        batch.setIsDeleted(true);
        batch.setDeletedAt(LocalDateTime.now());
        batch.setDeletedBy(getCurrentUser());
        batchRepository.save(batch);
    }

    private ProductBatchResponse mapToResponse(ProductBatch batch) {
        return ProductBatchResponse.builder()
                .id(batch.getId())
                .batchNumber(batch.getBatchNumber())
                .sku(batch.getProduct().getProductCode())
                .productName(batch.getProduct().getName())
                .manufactureDate(batch.getManufactureDate())
                .expiryDate(batch.getExpiryDate())
                .initialUnits(batch.getInitialUnits())
                .remainingUnits(batch.getRemainingUnits())
                .unitCost(batch.getUnitCost())
                .supplierName(batch.getSupplierName())
                .location(batch.getLocation())
                .qualityStatus(batch.getQualityStatus())
                .inspector(batch.getInspector())
                .status(batch.getStatus())
                .notes(batch.getNote())
                .build();
    }
}
