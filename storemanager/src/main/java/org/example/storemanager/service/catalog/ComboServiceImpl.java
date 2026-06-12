package org.example.storemanager.service.catalog;

import org.example.storemanager.dto.request.catalog.ComboRequest;
import org.example.storemanager.dto.response.catalog.ComboResponse;
import org.example.storemanager.entity.catalog.Combo;
import org.example.storemanager.entity.catalog.ComboDetail;
import org.example.storemanager.entity.catalog.Product;
import org.example.storemanager.enums.ErrorCode;
import org.example.storemanager.exception.DuplicateResourceException;
import org.example.storemanager.exception.ResourceNotFoundException;
import org.example.storemanager.repository.catalog.ComboDetailRepository;
import org.example.storemanager.repository.catalog.ComboRepository;
import org.example.storemanager.repository.catalog.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComboServiceImpl implements ComboService {

    private final ComboRepository comboRepository;
    private final ComboDetailRepository comboDetailRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ComboServiceImpl(ComboRepository comboRepository,
                            ComboDetailRepository comboDetailRepository,
                            ProductRepository productRepository) {
        this.comboRepository = comboRepository;
        this.comboDetailRepository = comboDetailRepository;
        this.productRepository = productRepository;
    }

    private String getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        }
        return "system";
    }

    @Override
    public Page<ComboResponse> searchCombos(String search, Boolean isActive, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Combo> combos = comboRepository.searchCombos(search, isActive, pageable);
        return combos.map(this::mapToResponse);
    }

    @Override
    public ComboResponse getComboById(Long id) {
        Combo combo = comboRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.COMBO_NOT_FOUND, "Combo", "id", id));
        return mapToResponse(combo);
    }

    @Override
    @Transactional
    public ComboResponse createCombo(ComboRequest request) {
        if (comboRepository.existsByComboCodeAndIsDeletedFalse(request.getComboCode())) {
            throw new DuplicateResourceException("Combo", "comboCode", request.getComboCode());
        }

        Combo combo = Combo.builder()
                .comboCode(request.getComboCode())
                .comboName(request.getComboName())
                .price(request.getPrice())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
        combo.setCreatedBy(getCurrentUser());
        Combo savedCombo = comboRepository.save(combo);

        List<ComboDetail> details = new ArrayList<>();
        if (request.getDetails() != null) {
            for (ComboRequest.ComboDetailRequest dReq : request.getDetails()) {
                Product product = productRepository.findByIdAndIsDeletedFalse(dReq.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm trong combo", "id", dReq.getProductId()));

                ComboDetail detail = ComboDetail.builder()
                        .combo(savedCombo)
                        .product(product)
                        .quantity(dReq.getQuantity())
                        .build();
                detail.setCreatedBy(getCurrentUser());
                details.add(comboDetailRepository.save(detail));
            }
        }

        return mapToResponse(savedCombo);
    }

    @Override
    @Transactional
    public ComboResponse updateCombo(Long id, ComboRequest request) {
        Combo combo = comboRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.COMBO_NOT_FOUND, "Combo", "id", id));

        if (!combo.getComboCode().equals(request.getComboCode()) &&
                comboRepository.existsByComboCodeAndIsDeletedFalse(request.getComboCode())) {
            throw new DuplicateResourceException("Combo", "comboCode", request.getComboCode());
        }

        combo.setComboCode(request.getComboCode());
        combo.setComboName(request.getComboName());
        combo.setPrice(request.getPrice());
        if (request.getIsActive() != null) {
            combo.setIsActive(request.getIsActive());
        }
        combo.setUpdatedBy(getCurrentUser());
        Combo savedCombo = comboRepository.save(combo);

        // Xóa các chi tiết cũ (xóa vật lý để tạo lại mới, hoặc đánh dấu xóa mềm)
        // Trong trường hợp quan hệ chi tiết này, việc xóa vật lý chi tiết cũ và tạo lại là phổ biến,
        // nhưng ta cũng có thể thực hiện xóa mềm. Ở đây để đơn giản và chuẩn JPA, ta xóa các bản ghi chi tiết cũ:
        comboDetailRepository.deleteAllByComboId(id);

        if (request.getDetails() != null) {
            for (ComboRequest.ComboDetailRequest dReq : request.getDetails()) {
                Product product = productRepository.findByIdAndIsDeletedFalse(dReq.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm trong combo", "id", dReq.getProductId()));

                ComboDetail detail = ComboDetail.builder()
                        .combo(savedCombo)
                        .product(product)
                        .quantity(dReq.getQuantity())
                        .build();
                detail.setCreatedBy(getCurrentUser());
                comboDetailRepository.save(detail);
            }
        }

        return mapToResponse(savedCombo);
    }

    @Override
    @Transactional
    public void deleteCombo(Long id) {
        Combo combo = comboRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.COMBO_NOT_FOUND, "Combo", "id", id));

        if (combo.getIsActive() != null && combo.getIsActive()) {
            throw new org.example.storemanager.exception.BusinessException(ErrorCode.BUSINESS_ERROR, "Không thể xóa gói combo đang hoạt động. Vui lòng chuyển trạng thái hoạt động (isActive) sang false trước khi xóa.");
        }

        // Soft delete Combo
        combo.setIsDeleted(true);
        combo.setDeletedAt(LocalDateTime.now());
        combo.setDeletedBy(getCurrentUser());
        comboRepository.save(combo);

        // Soft delete Combo Details
        List<ComboDetail> details = comboDetailRepository.findAllByComboIdAndIsDeletedFalse(id);
        for (ComboDetail detail : details) {
            detail.setIsDeleted(true);
            detail.setDeletedAt(LocalDateTime.now());
            detail.setDeletedBy(getCurrentUser());
            comboDetailRepository.save(detail);
        }
    }

    @Override
    public java.util.List<ComboResponse> getSoftDeletedCombos() {
        return comboRepository.findAllByIsDeletedTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ComboResponse mapToResponse(Combo combo) {
        if (combo == null) return null;

        List<ComboDetail> details = comboDetailRepository.findAllByComboIdAndIsDeletedFalse(combo.getId());
        List<ComboResponse.ComboDetailResponse> detailResponses = details.stream()
                .map(d -> ComboResponse.ComboDetailResponse.builder()
                        .id(d.getId())
                        .productId(d.getProduct().getId())
                        .productName(d.getProduct().getName())
                        .productCode(d.getProduct().getProductCode())
                        .quantity(d.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return ComboResponse.builder()
                .id(combo.getId())
                .comboCode(combo.getComboCode())
                .comboName(combo.getComboName())
                .price(combo.getPrice())
                .isActive(combo.getIsActive())
                .deletedAt(combo.getDeletedAt())
                .deletedBy(combo.getDeletedBy())
                .createdAt(combo.getCreatedAt())
                .createdBy(combo.getCreatedBy())
                .updatedAt(combo.getUpdatedAt())
                .updatedBy(combo.getUpdatedBy())
                .details(detailResponses)
                .build();
    }
}
