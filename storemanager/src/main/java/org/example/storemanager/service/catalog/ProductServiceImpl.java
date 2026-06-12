package org.example.storemanager.service.catalog;

import org.example.storemanager.dto.request.catalog.ProductRequest;
import org.example.storemanager.dto.request.catalog.ProductUnitRequest;
import org.example.storemanager.dto.request.catalog.SerialNumberBatchRequest;
import org.example.storemanager.dto.request.catalog.SerialNumberItemRequest;
import org.example.storemanager.dto.response.catalog.ProductResponse;
import org.example.storemanager.dto.response.catalog.ProductUnitResponse;
import org.example.storemanager.dto.response.catalog.SerialNumberResponse;
import org.example.storemanager.entity.catalog.Product;
import org.example.storemanager.entity.catalog.ProductCategory;
import org.example.storemanager.entity.catalog.ProductUnit;
import org.example.storemanager.entity.catalog.SerialNumber;
import org.example.storemanager.entity.catalog.Unit;
import org.example.storemanager.enums.ErrorCode;
import org.example.storemanager.exception.DuplicateResourceException;
import org.example.storemanager.exception.ResourceNotFoundException;
import org.example.storemanager.repository.catalog.ProductCategoryRepository;
import org.example.storemanager.repository.catalog.ProductRepository;
import org.example.storemanager.repository.catalog.ProductUnitRepository;
import org.example.storemanager.repository.catalog.SerialNumberRepository;
import org.example.storemanager.repository.catalog.UnitRepository;
import org.example.storemanager.service.common.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final UnitRepository unitRepository;
    private final ProductUnitRepository productUnitRepository;
    private final SerialNumberRepository serialNumberRepository;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              ProductCategoryRepository categoryRepository,
                              UnitRepository unitRepository,
                              ProductUnitRepository productUnitRepository,
                              SerialNumberRepository serialNumberRepository,
                              CloudinaryService cloudinaryService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.unitRepository = unitRepository;
        this.productUnitRepository = productUnitRepository;
        this.serialNumberRepository = serialNumberRepository;
        this.cloudinaryService = cloudinaryService;
    }

    private String getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        }
        return "system";
    }

    @Override
    public Page<ProductResponse> searchProducts(String search, Long categoryId, Boolean isActive, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.searchProducts(search, categoryId, isActive, pageable);
        return products.map(this::mapToResponse);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm", "id", id));
        return mapToResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByProductCodeAndIsDeletedFalse(request.getProductCode())) {
            throw new DuplicateResourceException("Sản phẩm", "productCode", request.getProductCode());
        }

        ProductCategory category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findByIdAndIsDeletedFalse(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND, "Nhóm sản phẩm", "id", request.getCategoryId()));
        }

        Unit baseUnit = unitRepository.findByIdAndIsDeletedFalse(request.getBaseUnitId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.UNIT_NOT_FOUND, "Đơn vị tính", "id", request.getBaseUnitId()));

        Product product = Product.builder()
                .productCode(request.getProductCode())
                .name(request.getName())
                .description(request.getDescription())
                .basePrice(request.getBasePrice())
                .costPrice(request.getCostPrice())
                .brand(request.getBrand())
                .mainImageUrl(request.getMainImageUrl())
                .barcode(request.getBarcode())
                .category(category)
                .baseUnit(baseUnit)
                .weight(request.getWeight())
                .reorderPoint(request.getReorderPoint())
                .minStock(request.getMinStock())
                .maxStock(request.getMaxStock())
                .galleryImages(request.getGalleryImages())
                .variants(request.getVariants())
                .build();
        product.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        product.setCreatedBy(getCurrentUser());

        Product saved = productRepository.save(product);
        saveConversionUnits(saved, request.getConversionUnits());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm", "id", id));

        if (!product.getProductCode().equals(request.getProductCode()) &&
                productRepository.existsByProductCodeAndIsDeletedFalse(request.getProductCode())) {
            throw new DuplicateResourceException("Sản phẩm", "productCode", request.getProductCode());
        }

        ProductCategory category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findByIdAndIsDeletedFalse(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND, "Nhóm sản phẩm", "id", request.getCategoryId()));
        }

        Unit baseUnit = unitRepository.findByIdAndIsDeletedFalse(request.getBaseUnitId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.UNIT_NOT_FOUND, "Đơn vị tính", "id", request.getBaseUnitId()));

        product.setProductCode(request.getProductCode());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBasePrice(request.getBasePrice());
        product.setCostPrice(request.getCostPrice());
        product.setBrand(request.getBrand());
        if (request.getMainImageUrl() != null) {
            product.setMainImageUrl(request.getMainImageUrl());
        }
        product.setBarcode(request.getBarcode());
        product.setCategory(category);
        product.setBaseUnit(baseUnit);
        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }
        product.setWeight(request.getWeight());
        product.setReorderPoint(request.getReorderPoint());
        product.setMinStock(request.getMinStock());
        product.setMaxStock(request.getMaxStock());
        product.setGalleryImages(request.getGalleryImages());
        product.setVariants(request.getVariants());
        product.setUpdatedBy(getCurrentUser());

        productUnitRepository.deleteAllByProductId(id);
        Product saved = productRepository.save(product);
        saveConversionUnits(saved, request.getConversionUnits());
        return mapToResponse(saved);
    }

    private void saveConversionUnits(Product product, List<ProductUnitRequest> conversionUnits) {
        if (conversionUnits == null || conversionUnits.isEmpty()) {
            return;
        }
        for (ProductUnitRequest unitReq : conversionUnits) {
            Unit unit = unitRepository.findByIdAndIsDeletedFalse(unitReq.getUnitId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.UNIT_NOT_FOUND, "Đơn vị tính quy đổi", "id", unitReq.getUnitId()));

            ProductUnit productUnit = ProductUnit.builder()
                    .product(product)
                    .unit(unit)
                    .conversionRate(BigDecimal.valueOf(unitReq.getConversionRate()))
                    .price(unitReq.getPrice() != null ? unitReq.getPrice() : BigDecimal.ZERO)
                    .barcode(unitReq.getBarcode())
                    .build();
            productUnit.setCreatedBy(getCurrentUser());
            productUnitRepository.save(productUnit);
        }
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm", "id", id));

        if (product.getIsActive() != null && product.getIsActive()) {
            throw new org.example.storemanager.exception.BusinessException(ErrorCode.BUSINESS_ERROR, "Không thể xóa sản phẩm đang hoạt động. Vui lòng chuyển trạng thái hoạt động (isActive) sang false trước khi xóa.");
        }

        // Thiết lập trạng thái xóa mềm, ghi nhận người xóa và ngày xóa
        product.setIsDeleted(true);
        product.setDeletedAt(LocalDateTime.now());
        product.setDeletedBy(getCurrentUser());

        productRepository.save(product);
    }

    @Override
    @Transactional
    public ProductResponse uploadProductImage(Long id, MultipartFile file) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm", "id", id));

        String url = cloudinaryService.uploadFile(file);
        product.setMainImageUrl(url);
        product.setUpdatedBy(getCurrentUser());

        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    private ProductResponse mapToResponse(Product product) {
        if (product == null) return null;

        List<ProductUnitResponse> unitResponses = productUnitRepository.findAllByProductIdAndIsDeletedFalse(product.getId())
                .stream()
                .map(pu -> ProductUnitResponse.builder()
                        .id(pu.getId())
                        .unitId(pu.getUnit().getId())
                        .unitName(pu.getUnit().getUnitName())
                        .unitCode(pu.getUnit().getUnitCode())
                        .conversionRate(pu.getConversionRate().intValue())
                        .price(pu.getPrice())
                        .barcode(pu.getBarcode())
                        .build())
                .collect(Collectors.toList());

        return ProductResponse.builder()
                .id(product.getId())
                .productCode(product.getProductCode())
                .name(product.getName())
                .description(product.getDescription())
                .basePrice(product.getBasePrice())
                .costPrice(product.getCostPrice())
                .brand(product.getBrand())
                .mainImageUrl(product.getMainImageUrl())
                .barcode(product.getBarcode())
                .isActive(product.getIsActive())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getCategoryName() : null)
                .baseUnitId(product.getBaseUnit() != null ? product.getBaseUnit().getId() : null)
                .baseUnitName(product.getBaseUnit() != null ? product.getBaseUnit().getUnitName() : null)
                .onHand(0) // Default 0, computed by stockledger if needed
                .units(unitResponses)
                .updatedAt(product.getUpdatedAt())
                .updatedBy(product.getUpdatedBy())
                .weight(product.getWeight())
                .reorderPoint(product.getReorderPoint())
                .minStock(product.getMinStock())
                .maxStock(product.getMaxStock())
                .galleryImages(product.getGalleryImages())
                .variants(product.getVariants())
                .deletedAt(product.getDeletedAt())
                .deletedBy(product.getDeletedBy())
                .createdAt(product.getCreatedAt())
                .createdBy(product.getCreatedBy())
                .build();
    }

    @Override
    public List<SerialNumberResponse> getProductSerials(Long productId, String status) {
        productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm", "id", productId));

        List<SerialNumber> serials;
        if (status == null || status.trim().isEmpty()) {
            serials = serialNumberRepository.findAllByProductIdAndIsDeletedFalse(productId);
        } else {
            serials = serialNumberRepository.findAllByProductIdAndStatusAndIsDeletedFalse(productId, status);
        }

        return serials.stream()
                .map(this::mapToSerialNumberResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<SerialNumberResponse> addProductSerials(Long productId, SerialNumberBatchRequest request) {
        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm", "id", productId));

        List<SerialNumber> savedSerials = new java.util.ArrayList<>();
        String status = request.getStatus() != null ? request.getStatus() : "AVAILABLE";

        if (request.getSerialDetails() != null && !request.getSerialDetails().isEmpty()) {
            for (SerialNumberItemRequest detail : request.getSerialDetails()) {
                if (serialNumberRepository.existsBySerialNumberAndIsDeletedFalse(detail.getSerialNumber())) {
                    throw new DuplicateResourceException("Số Serial/IMEI", "serialNumber", detail.getSerialNumber());
                }

                SerialNumber serial = SerialNumber.builder()
                        .serialNumber(detail.getSerialNumber())
                        .status(status)
                        .product(product)
                        .macAddress(detail.getMacAddress())
                        .imei1(detail.getImei1())
                        .imei2(detail.getImei2())
                        .warrantyExpiry(detail.getWarrantyExpiry())
                        .build();
                serial.setCreatedBy(getCurrentUser());
                savedSerials.add(serialNumberRepository.save(serial));
            }
        } else if (request.getSerialNumbers() != null) {
            for (String serialNum : request.getSerialNumbers()) {
                if (serialNumberRepository.existsBySerialNumberAndIsDeletedFalse(serialNum)) {
                    throw new DuplicateResourceException("Số Serial/IMEI", "serialNumber", serialNum);
                }

                SerialNumber serial = SerialNumber.builder()
                        .serialNumber(serialNum)
                        .status(status)
                        .product(product)
                        .build();
                serial.setCreatedBy(getCurrentUser());
                savedSerials.add(serialNumberRepository.save(serial));
            }
        }

        return savedSerials.stream()
                .map(this::mapToSerialNumberResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getSoftDeletedProducts() {
        return productRepository.findAllByIsDeletedTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SerialNumberResponse> getSoftDeletedSerials() {
        return serialNumberRepository.findAllByIsDeletedTrue().stream()
                .map(this::mapToSerialNumberResponse)
                .collect(Collectors.toList());
    }

    private SerialNumberResponse mapToSerialNumberResponse(SerialNumber serialNumber) {
        if (serialNumber == null) return null;
        return SerialNumberResponse.builder()
                .id(serialNumber.getId())
                .serialNumber(serialNumber.getSerialNumber())
                .status(serialNumber.getStatus())
                .importReceiptId(serialNumber.getImportReceiptId())
                .macAddress(serialNumber.getMacAddress())
                .imei1(serialNumber.getImei1())
                .imei2(serialNumber.getImei2())
                .warrantyExpiry(serialNumber.getWarrantyExpiry())
                .deletedAt(serialNumber.getDeletedAt())
                .deletedBy(serialNumber.getDeletedBy())
                .createdAt(serialNumber.getCreatedAt())
                .createdBy(serialNumber.getCreatedBy())
                .updatedAt(serialNumber.getUpdatedAt())
                .updatedBy(serialNumber.getUpdatedBy())
                .build();
    }
}
