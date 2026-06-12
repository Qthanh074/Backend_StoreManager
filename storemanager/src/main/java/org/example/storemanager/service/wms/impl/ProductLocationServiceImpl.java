package org.example.storemanager.service.wms.impl;

import org.example.storemanager.dto.request.wms.ProductLocationAssignRequest;
import org.example.storemanager.dto.response.wms.ProductLocationResponse;
import org.example.storemanager.entity.catalog.Product;
import org.example.storemanager.entity.wms.ProductLocation;
import org.example.storemanager.entity.wms.WarehouseBin;
import org.example.storemanager.enums.ErrorCode;
import org.example.storemanager.exception.ResourceNotFoundException;
import org.example.storemanager.repository.catalog.ProductRepository;
import org.example.storemanager.repository.wms.ProductLocationRepository;
import org.example.storemanager.repository.wms.WarehouseBinRepository;
import org.example.storemanager.service.wms.ProductLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductLocationServiceImpl implements ProductLocationService {

    private final ProductLocationRepository productLocationRepository;
    private final ProductRepository productRepository;
    private final WarehouseBinRepository warehouseBinRepository;

    @Autowired
    public ProductLocationServiceImpl(ProductLocationRepository productLocationRepository,
                                      ProductRepository productRepository,
                                      WarehouseBinRepository warehouseBinRepository) {
        this.productLocationRepository = productLocationRepository;
        this.productRepository = productRepository;
        this.warehouseBinRepository = warehouseBinRepository;
    }

    private String getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        }
        return "system";
    }

    @Override
    public List<ProductLocationResponse> getProductLocations(Long productId, Long binId) {
        List<ProductLocation> locations;
        if (productId != null && binId != null) {
            locations = productLocationRepository.findByProductIdAndBinIdAndIsDeletedFalse(productId, binId)
                    .map(List::of)
                    .orElse(List.of());
        } else if (productId != null) {
            locations = productLocationRepository.findAllByProductIdAndIsDeletedFalse(productId);
        } else if (binId != null) {
            locations = productLocationRepository.findAllByBinIdAndIsDeletedFalse(binId);
        } else {
            locations = productLocationRepository.findAllByIsDeletedFalse();
        }
        return locations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductLocationResponse assignProductLocation(ProductLocationAssignRequest request) {
        Product product = productRepository.findByIdAndIsDeletedFalse(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm", "id", request.getProductId()));

        WarehouseBin bin = warehouseBinRepository.findByIdAndIsDeletedFalse(request.getBinId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BIN_NOT_FOUND, "Ô/kệ kho", "id", request.getBinId()));

        Optional<ProductLocation> existingOpt = productLocationRepository
                .findByProductIdAndBinIdAndIsDeletedFalse(request.getProductId(), request.getBinId());

        if (request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            if (existingOpt.isPresent()) {
                ProductLocation location = existingOpt.get();
                location.setIsDeleted(true);
                location.setDeletedAt(LocalDateTime.now());
                location.setDeletedBy(getCurrentUser());
                productLocationRepository.save(location);
                return mapToResponse(location);
            }
            return ProductLocationResponse.builder()
                    .productId(product.getId())
                    .productCode(product.getProductCode())
                    .productName(product.getName())
                    .binId(bin.getId())
                    .binCode(bin.getBinCode())
                    .zoneCode(bin.getZone() != null ? bin.getZone().getZoneCode() : null)
                    .quantity(BigDecimal.ZERO)
                    .build();
        }

        ProductLocation location;
        if (existingOpt.isPresent()) {
            location = existingOpt.get();
            location.setQuantity(request.getQuantity());
            location.setUpdatedBy(getCurrentUser());
        } else {
            location = ProductLocation.builder()
                    .product(product)
                    .bin(bin)
                    .quantity(request.getQuantity())
                    .build();
            location.setCreatedBy(getCurrentUser());
        }

        ProductLocation saved = productLocationRepository.save(location);
        return mapToResponse(saved);
    }

    private ProductLocationResponse mapToResponse(ProductLocation location) {
        if (location == null) return null;
        return ProductLocationResponse.builder()
                .id(location.getId())
                .productId(location.getProduct().getId())
                .productCode(location.getProduct().getProductCode())
                .productName(location.getProduct().getName())
                .binId(location.getBin().getId())
                .binCode(location.getBin().getBinCode())
                .zoneCode(location.getBin().getZone() != null ? location.getBin().getZone().getZoneCode() : null)
                .quantity(location.getQuantity())
                .build();
    }
}
