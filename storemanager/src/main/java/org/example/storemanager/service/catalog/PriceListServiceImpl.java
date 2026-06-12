package org.example.storemanager.service.catalog;

import org.example.storemanager.dto.request.catalog.PriceListRequest;
import org.example.storemanager.dto.response.catalog.PriceListResponse;
import org.example.storemanager.entity.catalog.PriceList;
import org.example.storemanager.entity.catalog.PriceListDetail;
import org.example.storemanager.entity.catalog.Product;
import org.example.storemanager.entity.system.Branch;
import org.example.storemanager.enums.ErrorCode;
import org.example.storemanager.exception.DuplicateResourceException;
import org.example.storemanager.exception.ResourceNotFoundException;
import org.example.storemanager.repository.catalog.PriceListDetailRepository;
import org.example.storemanager.repository.catalog.PriceListRepository;
import org.example.storemanager.repository.catalog.ProductRepository;
import org.example.storemanager.repository.system.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceListServiceImpl implements PriceListService {

    private final PriceListRepository priceListRepository;
    private final PriceListDetailRepository priceListDetailRepository;
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;

    @Autowired
    public PriceListServiceImpl(PriceListRepository priceListRepository,
                                PriceListDetailRepository priceListDetailRepository,
                                ProductRepository productRepository,
                                BranchRepository branchRepository) {
        this.priceListRepository = priceListRepository;
        this.priceListDetailRepository = priceListDetailRepository;
        this.productRepository = productRepository;
        this.branchRepository = branchRepository;
    }

    private String getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        }
        return "system";
    }

    @Override
    public List<PriceListResponse> getActivePriceLists(Long branchId, Boolean isActive) {
        return priceListRepository.findActivePriceLists(branchId, isActive, LocalDateTime.now())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PriceListResponse getPriceListById(Long id) {
        PriceList priceList = priceListRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRICE_LIST_NOT_FOUND, "Bảng giá", "id", id));
        return mapToResponse(priceList);
    }

    @Override
    @Transactional
    public PriceListResponse createPriceList(PriceListRequest request) {
        if (priceListRepository.existsByListCodeAndIsDeletedFalse(request.getListCode())) {
            throw new DuplicateResourceException("Bảng giá", "listCode", request.getListCode());
        }

        Branch branch = null;
        if (request.getBranchId() != null) {
            branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRANCH_NOT_FOUND, "Chi nhánh", "id", request.getBranchId()));
        }

        PriceList priceList = PriceList.builder()
                .listCode(request.getListCode())
                .listName(request.getListName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .branch(branch)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
        priceList.setCreatedBy(getCurrentUser());
        PriceList savedPriceList = priceListRepository.save(priceList);

        if (request.getDetails() != null) {
            for (PriceListRequest.PriceListDetailRequest dReq : request.getDetails()) {
                Product product = productRepository.findByIdAndIsDeletedFalse(dReq.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm trong bảng giá", "id", dReq.getProductId()));

                PriceListDetail detail = PriceListDetail.builder()
                        .priceList(savedPriceList)
                        .product(product)
                        .price(dReq.getPrice())
                        .build();
                detail.setCreatedBy(getCurrentUser());
                priceListDetailRepository.save(detail);
            }
        }

        return mapToResponse(savedPriceList);
    }

    @Override
    @Transactional
    public PriceListResponse updatePriceList(Long id, PriceListRequest request) {
        PriceList priceList = priceListRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRICE_LIST_NOT_FOUND, "Bảng giá", "id", id));

        if (!priceList.getListCode().equals(request.getListCode()) &&
                priceListRepository.existsByListCodeAndIsDeletedFalse(request.getListCode())) {
            throw new DuplicateResourceException("Bảng giá", "listCode", request.getListCode());
        }

        Branch branch = null;
        if (request.getBranchId() != null) {
            branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRANCH_NOT_FOUND, "Chi nhánh", "id", request.getBranchId()));
        }

        priceList.setListCode(request.getListCode());
        priceList.setListName(request.getListName());
        priceList.setStartDate(request.getStartDate());
        priceList.setEndDate(request.getEndDate());
        priceList.setBranch(branch);
        if (request.getIsActive() != null) {
            priceList.setIsActive(request.getIsActive());
        }
        priceList.setUpdatedBy(getCurrentUser());
        PriceList savedPriceList = priceListRepository.save(priceList);

        // Xóa chi tiết cũ và thêm lại
        priceListDetailRepository.deleteAllByPriceListId(id);

        if (request.getDetails() != null) {
            for (PriceListRequest.PriceListDetailRequest dReq : request.getDetails()) {
                Product product = productRepository.findByIdAndIsDeletedFalse(dReq.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm trong bảng giá", "id", dReq.getProductId()));

                PriceListDetail detail = PriceListDetail.builder()
                        .priceList(savedPriceList)
                        .product(product)
                        .price(dReq.getPrice())
                        .build();
                detail.setCreatedBy(getCurrentUser());
                priceListDetailRepository.save(detail);
            }
        }

        return mapToResponse(savedPriceList);
    }

    @Override
    @Transactional
    public void deletePriceList(Long id) {
        PriceList priceList = priceListRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRICE_LIST_NOT_FOUND, "Bảng giá", "id", id));

        if (priceList.getIsActive() != null && priceList.getIsActive()) {
            throw new org.example.storemanager.exception.BusinessException(ErrorCode.BUSINESS_ERROR, "Không thể xóa bảng giá đang hoạt động. Vui lòng chuyển trạng thái hoạt động (isActive) sang false trước khi xóa.");
        }

        // Soft delete PriceList
        priceList.setIsDeleted(true);
        priceList.setDeletedAt(LocalDateTime.now());
        priceList.setDeletedBy(getCurrentUser());
        priceListRepository.save(priceList);

        // Soft delete PriceList Details
        List<PriceListDetail> details = priceListDetailRepository.findAllByPriceListIdAndIsDeletedFalse(id);
        for (PriceListDetail detail : details) {
            detail.setIsDeleted(true);
            detail.setDeletedAt(LocalDateTime.now());
            detail.setDeletedBy(getCurrentUser());
            priceListDetailRepository.save(detail);
        }
    }

    @Override
    public List<PriceListResponse> getSoftDeletedPriceLists() {
        return priceListRepository.findAllByIsDeletedTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PriceListResponse mapToResponse(PriceList priceList) {
        if (priceList == null) return null;

        List<PriceListDetail> details = priceListDetailRepository.findAllByPriceListIdAndIsDeletedFalse(priceList.getId());
        List<PriceListResponse.PriceListDetailResponse> detailResponses = details.stream()
                .map(d -> PriceListResponse.PriceListDetailResponse.builder()
                        .id(d.getId())
                        .productId(d.getProduct().getId())
                        .productName(d.getProduct().getName())
                        .productCode(d.getProduct().getProductCode())
                        .price(d.getPrice())
                        .build())
                .collect(Collectors.toList());

        return PriceListResponse.builder()
                .id(priceList.getId())
                .listCode(priceList.getListCode())
                .listName(priceList.getListName())
                .startDate(priceList.getStartDate())
                .endDate(priceList.getEndDate())
                .isActive(priceList.getIsActive())
                .branchId(priceList.getBranch() != null ? priceList.getBranch().getId() : null)
                .branchName(priceList.getBranch() != null ? priceList.getBranch().getBranchName() : null)
                .deletedAt(priceList.getDeletedAt())
                .deletedBy(priceList.getDeletedBy())
                .createdAt(priceList.getCreatedAt())
                .createdBy(priceList.getCreatedBy())
                .updatedAt(priceList.getUpdatedAt())
                .updatedBy(priceList.getUpdatedBy())
                .details(detailResponses)
                .build();
    }
}
