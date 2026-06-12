package org.example.storemanager.service.wms;

import org.example.storemanager.dto.request.wms.WarehouseBinRequest;
import org.example.storemanager.dto.request.wms.WarehouseZoneRequest;
import org.example.storemanager.dto.response.wms.WarehouseBinResponse;
import org.example.storemanager.dto.response.wms.WarehouseZoneResponse;
import org.example.storemanager.entity.system.Branch;
import org.example.storemanager.entity.wms.WarehouseBin;
import org.example.storemanager.entity.wms.WarehouseZone;
import org.example.storemanager.enums.ErrorCode;
import org.example.storemanager.exception.BusinessException;
import org.example.storemanager.exception.DuplicateResourceException;
import org.example.storemanager.exception.ResourceNotFoundException;
import org.example.storemanager.repository.system.BranchRepository;
import org.example.storemanager.repository.wms.WarehouseBinRepository;
import org.example.storemanager.repository.wms.WarehouseZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseService {

    private final WarehouseZoneRepository zoneRepository;
    private final WarehouseBinRepository binRepository;
    private final BranchRepository branchRepository;

    @Autowired
    public WarehouseService(WarehouseZoneRepository zoneRepository,
                            WarehouseBinRepository binRepository,
                            BranchRepository branchRepository) {
        this.zoneRepository = zoneRepository;
        this.binRepository = binRepository;
        this.branchRepository = branchRepository;
    }

    private String getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "system";
    }

    // ======================== ZONE ========================

    public List<WarehouseZoneResponse> getAllZones() {
        return zoneRepository.findByIsDeletedFalse()
                .stream().map(this::mapZone).collect(Collectors.toList());
    }

    public WarehouseZoneResponse getZoneById(Long id) {
        return mapZone(findZone(id));
    }

    @Transactional
    public WarehouseZoneResponse createZone(WarehouseZoneRequest req) {
        if (zoneRepository.existsByZoneCodeAndIsDeletedFalse(req.getZoneCode())) {
            throw new DuplicateResourceException("Khu vực kho", "zoneCode", req.getZoneCode());
        }
        Branch branch = branchRepository.findById(req.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRANCH_NOT_FOUND, "Chi nhánh", "id", req.getBranchId()));

        WarehouseZone zone = WarehouseZone.builder()
                .zoneCode(req.getZoneCode())
                .zoneName(req.getZoneName())
                .conditions(req.getConditions())
                .branch(branch)
                .build();
        zone.setCreatedBy(getCurrentUser());
        return mapZone(zoneRepository.save(zone));
    }

    @Transactional
    public WarehouseZoneResponse updateZone(Long id, WarehouseZoneRequest req) {
        WarehouseZone zone = findZone(id);
        Branch branch = branchRepository.findById(req.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRANCH_NOT_FOUND, "Chi nhánh", "id", req.getBranchId()));

        zone.setZoneCode(req.getZoneCode());
        zone.setZoneName(req.getZoneName());
        zone.setConditions(req.getConditions());
        zone.setBranch(branch);
        zone.setUpdatedBy(getCurrentUser());
        return mapZone(zoneRepository.save(zone));
    }

    @Transactional
    public void deleteZone(Long id) {
        WarehouseZone zone = findZone(id);
        zone.setIsDeleted(true);
        zone.setDeletedAt(LocalDateTime.now());
        zone.setDeletedBy(getCurrentUser());
        zoneRepository.save(zone);
    }

    // ======================== BIN ========================

    public List<WarehouseBinResponse> getAllBins() {
        return binRepository.findAll().stream()
                .filter(b -> Boolean.FALSE.equals(b.getIsDeleted()))
                .map(this::mapBin).collect(Collectors.toList());
    }

    public WarehouseBinResponse getBinById(Long id) {
        return mapBin(findBin(id));
    }

    @Transactional
    public WarehouseBinResponse createBin(WarehouseBinRequest req) {
        if (binRepository.findByBinCodeAndIsDeletedFalse(req.getBinCode()).isPresent()) {
            throw new DuplicateResourceException("Ô/kệ kho", "binCode", req.getBinCode());
        }
        WarehouseZone zone = findZone(req.getZoneId());

        WarehouseBin bin = WarehouseBin.builder()
                .binCode(req.getBinCode())
                .barcode(req.getBarcode())
                .maxCapacity(req.getMaxCapacity())
                .zone(zone)
                .build();
        bin.setCreatedBy(getCurrentUser());
        return mapBin(binRepository.save(bin));
    }

    @Transactional
    public WarehouseBinResponse updateBin(Long id, WarehouseBinRequest req) {
        WarehouseBin bin = findBin(id);
        WarehouseZone zone = findZone(req.getZoneId());

        bin.setBinCode(req.getBinCode());
        bin.setBarcode(req.getBarcode());
        bin.setMaxCapacity(req.getMaxCapacity());
        bin.setZone(zone);
        bin.setUpdatedBy(getCurrentUser());
        return mapBin(binRepository.save(bin));
    }

    @Transactional
    public void deleteBin(Long id) {
        WarehouseBin bin = findBin(id);
        bin.setIsDeleted(true);
        bin.setDeletedAt(LocalDateTime.now());
        bin.setDeletedBy(getCurrentUser());
        binRepository.save(bin);
    }

    // ======================== HELPERS ========================

    private WarehouseZone findZone(Long id) {
        return zoneRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Khu vực kho", "id", id));
    }

    private WarehouseBin findBin(Long id) {
        return binRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BIN_NOT_FOUND, "Ô/kệ kho", "id", id));
    }

    private WarehouseZoneResponse mapZone(WarehouseZone z) {
        return WarehouseZoneResponse.builder()
                .id(z.getId())
                .zoneCode(z.getZoneCode())
                .zoneName(z.getZoneName())
                .conditions(z.getConditions())
                .branchId(z.getBranch() != null ? z.getBranch().getId() : null)
                .branchName(z.getBranch() != null ? z.getBranch().getBranchName() : null)
                .createdBy(z.getCreatedBy())
                .createdAt(z.getCreatedAt())
                .updatedBy(z.getUpdatedBy())
                .updatedAt(z.getUpdatedAt())
                .build();
    }

    private WarehouseBinResponse mapBin(WarehouseBin b) {
        return WarehouseBinResponse.builder()
                .id(b.getId())
                .binCode(b.getBinCode())
                .barcode(b.getBarcode())
                .maxCapacity(b.getMaxCapacity())
                .zoneId(b.getZone() != null ? b.getZone().getId() : null)
                .zoneCode(b.getZone() != null ? b.getZone().getZoneCode() : null)
                .zoneName(b.getZone() != null ? b.getZone().getZoneName() : null)
                .createdBy(b.getCreatedBy())
                .createdAt(b.getCreatedAt())
                .updatedBy(b.getUpdatedBy())
                .updatedAt(b.getUpdatedAt())
                .build();
    }
}
