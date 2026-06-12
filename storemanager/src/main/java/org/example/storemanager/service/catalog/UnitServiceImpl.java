package org.example.storemanager.service.catalog;

import org.example.storemanager.dto.request.catalog.UnitRequest;
import org.example.storemanager.dto.response.catalog.UnitResponse;
import org.example.storemanager.entity.catalog.Unit;
import org.example.storemanager.enums.ErrorCode;
import org.example.storemanager.exception.DuplicateResourceException;
import org.example.storemanager.exception.ResourceNotFoundException;
import org.example.storemanager.repository.catalog.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;

    @Autowired
    public UnitServiceImpl(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    private String getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        }
        return "system";
    }

    @Override
    public List<UnitResponse> getAllUnits() {
        return unitRepository.findAllByIsDeletedFalse().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UnitResponse getUnitById(Long id) {
        Unit unit = unitRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.UNIT_NOT_FOUND, "Đơn vị tính", "id", id));
        return mapToResponse(unit);
    }

    @Override
    @Transactional
    public UnitResponse createUnit(UnitRequest request) {
        String code = request.getAbbreviation();
        if (code == null || code.trim().isEmpty()) {
            code = "UNT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } else {
            code = code.trim().toUpperCase();
            if (unitRepository.existsByUnitCodeAndIsDeletedFalse(code)) {
                throw new DuplicateResourceException("Đơn vị tính", "unitCode", code);
            }
        }

        Unit unit = Unit.builder()
                .unitCode(code)
                .unitName(request.getUnitName())
                .description(request.getDescription())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
        unit.setCreatedBy(getCurrentUser());

        Unit saved = unitRepository.save(unit);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public UnitResponse updateUnit(Long id, UnitRequest request) {
        Unit unit = unitRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.UNIT_NOT_FOUND, "Đơn vị tính", "id", id));

        String code = request.getAbbreviation();
        if (code != null && !code.trim().isEmpty()) {
            code = code.trim().toUpperCase();
            if (!unit.getUnitCode().equals(code) && unitRepository.existsByUnitCodeAndIsDeletedFalse(code)) {
                throw new DuplicateResourceException("Đơn vị tính", "unitCode", code);
            }
            unit.setUnitCode(code);
        }

        unit.setUnitName(request.getUnitName());
        unit.setDescription(request.getDescription());
        if (request.getIsActive() != null) {
            unit.setIsActive(request.getIsActive());
        }
        unit.setUpdatedBy(getCurrentUser());

        Unit saved = unitRepository.save(unit);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteUnit(Long id) {
        Unit unit = unitRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.UNIT_NOT_FOUND, "Đơn vị tính", "id", id));

        if (unit.getIsActive() != null && unit.getIsActive()) {
            throw new org.example.storemanager.exception.BusinessException(ErrorCode.BUSINESS_ERROR, "Không thể xóa đơn vị tính đang hoạt động. Vui lòng chuyển trạng thái hoạt động (isActive) sang false trước khi xóa.");
        }

        // Thiết lập trạng thái xóa mềm, ghi nhận người xóa và ngày xóa
        unit.setIsDeleted(true);
        unit.setDeletedAt(LocalDateTime.now());
        unit.setDeletedBy(getCurrentUser());

        unitRepository.save(unit);
    }

    @Override
    public List<UnitResponse> getSoftDeletedUnits() {
        return unitRepository.findAllByIsDeletedTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private UnitResponse mapToResponse(Unit unit) {
        if (unit == null) return null;
        return UnitResponse.builder()
                .id(unit.getId())
                .unitName(unit.getUnitName())
                .abbreviation(unit.getUnitCode())
                .description(unit.getDescription())
                .isActive(unit.getIsActive())
                .deletedAt(unit.getDeletedAt())
                .deletedBy(unit.getDeletedBy())
                .createdAt(unit.getCreatedAt())
                .createdBy(unit.getCreatedBy())
                .updatedAt(unit.getUpdatedAt())
                .updatedBy(unit.getUpdatedBy())
                .build();
    }
}
